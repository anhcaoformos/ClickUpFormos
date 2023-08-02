import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IDownloadHistory, NewDownloadHistory } from '../download-history.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDownloadHistory for edit and NewDownloadHistoryFormGroupInput for create.
 */
type DownloadHistoryFormGroupInput = IDownloadHistory | PartialWithRequiredKeyOf<NewDownloadHistory>;

type DownloadHistoryFormDefaults = Pick<NewDownloadHistory, 'id'>;

type DownloadHistoryFormGroupContent = {
  id: FormControl<IDownloadHistory['id'] | NewDownloadHistory['id']>;
  taskId: FormControl<IDownloadHistory['taskId']>;
  timestamp: FormControl<IDownloadHistory['timestamp']>;
  profile: FormControl<IDownloadHistory['profile']>;
};

export type DownloadHistoryFormGroup = FormGroup<DownloadHistoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DownloadHistoryFormService {
  createDownloadHistoryFormGroup(downloadHistory: DownloadHistoryFormGroupInput = { id: null }): DownloadHistoryFormGroup {
    const downloadHistoryRawValue = {
      ...this.getFormDefaults(),
      ...downloadHistory,
    };
    return new FormGroup<DownloadHistoryFormGroupContent>({
      id: new FormControl(
        { value: downloadHistoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      taskId: new FormControl(downloadHistoryRawValue.taskId),
      timestamp: new FormControl(downloadHistoryRawValue.timestamp),
      profile: new FormControl(downloadHistoryRawValue.profile),
    });
  }

  getDownloadHistory(form: DownloadHistoryFormGroup): IDownloadHistory | NewDownloadHistory {
    return form.getRawValue() as IDownloadHistory | NewDownloadHistory;
  }

  resetForm(form: DownloadHistoryFormGroup, downloadHistory: DownloadHistoryFormGroupInput): void {
    const downloadHistoryRawValue = { ...this.getFormDefaults(), ...downloadHistory };
    form.reset(
      {
        ...downloadHistoryRawValue,
        id: { value: downloadHistoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DownloadHistoryFormDefaults {
    return {
      id: null,
    };
  }
}
