import { JWTToken } from "./jwt-token";
import { Offer } from "./offer";
import { Role } from "./role";

export interface User {
  username: string;
  password: string;
  email?: string;
  firstname?: string;
  lastname?: string;
  address?: string;
  postalCode?: number;
  city?: string;
  country?: string;
  creationDate?: Date;
  roles?: Role[];
  id?: number;
  token?: JWTToken;
  darkModeEnabled?: boolean | true;
  accountActive?: boolean;
  offer?: string;
}
