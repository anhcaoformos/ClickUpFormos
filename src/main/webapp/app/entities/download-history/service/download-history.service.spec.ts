import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDownloadHistory } from '../download-history.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../download-history.test-samples';

import { DownloadHistoryService } from './download-history.service';

const requireRestSample: IDownloadHistory = {
  ...sampleWithRequiredData,
};

describe('DownloadHistory Service', () => {
  let service: DownloadHistoryService;
  let httpMock: HttpTestingController;
  let expectedResult: IDownloadHistory | IDownloadHistory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DownloadHistoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a DownloadHistory', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const downloadHistory = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(downloadHistory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DownloadHistory', () => {
      const downloadHistory = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(downloadHistory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DownloadHistory', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DownloadHistory', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DownloadHistory', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDownloadHistoryToCollectionIfMissing', () => {
      it('should add a DownloadHistory to an empty array', () => {
        const downloadHistory: IDownloadHistory = sampleWithRequiredData;
        expectedResult = service.addDownloadHistoryToCollectionIfMissing([], downloadHistory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(downloadHistory);
      });

      it('should not add a DownloadHistory to an array that contains it', () => {
        const downloadHistory: IDownloadHistory = sampleWithRequiredData;
        const downloadHistoryCollection: IDownloadHistory[] = [
          {
            ...downloadHistory,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDownloadHistoryToCollectionIfMissing(downloadHistoryCollection, downloadHistory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DownloadHistory to an array that doesn't contain it", () => {
        const downloadHistory: IDownloadHistory = sampleWithRequiredData;
        const downloadHistoryCollection: IDownloadHistory[] = [sampleWithPartialData];
        expectedResult = service.addDownloadHistoryToCollectionIfMissing(downloadHistoryCollection, downloadHistory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(downloadHistory);
      });

      it('should add only unique DownloadHistory to an array', () => {
        const downloadHistoryArray: IDownloadHistory[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const downloadHistoryCollection: IDownloadHistory[] = [sampleWithRequiredData];
        expectedResult = service.addDownloadHistoryToCollectionIfMissing(downloadHistoryCollection, ...downloadHistoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const downloadHistory: IDownloadHistory = sampleWithRequiredData;
        const downloadHistory2: IDownloadHistory = sampleWithPartialData;
        expectedResult = service.addDownloadHistoryToCollectionIfMissing([], downloadHistory, downloadHistory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(downloadHistory);
        expect(expectedResult).toContain(downloadHistory2);
      });

      it('should accept null and undefined values', () => {
        const downloadHistory: IDownloadHistory = sampleWithRequiredData;
        expectedResult = service.addDownloadHistoryToCollectionIfMissing([], null, downloadHistory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(downloadHistory);
      });

      it('should return initial array if no DownloadHistory is added', () => {
        const downloadHistoryCollection: IDownloadHistory[] = [sampleWithRequiredData];
        expectedResult = service.addDownloadHistoryToCollectionIfMissing(downloadHistoryCollection, undefined, null);
        expect(expectedResult).toEqual(downloadHistoryCollection);
      });
    });

    describe('compareDownloadHistory', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDownloadHistory(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDownloadHistory(entity1, entity2);
        const compareResult2 = service.compareDownloadHistory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDownloadHistory(entity1, entity2);
        const compareResult2 = service.compareDownloadHistory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDownloadHistory(entity1, entity2);
        const compareResult2 = service.compareDownloadHistory(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
