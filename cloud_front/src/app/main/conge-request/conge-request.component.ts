import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Conge } from 'src/app/models/conge';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-conge-request',
  templateUrl: './conge-request.component.html',
  styleUrls: ['./conge-request.component.scss']
})
export class CongeRequestComponent implements OnInit {

  public congesRequestForm: FormGroup = this.formBuilder.group({
    startDate: ['', Validators.required],
    endDate: ['', Validators.required]
  });

  constructor(private formBuilder: FormBuilder, private dataService: DataService, private router: Router, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
  }

  sendCongeRequest() {
    let startDate = this.congesRequestForm.controls['startDate'].value;
    let endDate = this.congesRequestForm.controls['endDate'].value;

    if (!startDate || !endDate) {
      this.snackBar.open('Veuillez préciser une date de début et de fin du congé', '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      return;
    }
    let conge: Conge = { startDate, endDate };
    this.dataService.sendCongeRequest(conge).subscribe(() => {
      // Redirect to conges page
      this.back();
    });
  }

  back(): void {
    // Redirect to conges page
    this.router.navigate(['/main/conges']);
  }

}
