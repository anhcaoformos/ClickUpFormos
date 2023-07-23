import { IProfile } from 'app/entities/profile/profile.model';

export interface IDownloadHistory {
  id: number;
  taskId?: string | null;
  historyId?: string | null;
  profile?: Pick<IProfile, 'id'> | null;
}

export type NewDownloadHistory = Omit<IDownloadHistory, 'id'> & { id: null };
