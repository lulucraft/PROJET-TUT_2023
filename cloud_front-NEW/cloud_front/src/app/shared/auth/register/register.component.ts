import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss', './../auth.scss']
})
export class RegisterComponent implements OnInit {

  public registerForm: FormGroup = new FormBuilder().group({
    username: ['', Validators.required],
    email: ['', Validators.required],
    password: ['', Validators.required],
    confirmPassword: ['', Validators.required]
  });

  constructor(private authService: AuthService, private snackBar: MatSnackBar, private router: Router) { }

  ngOnInit(): void {
    // Redirect to user page if already authenticated (register page should not be accessible if already authenticated)
    if (this.authService.isAuthenticated()) {
      if (!this.authService.isUserAdmin()) {
        this.router.navigate(['/user']);
      } else {
        this.router.navigate(['/admin']);
      }
    }
  }

  register(): void {
    let username: string = this.registerForm.controls["username"].value;
    if (!username) {
      this.snackBar.open('Veuillez entrer un nom d\'utilisateur', '', { duration: 2500, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      return;
    }

    let email: string = this.registerForm.controls["email"].value;
    if (!email) {
      this.snackBar.open('Veuillez entrer une adresse email', '', { duration: 2500, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      return;
    }

    let password: string = this.registerForm.controls["password"].value;
    let confirmPassword: string = this.registerForm.controls["confirmPassword"].value;

    if (!password || !confirmPassword || password !== confirmPassword) {
      this.snackBar.open('Les mots de passe ne correspondent pas', '', { duration: 2500, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      return;
    }

    this.authService.register({
      username: username,
      email: email,
      password: password,
    });
  }

}
