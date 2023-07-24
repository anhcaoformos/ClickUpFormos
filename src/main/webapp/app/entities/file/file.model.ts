import { IDownloadHistory } from 'app/entities/download-history/download-history.model';

export interface IFile {
  id: number;
  name?: string | null;
  fileOnServer?: string | null;
  relativePath?: string | null;
  downloadHistory?: Pick<IDownloadHistory, 'id'> | null;
}

export type NewFile = Omit<IFile, 'id'> & { id: null };
