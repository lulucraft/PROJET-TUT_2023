import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts.component.html',
  styleUrls: ['./accounts.component.scss']
})
export class AccountsComponent implements OnInit {

  public users: MatTableDataSource<User> = new MatTableDataSource<User>();
  public columnsUsers: string[] = ["id", "username", "congesNbr", "accountActive", "creationDate", "btn_edit"];//, "email"

  constructor(private dataService: DataService, private router: Router, private authService: AuthService) { }

  ngOnInit(): void {
    this.dataService.getUsers().subscribe((users: User[]) => this.users.data = users);
  }

  // addAccount(): void {
  //   this.router.navigate(['/admin/account', -1]);
  // }

  isTheUser(user: User): boolean {
    return user.username === this.authService.currentUserValue?.username;
  }

  isAdmin(user: User): boolean {
    return !!user.roles && user.roles.find(role => role.name === "ADMIN") != null;
  }

  editUser(user: User): void {
    this.router.navigate(['/admin/account', user.id]);
  }

}
