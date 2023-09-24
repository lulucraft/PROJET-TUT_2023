import { Component } from '@angular/core';
import { DataService } from 'src/app/services/data.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-enter-user',
  templateUrl: './enter-user.component.html',
  styleUrls: ['./enter-user.component.scss']
})
export class EnterUserComponent {

  public username?: string;

  constructor(private dataService: DataService, private authService: AuthService, private snackBar: MatSnackBar) {
  }

  addUser(): void {
    if (!this.username) {
      this.snackBar.open("Veuillez entrer un nom d'utilisateur", '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      return;
    }

    if (this.username === this.authService.currentUserValue?.username) {
      this.snackBar.open("Vous ne pouvez pas partager à vous-même", '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      return;
    }

    this.dataService.addUserShare(this.username).subscribe({
      next: (d) => {
        if (d == null) {
          // this.snackBar.open("Utilisateur incorrect", '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
          return;
        }
        console.log(d)
      }
    });
  }

}
