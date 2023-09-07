import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { EditPasswordComponent } from '../../shared/auth/edit-password/edit-password.component';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {

  public user: User;

  constructor(private authService: AuthService, private dialog: MatDialog) {
    this.user = this.authService.currentUserValue!;
  }

  ngOnInit(): void {
  }

  getRoles(): string {
    if (!this.user.roles || !this.user.roles.length) return 'Aucun';

    return this.user.roles.map(r => r.name).join(', ');
  }

  openPopupEditPassword(): void {
    this.dialog.open(EditPasswordComponent, {
      data: { password: 'test' },
    });
  }

}
