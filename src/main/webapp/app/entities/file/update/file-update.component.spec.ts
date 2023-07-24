import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FileFormService } from './file-form.service';
import { FileService } from '../service/file.service';
import { IFile } from '../file.model';
import { IDownloadHistory } from 'app/entities/download-history/download-history.model';
import { DownloadHistoryService } from 'app/entities/download-history/service/download-history.service';

import { FileUpdateComponent } from './file-update.component';

describe('File Management Update Component', () => {
  let comp: FileUpdateComponent;
  let fixture: ComponentFixture<FileUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let fileFormService: FileFormService;
  let fileService: FileService;
  let downloadHistoryService: DownloadHistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), FileUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(FileUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FileUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fileFormService = TestBed.inject(FileFormService);
    fileService = TestBed.inject(FileService);
    downloadHistoryService = TestBed.inject(DownloadHistoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call DownloadHistory query and add missing value', () => {
      const file: IFile = { id: 456 };
      const downloadHistory: IDownloadHistory = { id: 14055 };
      file.downloadHistory = downloadHistory;

      const downloadHistoryCollection: IDownloadHistory[] = [{ id: 8790 }];
      jest.spyOn(downloadHistoryService, 'query').mockReturnValue(of(new HttpResponse({ body: downloadHistoryCollection })));
      const additionalDownloadHistories = [downloadHistory];
      const expectedCollection: IDownloadHistory[] = [...additionalDownloadHistories, ...downloadHistoryCollection];
      jest.spyOn(downloadHistoryService, 'addDownloadHistoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ file });
      comp.ngOnInit();

      expect(downloadHistoryService.query).toHaveBeenCalled();
      expect(downloadHistoryService.addDownloadHistoryToCollectionIfMissing).toHaveBeenCalledWith(
        downloadHistoryCollection,
        ...additionalDownloadHistories.map(expect.objectContaining)
      );
      expect(comp.downloadHistoriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const file: IFile = { id: 456 };
      const downloadHistory: IDownloadHistory = { id: 6785 };
      file.downloadHistory = downloadHistory;

      activatedRoute.data = of({ file });
      comp.ngOnInit();

      expect(comp.downloadHistoriesSharedCollection).toContain(downloadHistory);
      expect(comp.file).toEqual(file);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFile>>();
      const file = { id: 123 };
      jest.spyOn(fileFormService, 'getFile').mockReturnValue(file);
      jest.spyOn(fileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ file });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: file }));
      saveSubject.complete();

      // THEN
      expect(fileFormService.getFile).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(fileService.update).toHaveBeenCalledWith(expect.objectContaining(file));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFile>>();
      const file = { id: 123 };
      jest.spyOn(fileFormService, 'getFile').mockReturnValue({ id: null });
      jest.spyOn(fileService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ file: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: file }));
      saveSubject.complete();

      // THEN
      expect(fileFormService.getFile).toHaveBeenCalled();
      expect(fileService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFile>>();
      const file = { id: 123 };
      jest.spyOn(fileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ file });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(fileService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDownloadHistory', () => {
      it('Should forward to downloadHistoryService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(downloadHistoryService, 'compareDownloadHistory');
        comp.compareDownloadHistory(entity, entity2);
        expect(downloadHistoryService.compareDownloadHistory).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
