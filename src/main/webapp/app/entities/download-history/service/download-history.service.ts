import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDownloadHistory, NewDownloadHistory } from '../download-history.model';

export type PartialUpdateDownloadHistory = Partial<IDownloadHistory> & Pick<IDownloadHistory, 'id'>;

export type EntityResponseType = HttpResponse<IDownloadHistory>;
export type EntityArrayResponseType = HttpResponse<IDownloadHistory[]>;

@Injectable({ providedIn: 'root' })
export class DownloadHistoryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/download-histories');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(downloadHistory: NewDownloadHistory): Observable<EntityResponseType> {
    return this.http.post<IDownloadHistory>(this.resourceUrl, downloadHistory, { observe: 'response' });
  }

  update(downloadHistory: IDownloadHistory): Observable<EntityResponseType> {
    return this.http.put<IDownloadHistory>(`${this.resourceUrl}/${this.getDownloadHistoryIdentifier(downloadHistory)}`, downloadHistory, {
      observe: 'response',
    });
  }

  partialUpdate(downloadHistory: PartialUpdateDownloadHistory): Observable<EntityResponseType> {
    return this.http.patch<IDownloadHistory>(`${this.resourceUrl}/${this.getDownloadHistoryIdentifier(downloadHistory)}`, downloadHistory, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDownloadHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDownloadHistory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  findAllByProfile(profileId: string, req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDownloadHistory[]>(`${this.resourceUrl}/profile/${profileId}`, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDownloadHistoryIdentifier(downloadHistory: Pick<IDownloadHistory, 'id'>): number {
    return downloadHistory.id;
  }

  compareDownloadHistory(o1: Pick<IDownloadHistory, 'id'> | null, o2: Pick<IDownloadHistory, 'id'> | null): boolean {
    return o1 && o2 ? this.getDownloadHistoryIdentifier(o1) === this.getDownloadHistoryIdentifier(o2) : o1 === o2;
  }

  addDownloadHistoryToCollectionIfMissing<Type extends Pick<IDownloadHistory, 'id'>>(
    downloadHistoryCollection: Type[],
    ...downloadHistoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const downloadHistories: Type[] = downloadHistoriesToCheck.filter(isPresent);
    if (downloadHistories.length > 0) {
      const downloadHistoryCollectionIdentifiers = downloadHistoryCollection.map(
        downloadHistoryItem => this.getDownloadHistoryIdentifier(downloadHistoryItem)!
      );
      const downloadHistoriesToAdd = downloadHistories.filter(downloadHistoryItem => {
        const downloadHistoryIdentifier = this.getDownloadHistoryIdentifier(downloadHistoryItem);
        if (downloadHistoryCollectionIdentifiers.includes(downloadHistoryIdentifier)) {
          return false;
        }
        downloadHistoryCollectionIdentifiers.push(downloadHistoryIdentifier);
        return true;
      });
      return [...downloadHistoriesToAdd, ...downloadHistoryCollection];
    }
    return downloadHistoryCollection;
  }
}
