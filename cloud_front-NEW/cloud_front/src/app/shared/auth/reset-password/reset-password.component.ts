import { Component, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  encapsulation: ViewEncapsulation.None,
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss', '../auth.scss']
})
export class ResetPasswordComponent {

  public resetPasswordForm: FormGroup = new FormBuilder().group({
    email: new FormControl('', Validators.required)
  });

  constructor(private authService: AuthService, private snackBar: MatSnackBar) {}

  resetPassword(): void {
    let email = this.resetPasswordForm.controls['email'].value;
    let regexEmail = new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/);
    if (!email || !regexEmail.test(email)) {
      this.snackBar.open('Email incorrect !', '', { duration: 20000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      // alert('Email incorrect !');
      return;
    }

    this.authService.resetPassword(email).subscribe((data: any) => {
      console.log(data);
      this.snackBar.open('Demande de réinitialisation envoyée', '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'success'] });
    });
  }
}
