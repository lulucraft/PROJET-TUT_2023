import { Inject, Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { catchError, Observable, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { JWTToken } from '../models/jwt-token';

@Injectable()
export class TokenHttpInterceptorInterceptor implements HttpInterceptor {

  constructor(@Inject('API_BASE_URL') private apiBaseUrl: string, private authService: AuthService) { }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // console.log(request.url)
    // request.headers.set('Access-Control-Allow-Origin', '*');

    if (request.url === this.apiBaseUrl + 'api/auth/register' ||
      request.url === this.apiBaseUrl + 'api/auth/login' ||
      request.url === this.apiBaseUrl + 'api/auth/logout' ||
      request.url === 'login' ||
      request.url === this.apiBaseUrl + 'api/auth/refreshtoken') {
      return next.handle(request);
    }

    // Add token to http request
    const req = request.clone({ headers: request.headers.set('Authorization', 'Bearer ' + this.authService.currentUserValue?.token?.accessToken) })

    // console.log(req);
    return next.handle(req).pipe(
      catchError(err => {
        // If user not authenticated
        if (!this.authService.currentUserValue || !this.authService.currentUserValue.token) {
          this.authService.logout();
        }

        // If token expired
        if (this.authService.isTokenExpired()) {
          // Try to refresh token
          return this.authService.refreshTokenRequest().pipe(
            switchMap((token: JWTToken) => {
              console.log(token);
              if (!token) {
                this.authService.logout(window.location.pathname);
                throw new Error('Refresh token expired');
              }
              this.authService.saveRefreshToken(token);
              // Re-execute previous failed request
              // Add new token to http request
              const previousReq = req.clone({ headers: request.headers.set('Authorization', 'Bearer ' + this.authService.currentUserValue?.token?.accessToken) })
              // console.log(previousReq)
              return next.handle(previousReq);
            }),
            catchError((err) => {
              return throwError(() => console.error(err));
            })
          );
        }

        return throwError(() => console.error(err));
      })
    );
  }
}
