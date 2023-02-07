import { Inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import jwt_decode from 'jwt-decode';
import { User } from '../models/user';
import { JWTToken } from '../models/jwt-token';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private currentUserSubject: BehaviorSubject<User | null>;
  private currentUser: Observable<User | null>;

  constructor(
    private http: HttpClient,
    @Inject('API_BASE_URL') private apiBaseUrl: string,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    let currentUser = localStorage.getItem('currentUser');
    let user = null;

    if (currentUser) {
      //currentUser = '{}';
      user = JSON.parse(currentUser);
    }

    this.currentUserSubject = new BehaviorSubject<User | null>(user);
    this.currentUser = this.currentUserSubject.asObservable();
  }

  // onTimeOut() {
  //   if (this.currentUserValue !== null) {
  //     if (this.isTokenExpired()) {
  //       this.logout();
  //     } else {
  //       setTimeout(() => {
  //         this.onTimeOut();
  //       }, 1000);
  //     }
  //   }
  // }

  register(user: User): void {
    this.http.post(this.apiBaseUrl + 'api/auth/register', user)
      .subscribe({
        next: (resp: any) => {
          if (!resp) {
            console.error('Error while registering');
            return;
          }

          this.login(user);
          this.snackBar.open('Votre compte a été créé', '', { duration: 2500, horizontalPosition: 'center', verticalPosition: 'top', panelClass: ['snack-bar-container'] });
        },
        error: (error) => {
          if (error.error) {// && error.error.message) {
            // if (error.error.message === "Ce nom d'utilisateur est déjà utilisé") {
            this.snackBar.open('Ce nom d\'utilisateur est déjà utilisé', '', { duration: 1500, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
            this.login(user);
            return;
            // } else {
            //   this.snackBar.open(error.error.message, '', { duration: 1500, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
            // }
          }
          console.error(error);
        }
      });
  }

  login(user: User, redirect: boolean = true): void {
    let params: HttpParams = new HttpParams()
      .set('username', user.username)
      .set('password', user.password);

    this.http.post<JWTToken>(this.apiBaseUrl + 'api/auth/login', null, { params: params })
      .subscribe({
        next: (resp: JWTToken) => {
          user.token = resp;
          // Reset password to hide it in localStorage
          user.password = '';

          let decodedToken: JWTToken | any;
          try {
            decodedToken = this.decodeToken(user.token);
          } catch (error) {
            this.snackBar.open('Vous n\'êtes pas autorisé à vous connecter', '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
            return;
          }

          // User roles
          user.roles = [];
          for (let role of decodedToken.roles) {//this.getUserRolesFromToken(user)
            user.roles.push({ name: role });
          }

          // User details
          if (decodedToken.creation_date) {
            user.creationDate = new Date(decodedToken.creation_date * 1000);
          }
          user.accountActive = decodedToken.account_active;

          // User preferences
          user.darkModeEnabled = decodedToken.dark_mode_enabled;

          localStorage.setItem('currentUser', JSON.stringify(user));
          this.currentUserSubject.next(user);
          console.log(resp['accessToken']);

          if (redirect) {
            if (this.route.snapshot.queryParams['returnUrl']) {
              // Redirect to redirectUrl
              this.router.navigateByUrl(this.route.snapshot.queryParams['returnUrl']);
              return;
            }

            if (!this.isUserAdmin()) {
              // Redirect to home page
              this.router.navigate(['/']);
            } else {
              this.router.navigate(['/admin']);
            }
          }
        },
        error: (error) => {
          console.error(error);
        }
      });
  }

  logout(returnUrl: string | null = null): void {
    let headers = new HttpHeaders({
      'Authorization': 'Bearer ' + this.currentUserValue?.token?.accessToken
    });
    this.http.post(this.apiBaseUrl + 'api/auth/logout', null, { headers: headers })
      .subscribe({
        next: (resp: any) => {
          console.log(resp);
        }
      });
    this.currentUserSubject.next(null);
    localStorage.removeItem('currentUser');
    // If returnUrl is defined, redirect to it
    this.router.navigate(['/login'], (returnUrl ? { queryParams: { returnUrl: returnUrl } } : undefined));
  }

  saveRefreshToken(token: JWTToken): void {
    let user = this.currentUserValue;
    if (!user || !token) {
      // Fail to refresh token
      this.logout(window.location.pathname);
      return;
    }

    user.token = token;
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.currentUserSubject.next(user);// = new BehaviorSubject<User | null>(user);
  }

  refreshTokenRequest(): Observable<JWTToken> {
    let headers = new HttpHeaders().set('Authorization', 'Bearer ' + this.currentUserValue?.token?.refreshToken);
    console.log('refreshToken to send: ' + this.currentUserValue?.token?.refreshToken);

    return this.http.get<JWTToken>(this.apiBaseUrl + 'api/auth/refreshtoken', { headers: headers });
  }

  isAuthenticated(): boolean {
    // return !!this.currentUserSubject;
    return !!this.currentUserValue && !!this.currentUserValue.token;
    // return !isTokenExpired;
  }

  isTokenExpired(): boolean {
    if (!this.currentUserValue || !this.currentUserValue.token) return true;

    const decoded: any = jwt_decode(this.currentUserValue.token.accessToken);
    if (!decoded || !decoded.exp) return true;

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return !(date.valueOf() > new Date().valueOf());
  }

  private decodeToken(token: JWTToken): any {
    return jwt_decode(token.accessToken);
  }

  // private getUserRolesFromToken(user: User): string[] {
  //   let roles: string[] = [];

  //   const decoded: any = jwt_decode(user.token!.accessToken);
  //   // console.log(decoded);
  //   if (!decoded || !decoded.roles) return roles;

  //   return decoded.roles;
  // }

  isUserAdmin(): boolean {
    if (!this.currentUserValue || !this.currentUserValue.roles) return false;

    let roles = this.currentUserValue.roles;
    return !!roles.length && !!roles.find(r => r.name === 'ADMIN');
  }

  isUser(): boolean {
    let roles = this.currentUserValue!.roles!;
    return !!roles.length && !!roles.find(r => r.name === 'USER');
  }

  changeDarkMode(darkModeEnabled: boolean) {
    let user = this.currentUserValue;
    if (!user) return;

    user.darkModeEnabled = darkModeEnabled;

    localStorage.setItem('currentUser', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  get currentUserValue(): User | null {
    if (!this.currentUserSubject) return null;

    return this.currentUserSubject.value;
  }

}
