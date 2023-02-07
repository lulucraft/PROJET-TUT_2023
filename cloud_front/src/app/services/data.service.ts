import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Conge } from '../models/conge';
import { Newsletter } from '../models/newsletter';
import { NewsletterType } from '../models/newsletter-type';
import { User } from '../models/user';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  constructor(private http: HttpClient, @Inject('API_BASE_URL') private apiBaseUrl: string, private authService: AuthService) { }

  // USER
  getCongesAcquis(): Observable<number> {
    let params: HttpParams = new HttpParams().set('username', this.authService.currentUserValue!.username);

    return this.http.get<number>(this.apiBaseUrl + 'api/user/congesacquis', { params: params });
  }

  getConges(): Observable<Conge[]> {
    let params: HttpParams = new HttpParams().set('username', this.authService.currentUserValue!.username);

    return this.http.get<Conge[]>(this.apiBaseUrl + 'api/user/conges', { params: params });
  }

  sendCongeRequest(conge: Conge): Observable<any> {
    return this.http.post(this.apiBaseUrl + 'api/user/congesrequest', conge);
  }

  deleteCongeRequest(congeId: number): Observable<string> {
    return this.http.post<string>(this.apiBaseUrl + 'api/user/deletecongesrequest', congeId);
  }

  getNewUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiBaseUrl + 'api/user/newusers');
  }

  getNewsletter(newsletterType: NewsletterType): Observable<Newsletter> {
    return this.http.get<Newsletter>(this.apiBaseUrl + 'api/user/newsletter', { params: new HttpParams().set('newsletterType', newsletterType.toString()) });
  }

  getNewsletters(): Observable<Newsletter[]> {
    return this.http.get<Newsletter[]>(this.apiBaseUrl + 'api/user/newsletters');
  }

  setDarkMode(darkMode: boolean): Observable<any> {
    return this.http.post(this.apiBaseUrl + 'api/user/darkmode', darkMode);
  }

  // ADMIN
  getCongesAdmin(): Observable<{user: Conge[]}> {
    return this.http.get<{user: Conge[]}>(this.apiBaseUrl + 'api/admin/conges');
  }

  sendCongeValidation(conge: Conge): Observable<any> {
    return this.http.post(this.apiBaseUrl + 'api/admin/validateconge', conge)
  }

  publishNewsletter(newsletter: Newsletter): Observable<any> {
    return this.http.post(this.apiBaseUrl + 'api/admin/editnewsletter', newsletter);
  }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiBaseUrl + 'api/admin/users');
  }

  getUser(userId: number): Observable<User> {
    return this.http.get<User>(this.apiBaseUrl + 'api/admin/user', { params: new HttpParams().set('userId', userId) });
  }

  editUser(user: User): Observable<any> {
    return this.http.post(this.apiBaseUrl + 'api/admin/edituser', user);
  }
}
