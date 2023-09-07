import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Navigation, Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-otplogin',
  templateUrl: './otplogin.component.html',
  styleUrls: ['./otplogin.component.scss', './../auth.scss']
})
export class OTPloginComponent {

  public otpForm: FormGroup;
  public loginInProgress: boolean = false;
  private user!: User;

  constructor(private router: Router, private authService: AuthService) {
    this.otpForm = new FormBuilder().group({
      otpcode: ['', Validators.required]
    });

    let navig: Navigation | null = this.router.getCurrentNavigation();
    if (!navig || !navig.extras || !navig.extras.state) {
      this.router.navigate(['/login']);
      console.error("Vous devez d'abord entrer vos identifiants de connexion dans la page login");
      return;
    }

    this.user = navig.extras.state["user"];
  }

  loginOTP(): void {
    let otpcode: string = this.otpForm.controls["otpcode"].value;
    if (otpcode) {
      this.loginInProgress = true;

      this.authService.login(this.user, otpcode).subscribe({
        next: () => {
          console.log("OTP ok");
          this.loginInProgress = false;
        },
        error: (error) => {
          console.error(error);
        }
      });
    }
  }
}
