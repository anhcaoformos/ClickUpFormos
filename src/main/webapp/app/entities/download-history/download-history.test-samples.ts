import { IDownloadHistory, NewDownloadHistory } from './download-history.model';

export const sampleWithRequiredData: IDownloadHistory = {
  id: 29741,
};

export const sampleWithPartialData: IDownloadHistory = {
  id: 11660,
  taskId: 'dynamic',
  historyId: 'Operations Rial Developer',
};

export const sampleWithFullData: IDownloadHistory = {
  id: 5107,
  taskId: 'Cobalt killer male',
  historyId: 'bandwidth Brand',
};

export const sampleWithNewData: NewDownloadHistory = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
