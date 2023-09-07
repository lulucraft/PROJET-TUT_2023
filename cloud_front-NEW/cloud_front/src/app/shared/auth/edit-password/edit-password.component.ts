import { Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from 'src/app/services/auth.service';

// export interface EditPasswordData {
//   oldPassword: string;
//   newPassword: string;
//   confirmNewPassword: string;
// }

@Component({
  // selector: 'app-edit-password',
  templateUrl: './edit-password.component.html',
  styleUrls: ['./edit-password.component.scss', '../auth.scss']
})
export class EditPasswordComponent {

  public oldPassword?: string;
  public newPassword?: string;
  public confirmNewPassword?: string;

  //@Inject(MAT_DIALOG_DATA) public data: EditPasswordData,
  constructor(private authService: AuthService, private snackBar: MatSnackBar) { }

  updatePassword(): void {
    if (!this.oldPassword) {
      this.snackBar.open("Veuillez entrer l'ancien mot de passe ", '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      return;
    }

    if (!this.newPassword) {
      this.snackBar.open("Veuillez entrer le nouveau mot de passe ", '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      return;
    }

    if (!this.confirmNewPassword) {
      this.snackBar.open("Veuillez confirmer le nouveau mot de passe ", '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      return;
    }

    if (this.newPassword !== this.confirmNewPassword) {
      this.snackBar.open("L'ancien mot de passe ne correspond pas avec la confirmation du nouveau mot de passe ", '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      return;
    }

    this.authService.updatePassword(this.oldPassword, this.newPassword).subscribe({
      next: () => {
        this.snackBar.open('Votre mot de passe a été mis à jour', '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'success'] });
      },
      error: () => {
        this.snackBar.open('Erreur lors de la mise à jour de votre mot de passe', '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      }
    });
  }
}
