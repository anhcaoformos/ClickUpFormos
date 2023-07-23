import { IUser } from 'app/entities/user/user.model';

export interface IProfile {
  id: number;
  name?: string | null;
  username?: string | null;
  password?: string | null;
  apiKey?: string | null;
  baseUrl?: string | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewProfile = Omit<IProfile, 'id'> & { id: null };
