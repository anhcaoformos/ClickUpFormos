import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DownloadHistoryFormService } from './download-history-form.service';
import { DownloadHistoryService } from '../service/download-history.service';
import { IDownloadHistory } from '../download-history.model';
import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';

import { DownloadHistoryUpdateComponent } from './download-history-update.component';

describe('DownloadHistory Management Update Component', () => {
  let comp: DownloadHistoryUpdateComponent;
  let fixture: ComponentFixture<DownloadHistoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let downloadHistoryFormService: DownloadHistoryFormService;
  let downloadHistoryService: DownloadHistoryService;
  let profileService: ProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), DownloadHistoryUpdateComponent],
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
      .overrideTemplate(DownloadHistoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DownloadHistoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    downloadHistoryFormService = TestBed.inject(DownloadHistoryFormService);
    downloadHistoryService = TestBed.inject(DownloadHistoryService);
    profileService = TestBed.inject(ProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Profile query and add missing value', () => {
      const downloadHistory: IDownloadHistory = { id: 456 };
      const profile: IProfile = { id: 21514 };
      downloadHistory.profile = profile;

      const profileCollection: IProfile[] = [{ id: 1800 }];
      jest.spyOn(profileService, 'query').mockReturnValue(of(new HttpResponse({ body: profileCollection })));
      const additionalProfiles = [profile];
      const expectedCollection: IProfile[] = [...additionalProfiles, ...profileCollection];
      jest.spyOn(profileService, 'addProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ downloadHistory });
      comp.ngOnInit();

      expect(profileService.query).toHaveBeenCalled();
      expect(profileService.addProfileToCollectionIfMissing).toHaveBeenCalledWith(
        profileCollection,
        ...additionalProfiles.map(expect.objectContaining)
      );
      expect(comp.profilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const downloadHistory: IDownloadHistory = { id: 456 };
      const profile: IProfile = { id: 11782 };
      downloadHistory.profile = profile;

      activatedRoute.data = of({ downloadHistory });
      comp.ngOnInit();

      expect(comp.profilesSharedCollection).toContain(profile);
      expect(comp.downloadHistory).toEqual(downloadHistory);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDownloadHistory>>();
      const downloadHistory = { id: 123 };
      jest.spyOn(downloadHistoryFormService, 'getDownloadHistory').mockReturnValue(downloadHistory);
      jest.spyOn(downloadHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ downloadHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: downloadHistory }));
      saveSubject.complete();

      // THEN
      expect(downloadHistoryFormService.getDownloadHistory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(downloadHistoryService.update).toHaveBeenCalledWith(expect.objectContaining(downloadHistory));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDownloadHistory>>();
      const downloadHistory = { id: 123 };
      jest.spyOn(downloadHistoryFormService, 'getDownloadHistory').mockReturnValue({ id: null });
      jest.spyOn(downloadHistoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ downloadHistory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: downloadHistory }));
      saveSubject.complete();

      // THEN
      expect(downloadHistoryFormService.getDownloadHistory).toHaveBeenCalled();
      expect(downloadHistoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDownloadHistory>>();
      const downloadHistory = { id: 123 };
      jest.spyOn(downloadHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ downloadHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(downloadHistoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProfile', () => {
      it('Should forward to profileService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(profileService, 'compareProfile');
        comp.compareProfile(entity, entity2);
        expect(profileService.compareProfile).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
