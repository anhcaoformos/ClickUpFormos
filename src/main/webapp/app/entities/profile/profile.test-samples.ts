import { IProfile, NewProfile } from './profile.model';

export const sampleWithRequiredData: IProfile = {
  id: 2845,
  name: 'Sedan vertical International',
  username: 'Woman killer Hat',
  password: 'Rhode Bentley',
  apiKey: 'worship',
  baseUrl: 'marginalise productivity Mountain',
};

export const sampleWithPartialData: IProfile = {
  id: 24413,
  name: 'Cyclocross Account',
  username: 'Intranet barring Accounts',
  password: 'Pickup Guilder Hybrid',
  apiKey: 'joule who Cambridgeshire',
  baseUrl: 'immense',
};

export const sampleWithFullData: IProfile = {
  id: 14167,
  name: 'Steel obediently',
  username: 'seize matrix',
  password: 'connecting earum hectare',
  apiKey: 'Chips',
  baseUrl: 'whoa clear',
};

export const sampleWithNewData: NewProfile = {
  name: 'Connecticut ea',
  username: 'Gasoline er',
  password: 'Chief',
  apiKey: 'Leone Buckinghamshire odio',
  baseUrl: 'qua freely Carolina',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
