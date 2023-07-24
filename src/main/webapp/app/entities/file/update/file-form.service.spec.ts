import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../file.test-samples';

import { FileFormService } from './file-form.service';

describe('File Form Service', () => {
  let service: FileFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FileFormService);
  });

  describe('Service methods', () => {
    describe('createFileFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFileFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            fileOnServer: expect.any(Object),
            relativePath: expect.any(Object),
            downloadHistory: expect.any(Object),
          })
        );
      });

      it('passing IFile should create a new form with FormGroup', () => {
        const formGroup = service.createFileFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            fileOnServer: expect.any(Object),
            relativePath: expect.any(Object),
            downloadHistory: expect.any(Object),
          })
        );
      });
    });

    describe('getFile', () => {
      it('should return NewFile for default File initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createFileFormGroup(sampleWithNewData);

        const file = service.getFile(formGroup) as any;

        expect(file).toMatchObject(sampleWithNewData);
      });

      it('should return NewFile for empty File initial value', () => {
        const formGroup = service.createFileFormGroup();

        const file = service.getFile(formGroup) as any;

        expect(file).toMatchObject({});
      });

      it('should return IFile', () => {
        const formGroup = service.createFileFormGroup(sampleWithRequiredData);

        const file = service.getFile(formGroup) as any;

        expect(file).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFile should not enable id FormControl', () => {
        const formGroup = service.createFileFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFile should disable id FormControl', () => {
        const formGroup = service.createFileFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
