import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss', './../auth.scss']
})
export class LoginComponent implements OnInit {

  public loginForm: FormGroup;

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

  ngOnInit(): void {
  }

  login(): void {
    let username: string = this.loginForm.controls["username"].value;
    let password: string = this.loginForm.controls["password"].value;
    if (username && password) {
      this.authService.login({ username: username, password: password });
    }
  }

}
