import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss', './../auth.scss']
})
export class LoginComponent {

  public loginForm: FormGroup;
  public loginInProgress: boolean = false;

  constructor(private authService: AuthService, private router: Router) {
    if (this.authService.isAuthenticated()) {
      if (!this.authService.isUserAdmin()) {
        this.router.navigate(['/']);
      } else {
        this.router.navigate(['/admin']);
      }
    }

    this.loginForm = new FormBuilder().group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  login(): void {
    let username: string = this.loginForm.controls["username"].value;
    let password: string = this.loginForm.controls["password"].value;
    if (username && password) {
      this.loginInProgress = true;
      this.authService.login({ username: username, password: password }).subscribe({
        next: () => {
          // Login ok
          this.loginInProgress = false;
        },
        error: (error) => {
          // Login error
          console.error(error);
          this.loginInProgress = false;
        }
      });
    }
  }

}
