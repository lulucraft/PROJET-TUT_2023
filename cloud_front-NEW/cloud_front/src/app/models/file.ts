export interface File {
  id?: number;
  name: string;
  creationDate: Date;
  modificationDate?: Date;
  size: number;
  hash?: string;
}
