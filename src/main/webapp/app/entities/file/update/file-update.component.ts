import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { FileFormService, FileFormGroup } from './file-form.service';
import { IFile } from '../file.model';
import { FileService } from '../service/file.service';
import { IDownloadHistory } from 'app/entities/download-history/download-history.model';
import { DownloadHistoryService } from 'app/entities/download-history/service/download-history.service';

@Component({
  standalone: true,
  selector: 'jhi-file-update',
  templateUrl: './file-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class FileUpdateComponent implements OnInit {
  isSaving = false;
  file: IFile | null = null;

  downloadHistoriesSharedCollection: IDownloadHistory[] = [];

  editForm: FileFormGroup = this.fileFormService.createFileFormGroup();

  constructor(
    protected fileService: FileService,
    protected fileFormService: FileFormService,
    protected downloadHistoryService: DownloadHistoryService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareDownloadHistory = (o1: IDownloadHistory | null, o2: IDownloadHistory | null): boolean =>
    this.downloadHistoryService.compareDownloadHistory(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ file }) => {
      this.file = file;
      if (file) {
        this.updateForm(file);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const file = this.fileFormService.getFile(this.editForm);
    if (file.id !== null) {
      this.subscribeToSaveResponse(this.fileService.update(file));
    } else {
      this.subscribeToSaveResponse(this.fileService.create(file));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFile>>): void {
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

  protected updateForm(file: IFile): void {
    this.file = file;
    this.fileFormService.resetForm(this.editForm, file);

    this.downloadHistoriesSharedCollection = this.downloadHistoryService.addDownloadHistoryToCollectionIfMissing<IDownloadHistory>(
      this.downloadHistoriesSharedCollection,
      file.downloadHistory
    );
  }

  protected loadRelationshipsOptions(): void {
    this.downloadHistoryService
      .query()
      .pipe(map((res: HttpResponse<IDownloadHistory[]>) => res.body ?? []))
      .pipe(
        map((downloadHistories: IDownloadHistory[]) =>
          this.downloadHistoryService.addDownloadHistoryToCollectionIfMissing<IDownloadHistory>(
            downloadHistories,
            this.file?.downloadHistory
          )
        )
      )
      .subscribe((downloadHistories: IDownloadHistory[]) => (this.downloadHistoriesSharedCollection = downloadHistories));
  }
}
