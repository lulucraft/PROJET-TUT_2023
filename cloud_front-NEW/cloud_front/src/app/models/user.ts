import { JWTToken } from "./jwt-token";
import { Role } from "./role";

export interface User {
  username: string;
  password: string;
  creationDate?: Date;
  roles?: Role[];
  id?: number;
  token?: JWTToken;
  darkModeEnabled?: boolean | true;
  accountActive?: boolean;
}
