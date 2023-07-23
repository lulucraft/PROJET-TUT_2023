export interface File {
  name: string;
  creationDate: Date;
  modificationDate: Date;
  data: BlobPart;
}
