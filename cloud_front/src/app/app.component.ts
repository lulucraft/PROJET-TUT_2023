import { Component, OnInit, Renderer2 } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './services/auth.service';
import { DataService } from './services/data.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  // Dark theme by défault
  private darkModeEnabled: boolean = true;

  constructor(private renderer: Renderer2, private authService: AuthService, private router: Router, private dataService: DataService) { }

  ngOnInit(): void {
    // Dark theme by défault
    // this.changeDarkMode(true);
    this.initDarkMode(true);

    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    if (this.authService.currentUserValue!.darkModeEnabled === false) {
      this.initDarkMode(false);
    }
  }

  // isAuthenticated(): boolean {
  //   return this.authService.isAuthenticated();
  // }

  changeDarkMode(darkModeEnabled: boolean): void {
    // Save user preference
    this.dataService.setDarkMode(darkModeEnabled)
      .pipe(
        catchError(err => {
          if (this.authService.isAuthenticated()) {
            alert("Erreur lors du changement du mode sombre");
          }
          return throwError(() => console.error(err));
        })
      )
      .subscribe(() => {
        this.authService.changeDarkMode(darkModeEnabled);
      });

    this.initDarkMode(darkModeEnabled);
  }

  // Update application theme
  initDarkMode(darkModeEnabled: boolean): void {
    this.darkModeEnabled = darkModeEnabled;
    if (darkModeEnabled) {
      this.renderer.addClass(document.body, 'darkMode');
    } else {
      this.renderer.removeClass(document.body, 'darkMode');
    }
  }

  get isDarkModeEnabled(): boolean {
    return this.darkModeEnabled;
  }

}
