import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DownloadHistoryDetailComponent } from './download-history-detail.component';

describe('DownloadHistory Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DownloadHistoryDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: DownloadHistoryDetailComponent,
              resolve: { downloadHistory: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(DownloadHistoryDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load downloadHistory on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DownloadHistoryDetailComponent);

      // THEN
      expect(instance.downloadHistory).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
