import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DownloadHistoryService } from '../service/download-history.service';

import { DownloadHistoryComponent } from './download-history.component';

describe('DownloadHistory Management Component', () => {
  let comp: DownloadHistoryComponent;
  let fixture: ComponentFixture<DownloadHistoryComponent>;
  let service: DownloadHistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'download-history', component: DownloadHistoryComponent }]),
        HttpClientTestingModule,
        DownloadHistoryComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(DownloadHistoryComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DownloadHistoryComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(DownloadHistoryService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.downloadHistories?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to downloadHistoryService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getDownloadHistoryIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getDownloadHistoryIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
