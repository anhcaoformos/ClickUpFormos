import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Params, Router, RouterModule } from '@angular/router';
import { combineLatest, filter, Observable, switchMap, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortDirective, SortByDirective } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';
import { IDownloadHistory } from '../download-history.model';
import { ASC, DESC, SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { EntityArrayResponseType, DownloadHistoryService } from '../service/download-history.service';
import { DownloadHistoryDeleteDialogComponent } from '../delete/download-history-delete-dialog.component';
import { SortService } from 'app/shared/sort/sort.service';

@Component({
  standalone: true,
  selector: 'jhi-download-history-by-profile',
  templateUrl: './download-history-by-profile.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
  ],
})
export class DownloadHistoryByProfileComponent implements OnInit {
  downloadHistories?: IDownloadHistory[];
  isLoading = false;

  predicate = 'id';
  ascending = true;
  profileId = '';
  constructor(
    protected downloadHistoryService: DownloadHistoryService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
    protected sortService: SortService,
    protected modalService: NgbModal
  ) {}

  trackId = (_index: number, item: IDownloadHistory): number => this.downloadHistoryService.getDownloadHistoryIdentifier(item);

  ngOnInit(): void {
    this.load();
  }

  delete(downloadHistory: IDownloadHistory): void {
    const modalRef = this.modalService.open(DownloadHistoryDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.downloadHistory = downloadHistory;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        switchMap(() => this.loadFromBackendWithRouteInformations())
      )
      .subscribe({
        next: (res: EntityArrayResponseType) => {
          this.onResponseSuccess(res);
        },
      });
  }

  load(): void {
    this.loadFromBackendWithRouteInformations().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(): void {
    this.handleNavigation(this.predicate, this.ascending);
  }

  protected loadFromBackendWithRouteInformations(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.params, this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, queryParams, data]) => this.fillComponentAttributeFromRoute(params, queryParams, data)),
      switchMap(() => this.queryBackend(this.profileId, this.predicate, this.ascending))
    );
  }

  protected fillComponentAttributeFromRoute(params: Params, queryParams: ParamMap, data: Data): void {
    const sort = (queryParams.get(SORT) ?? data[DEFAULT_SORT_DATA]).split(',');
    this.predicate = sort[0];
    this.ascending = sort[1] === ASC;
    this.profileId = params['profileId'];
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.downloadHistories = this.refineData(dataFromBody);
  }

  protected refineData(data: IDownloadHistory[]): IDownloadHistory[] {
    return data.sort(this.sortService.startSort(this.predicate, this.ascending ? 1 : -1));
  }

  protected fillComponentAttributesFromResponseBody(data: IDownloadHistory[] | null): IDownloadHistory[] {
    return data ?? [];
  }

  protected queryBackend(profileId?: string, predicate?: string, ascending?: boolean): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      sort: this.getSortQueryParam(predicate, ascending),
    };
    return this.downloadHistoryService.findAllByProfile(this.profileId, queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(predicate?: string, ascending?: boolean): void {
    const queryParamsObj = {
      sort: this.getSortQueryParam(predicate, ascending),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }
}
