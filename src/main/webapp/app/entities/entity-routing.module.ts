import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'profile',
        data: { pageTitle: 'clickupApp.profile.home.title' },
        loadChildren: () => import('./profile/profile.routes'),
      },
      {
        path: 'download-history',
        data: { pageTitle: 'clickupApp.downloadHistory.home.title' },
        loadChildren: () => import('./download-history/download-history.routes'),
      },
      {
        path: 'file',
        data: { pageTitle: 'clickupApp.file.home.title' },
        loadChildren: () => import('./file/file.routes'),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
