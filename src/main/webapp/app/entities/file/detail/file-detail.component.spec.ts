import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { FileDetailComponent } from './file-detail.component';

describe('File Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FileDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: FileDetailComponent,
              resolve: { file: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(FileDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load file on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', FileDetailComponent);

      // THEN
      expect(instance.file).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
