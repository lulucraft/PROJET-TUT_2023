import { HttpClient, HttpParams } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../models/user';
import { AuthService } from './auth.service';
import { File } from '../models/file';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  constructor(private http: HttpClient, @Inject('API_BASE_URL') private apiBaseUrl: string, private authService: AuthService) { }

  // USER
  getNewUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiBaseUrl + 'api/user/newusers');
  }

  getFiles(): Observable<File[]> {
    return this.http.get<File[]>(this.apiBaseUrl + 'api/user/files');
  }

  setDarkMode(darkMode: boolean): Observable<any> {
    return this.http.post(this.apiBaseUrl + 'api/user/darkmode', darkMode);
  }

  downloadFile(file: File) {
    this.downloadFileFromData(file.data);
  }


  // ADMIN
  editFile(file: File): Observable<any> {
    return this.http.post(this.apiBaseUrl + 'api/admin/editfile', file);
  }

  deleteFile(fileName: string): Observable<any> {
    return this.http.post(this.apiBaseUrl + 'api/admin/deletefile', fileName);
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


  downloadFileFromData(data: BlobPart) {
    const blob = new Blob([data]);//, { type: 'text/csv' }
    const url= window.URL.createObjectURL(blob);
    window.open(url);
  }
}
