import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../download-history.test-samples';

import { DownloadHistoryFormService } from './download-history-form.service';

describe('DownloadHistory Form Service', () => {
  let service: DownloadHistoryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DownloadHistoryFormService);
  });

  describe('Service methods', () => {
    describe('createDownloadHistoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDownloadHistoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            taskId: expect.any(Object),
            historyId: expect.any(Object),
            profile: expect.any(Object),
          })
        );
      });

      it('passing IDownloadHistory should create a new form with FormGroup', () => {
        const formGroup = service.createDownloadHistoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            taskId: expect.any(Object),
            historyId: expect.any(Object),
            profile: expect.any(Object),
          })
        );
      });
    });

    describe('getDownloadHistory', () => {
      it('should return NewDownloadHistory for default DownloadHistory initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createDownloadHistoryFormGroup(sampleWithNewData);

        const downloadHistory = service.getDownloadHistory(formGroup) as any;

        expect(downloadHistory).toMatchObject(sampleWithNewData);
      });

      it('should return NewDownloadHistory for empty DownloadHistory initial value', () => {
        const formGroup = service.createDownloadHistoryFormGroup();

        const downloadHistory = service.getDownloadHistory(formGroup) as any;

        expect(downloadHistory).toMatchObject({});
      });

      it('should return IDownloadHistory', () => {
        const formGroup = service.createDownloadHistoryFormGroup(sampleWithRequiredData);

        const downloadHistory = service.getDownloadHistory(formGroup) as any;

        expect(downloadHistory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDownloadHistory should not enable id FormControl', () => {
        const formGroup = service.createDownloadHistoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDownloadHistory should disable id FormControl', () => {
        const formGroup = service.createDownloadHistoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
