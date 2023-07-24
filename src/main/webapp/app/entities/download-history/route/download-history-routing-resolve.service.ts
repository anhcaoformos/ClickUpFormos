import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDownloadHistory } from '../download-history.model';
import { DownloadHistoryService } from '../service/download-history.service';

export const downloadHistoryResolve = (route: ActivatedRouteSnapshot): Observable<null | IDownloadHistory> => {
  const id = route.params['id'];
  if (id) {
    return inject(DownloadHistoryService)
      .find(id)
      .pipe(
        mergeMap((downloadHistory: HttpResponse<IDownloadHistory>) => {
          if (downloadHistory.body) {
            return of(downloadHistory.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default downloadHistoryResolve;
