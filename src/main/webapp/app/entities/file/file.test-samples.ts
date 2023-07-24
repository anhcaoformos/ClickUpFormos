import { IFile, NewFile } from './file.model';

export const sampleWithRequiredData: IFile = {
  id: 19795,
};

export const sampleWithPartialData: IFile = {
  id: 710,
  name: 'consectetur feed',
};

export const sampleWithFullData: IFile = {
  id: 9994,
  name: 'per Dakota',
  fileOnServer: 'payment',
  relativePath: 'Granite',
};

export const sampleWithNewData: NewFile = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
