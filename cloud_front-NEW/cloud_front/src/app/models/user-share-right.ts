import { Right } from "./right";
import { User } from "./user";

export interface UserShareRight {
  id: number;
  user: User;
  rights: Right[];
}
