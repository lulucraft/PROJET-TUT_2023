import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSelectChange } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { Conge } from 'src/app/models/conge';
import { AuthService } from 'src/app/services/auth.service';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-conges',
  templateUrl: './conges.component.html',
  styleUrls: ['./conges.component.scss']
})
export class CongesComponent implements OnInit {

  @ViewChild('paginator') paginator!: MatPaginator;

  // Conges counter table
  public congesCpt: CongeType[] = [
    { label: 'Acquis', nbr: 0.0 },
    { label: 'Pris', nbr: 0.0 },
  ];
  public displayedColumns: string[] = ['label', 'nbr'];

  // Unchanged conges list (not filtered)
  private conges?: Conge[];

  // Conges request table
  public congesRequestsNotValidated: MatTableDataSource<Conge> = new MatTableDataSource<Conge>();
  public columnsCongesRequestsNotValidated: string[] = ['creationDate', 'startDate', 'endDate', 'btn_del'];
  public filterNotValidated: string = 'all';


  constructor(private dataService: DataService, private router: Router, private authService: AuthService) { }

  ngOnInit(): void {
    // if (!this.isAdmin()) {
    // USER
    this.loadCongesAcquis()
    // }
  }

  ngAfterViewInit() {
    if (!this.isAdmin()) {
      // USER
      this.congesRequestsNotValidated.paginator = this.paginator;
    }

    // Load conges after getting pagination reference (pagination must be loaded bedore the table dataSource)
    this.loadConges();
  }

  loadCongesAcquis(): void {
    this.dataService.getCongesAcquis().subscribe((congesNbr: number) => {
      this.congesCpt[0].nbr = congesNbr;
    });
  }

  loadConges(): void {
    if (!this.isAdmin()) {
      // USER
      this.dataService.getConges().subscribe((conges: Conge[]) => {
        this.conges = conges;

        // Get unvalidated conges
        // this.congesRequestsNotValidated.data = conges.filter(c => c.validated === false);
        this.congesRequestsNotValidated.data = conges;

        this.calcCongesCounter();
      });
    }
  }

  calcCongesCounter(): void {
    if (!this.conges) return;

    this.congesCpt[1].nbr = 0;

    let nbr: number = 0.0;
    for (let c of this.conges) {
      // If conge is validated or if a conge request is in progress (no validator)
      if ((!c.validated && !c.validator) || (c.validated && c.validator)) {
        nbr = new Date(c.endDate).getTime() - new Date(c.startDate).getTime();
        // Get days from the dates (+1 to include the first day)
        this.congesCpt[1].nbr += (nbr / (1000 * 3600 * 24)) + 1;
      }
    }
  }

  getCongesRestants(): number {
    return this.congesCpt[0].nbr - this.congesCpt[1].nbr;
  }

  isAdmin(): boolean {
    return this.authService.isUserAdmin();
  }

  demandeConges(): void {
    this.router.navigate(['/main/congesrequest']);
  }

  deleteConge(congeId: number): void {
    this.dataService.deleteCongeRequest(congeId)
      .subscribe({
        next: (resp: string) => {
          console.info(resp);

          // Remove conges request from table
          this.congesRequestsNotValidated.data = this.congesRequestsNotValidated.data.filter(cr => cr.id !== congeId);
          // Remove conges request from unfilter conges list
          this.conges = this.conges!.filter(c => c.id !== congeId);

          // Refresh conges counter
          this.calcCongesCounter();
        },
        error: (err) => {
          alert("Erreur lors de la suppression de la demande de congés");
        }
      });
  }

  onSelectionChange(event: MatSelectChange): void {
    if (!this.conges) return;

    switch (event.value) {
      case 'inprogress':
        this.congesRequestsNotValidated.data = this.conges.filter(c => !c.validated && !c.validator);
        break;

      case 'validated':
        this.congesRequestsNotValidated.data = this.conges.filter(c => c.validated && c.validator);
        break;

      case 'invalidated':
        this.congesRequestsNotValidated.data = this.conges.filter(c => !c.validated && c.validator);
        break;

      default:
        this.congesRequestsNotValidated.data = this.conges;
        break;
    }
  }

}

interface CongeType {
  label: string;
  nbr: number;
}
