import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProfileGenerateTasksComponent } from './profile-generate-tasks.component';

describe('Profile Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProfileGenerateTasksComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ProfileGenerateTasksComponent,
              resolve: { profile: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(ProfileGenerateTasksComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load profile on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ProfileGenerateTasksComponent);

      // THEN
      expect(instance.profile).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
