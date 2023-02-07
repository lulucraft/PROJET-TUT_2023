import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { User } from 'src/app/models/user';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-admin-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {

  public accountForm: FormGroup = this.formBuilder.group({
    accountActive: ['true', Validators.required],
    congesNbr: ['']
    // email: ['', [Validators.required, Validators.email]],
    // password: ['', [Validators.required]]
  });

  public user$?: Observable<User>;
  private user: User | undefined;

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private dataService: DataService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(obj => {
      let userId: number = obj['ID'];

      this.user$ = this.dataService.getUser(userId);
      this.user$.subscribe((user: User) => {
        if (!user) {
          this.router.navigate(['/admin/accounts']);
          return;
        }

        this.user = user;

        this.accountForm.patchValue({
          accountActive: new String(user.accountActive).toString(),
          congesNbr: user.congesNbr
        });
      });
    });
  }

  editAccount(): void {
    let user: User | undefined = this.user;
    if (!user) return;

    let congesNbr: number = this.accountForm.controls['congesNbr'].value;
    if (congesNbr < 0) {
      this.snackBar.open('Le nombre de congés acquis doit être supérieur ou égal à 0', '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      return;
    }

    let accountActive: boolean = this.accountForm.controls['accountActive'].value;

    user.accountActive = accountActive;
    user.congesNbr = congesNbr;

    this.dataService.editUser(user).subscribe(() => {
      this.snackBar.open('Modificaitons effectuées', '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container'] });
      this.back();
    });
  }

  back(): void {
    this.router.navigate(['/admin/accounts']);
  }

}
