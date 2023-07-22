import { IProfile, NewProfile } from './profile.model';

export const sampleWithRequiredData: IProfile = {
  id: 21514,
};

export const sampleWithPartialData: IProfile = {
  id: 8712,
  name: 'International',
  username: 'Woman killer Hat',
  password: 'Rhode Bentley',
};

export const sampleWithFullData: IProfile = {
  id: 1720,
  name: 'quirk marginalise productivity',
  username: 'nemo Buckinghamshire 1080p',
  password: 'withdrawal',
  apiKey: 'Bicycle',
  token: 'barring Accounts',
  baseUrl: 'Pickup Guilder Hybrid',
};

export const sampleWithNewData: NewProfile = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
