import { NewsletterType } from "./newsletter-type";

export interface Newsletter {
  id?: number;
  title: string;
  text: string;
  type: NewsletterType;
}
