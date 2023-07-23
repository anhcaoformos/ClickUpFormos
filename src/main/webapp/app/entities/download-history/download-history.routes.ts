import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DownloadHistoryComponent } from './list/download-history.component';
import { DownloadHistoryDetailComponent } from './detail/download-history-detail.component';
import { DownloadHistoryUpdateComponent } from './update/download-history-update.component';
import { DownloadHistoryByProfileComponent } from './list-by-profile/download-history-by-profile.component';
import DownloadHistoryResolve from './route/download-history-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const downloadHistoryRoute: Routes = [
  {
    path: '',
    component: DownloadHistoryComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DownloadHistoryDetailComponent,
    resolve: {
      downloadHistory: DownloadHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DownloadHistoryUpdateComponent,
    resolve: {
      downloadHistory: DownloadHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DownloadHistoryUpdateComponent,
    resolve: {
      downloadHistory: DownloadHistoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'profile/:profileId',
    component: DownloadHistoryByProfileComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default downloadHistoryRoute;
