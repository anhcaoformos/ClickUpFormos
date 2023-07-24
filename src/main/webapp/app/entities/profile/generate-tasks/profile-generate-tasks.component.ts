import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IProfile } from '../profile.model';
import { FormGroup, FormControl, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';

import { ProfileFormGroup } from '../update/profile-form.service';
import { ProfileService } from '../service/profile.service';

@Component({
  standalone: true,
  selector: 'jhi-profile-generate-task',
  templateUrl: './profile-generate-tasks.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe, FormsModule, ReactiveFormsModule],
})
export class ProfileGenerateTasksComponent {
  @Input() profile: IProfile | null = null;
  generateTasksForm: FormGroup = new FormGroup({
    tasks: new FormControl<string>('', Validators.required),
  });

  constructor(protected activatedRoute: ActivatedRoute, protected profileService: ProfileService) {}

  previousState(): void {
    window.history.back();
  }

  generateTasks() {
    if (this.profile && this.profile.id) {
      const tasks = this.generateTasksForm.controls.tasks.value.split('\n');
      for (let index = 0; index < tasks.length; index++) {
        tasks[index] = tasks[index].trim();
      }
      console.log(this.generateTasksForm);
      console.log(tasks);
      this.profileService.generateTasks(this.profile, tasks).subscribe(data => {
        console.log(data);
      });
    }
  }
}
