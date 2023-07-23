import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { DownloadHistoryFormService, DownloadHistoryFormGroup } from './download-history-form.service';
import { IDownloadHistory } from '../download-history.model';
import { DownloadHistoryService } from '../service/download-history.service';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';

@Component({
  standalone: true,
  selector: 'jhi-download-history-update',
  templateUrl: './download-history-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DownloadHistoryUpdateComponent implements OnInit {
  isSaving = false;
  downloadHistory: IDownloadHistory | null = null;

  profilesSharedCollection: IProfile[] = [];

  editForm: DownloadHistoryFormGroup = this.downloadHistoryFormService.createDownloadHistoryFormGroup();

  constructor(
    protected downloadHistoryService: DownloadHistoryService,
    protected downloadHistoryFormService: DownloadHistoryFormService,
    protected profileService: ProfileService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProfile = (o1: IProfile | null, o2: IProfile | null): boolean => this.profileService.compareProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ downloadHistory }) => {
      this.downloadHistory = downloadHistory;
      if (downloadHistory) {
        this.updateForm(downloadHistory);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const downloadHistory = this.downloadHistoryFormService.getDownloadHistory(this.editForm);
    if (downloadHistory.id !== null) {
      this.subscribeToSaveResponse(this.downloadHistoryService.update(downloadHistory));
    } else {
      this.subscribeToSaveResponse(this.downloadHistoryService.create(downloadHistory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDownloadHistory>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(downloadHistory: IDownloadHistory): void {
    this.downloadHistory = downloadHistory;
    this.downloadHistoryFormService.resetForm(this.editForm, downloadHistory);

    this.profilesSharedCollection = this.profileService.addProfileToCollectionIfMissing<IProfile>(
      this.profilesSharedCollection,
      downloadHistory.profile
    );
  }

  protected loadRelationshipsOptions(): void {
    this.profileService
      .query()
      .pipe(map((res: HttpResponse<IProfile[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfile[]) =>
          this.profileService.addProfileToCollectionIfMissing<IProfile>(profiles, this.downloadHistory?.profile)
        )
      )
      .subscribe((profiles: IProfile[]) => (this.profilesSharedCollection = profiles));
  }
}
