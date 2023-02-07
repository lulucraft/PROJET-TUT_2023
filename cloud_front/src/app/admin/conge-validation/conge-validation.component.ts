import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Navigation, Router } from '@angular/router';
import { AdminConge } from 'src/app/models/conge-admin';
import { AuthService } from 'src/app/services/auth.service';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-conge-validation',
  templateUrl: './conge-validation.component.html',
  styleUrls: ['./conge-validation.component.scss']
})
export class CongeValidationComponent implements OnInit {

  public congeValidationForm: FormGroup = this.formBuilder.group({
    username: ['', Validators.required],
    id: ['0', Validators.required],
    creationDate: ['', Validators.required],
    startDate: ['', Validators.required],
    endDate: ['', Validators.required]
  });

  public adminConge!: AdminConge;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private dataService: DataService,
    private snackBar: MatSnackBar
  ) {
    let conge: Navigation | null = this.router.getCurrentNavigation();

    if (!conge || !conge.extras || !conge.extras.state) {
      this.router.navigate(['/admin/conges']);
      return;
    }

    // let adminConge: AdminConge = conge.extras.state as AdminConge;
    this.adminConge = conge.extras.state as AdminConge;

    if (!this.adminConge || !this.adminConge.conge) {
      this.router.navigate(['/admin/conges']);
      alert("Erreur lors de la récupération du congé");
      return;
    }

    this.congeValidationForm.setControl('username', this.formBuilder.control(this.adminConge.username));
    this.congeValidationForm.setControl('id', this.formBuilder.control(this.adminConge.conge.id));
    this.congeValidationForm.setControl('creationDate', this.formBuilder.control(this.adminConge.conge.creationDate));
    this.congeValidationForm.setControl('startDate', this.formBuilder.control(this.adminConge.conge.startDate));
    this.congeValidationForm.setControl('endDate', this.formBuilder.control(this.adminConge.conge.endDate));
    console.log(this.adminConge);
  }

  ngOnInit(): void {
  }

  sendCongeValidation(submitterId: string): void {
    if (!this.authService.currentUserValue) return;

    if (submitterId === 'validate') {
      this.adminConge.conge.validated = true;
    } else if (submitterId === 'refuse') {
      this.adminConge.conge.validated = false;
    }

    this.adminConge.conge.validator = this.authService.currentUserValue.username;

    this.dataService.sendCongeValidation(this.adminConge.conge).subscribe(() => {
      this.snackBar.open('Congé ' + (submitterId === 'validate' ? 'validé' : 'refusé') + ' avec succès', '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container'] });
      this.router.navigate(['/admin/conges']);
    });
  }

  back(): void {
    this.router.navigate(['/admin/conges']);
  }

}
