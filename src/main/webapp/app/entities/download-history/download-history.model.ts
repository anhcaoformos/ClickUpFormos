import { IProfile } from 'app/entities/profile/profile.model';

export interface IDownloadHistory {
  id: number;
  taskId?: string | null;
  historyId?: string | null;
  profile?: IProfile | null;
}

export type NewDownloadHistory = Omit<IDownloadHistory, 'id'> & { id: null };
