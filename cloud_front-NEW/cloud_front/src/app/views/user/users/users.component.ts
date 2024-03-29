import { Component } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { UserShareRight } from 'src/app/models/user-share-right';
import { DataService } from 'src/app/services/data.service';
import { EnterUserComponent } from './enter-user/enter-user.component';
import { MatDialog } from '@angular/material/dialog';
import { Right } from 'src/app/models/right';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent {

  // Users counter table
  private usersSharedRightsCpt: UserShareRight[] = [];
  public usersSharedRightsDataSource: MatTableDataSource<UserShareRight> = new MatTableDataSource<UserShareRight>();
  public usersDataSource: MatTableDataSource<UserShareRight> = new MatTableDataSource<UserShareRight>(this.usersSharedRightsCpt);
  public displayedColumns: string[] = ['username'];
  public checkboxColumns: string[] = [];
  // Unchanged users list (not filtered)
  private usersSharedRights?: UserShareRight[];
  private rights?: Right[];

  constructor(private dataService: DataService, private dialog: MatDialog, private snackBar: MatSnackBar) { }

  ngAfterViewInit() {
    // Load rights/users after getting pagination reference (pagination must be loaded before the table dataSource)
    this.loadRights();
    this.loadUsers();
  }

  loadUsers(): void {
    this.dataService.getUsersSharedRights().subscribe((usersSharedRights: UserShareRight[]) => {
      // this.loadRights();

      console.log(usersSharedRights);
      this.usersSharedRights = usersSharedRights;
      this.usersSharedRightsCpt = this.usersSharedRights;
      this.usersSharedRightsDataSource.data = this.usersSharedRightsCpt;
    });
  }

  loadRights(): void {
    this.dataService.getRights().subscribe((rights: Right[]) => {
      this.rights = rights;
      this.checkboxColumns = [];
      for (let right of rights) {
        this.checkboxColumns.push(right.name);
      }
      this.displayedColumns = ['username', ...this.checkboxColumns];
    });
  }

  addUser(): void {
    const matDialogRef = this.dialog.open(EnterUserComponent, {
      // data: { username: 'test' },
    });
    matDialogRef.afterClosed().subscribe(result => {
      window.location.reload();
    })
  }

  isFindRight(userShareRight: UserShareRight, rightName: string): boolean {
    return !!userShareRight.rights.find(r => r.name === rightName);
  }

  // findRight(userShareRight: UserShareRight, rightName: string): Right | undefined {
  //   return userShareRight.rights.find(r => r.name === rightName);
  // }

  findRight(rightName: string): Right | undefined {
    if (!this.rights) throw Error("Aucun droit chargé");

    return this.rights.find(r => r.name === rightName);
  }

  enableRight(userShareRight: UserShareRight, rightName: string, enable: boolean): void {
    let right = this.findRight(rightName);
    if (!right) {
      this.snackBar.open("Erreur interne. Impossible de trouver le droit", '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      return;
    }

    if (rightName === 'Afficher') {
      window.location.reload();
    }

    this.dataService.enableRight(userShareRight, right, enable).subscribe((d: any) => {
      console.log(d);
    });
  }

}
