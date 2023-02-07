import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSelectChange } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { Conge } from 'src/app/models/conge';
import { AdminConge } from 'src/app/models/conge-admin';
import { AuthService } from 'src/app/services/auth.service';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-admin-conges',
  templateUrl: './conges.component.html',
  styleUrls: ['./conges.component.scss']
})
export class CongesComponent implements OnInit {

  @ViewChild('paginator') paginator!: MatPaginator;

  // Unchanged conges list (not filtered)
  private conges?: AdminConge[];

  // Conges requests admin table
  public congesRequestsNotValidated: MatTableDataSource<AdminConge> = new MatTableDataSource<AdminConge>();
  public columnsCongesRequestsNotValidated: string[] = ['username', 'creationDate', 'startDate', 'endDate', 'btn_edit'];
  public filterNotValidated: string = 'all';

  constructor(private dataService: DataService, private router: Router, private authService: AuthService) { }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.congesRequestsNotValidated.paginator = this.paginator;

    // Load conges after getting pagination reference (pagination must be loaded bedore the table dataSource)
    this.loadConges();
  }

  loadConges(): void {
    this.dataService.getCongesAdmin().subscribe((conges: { user: Conge[] }) => {
      let adminConges: AdminConge[] = [];

      Object.entries(conges).forEach(([key, value], _index) => {
        for (let conge of value) {
          let adConge: AdminConge = { username: key, conge: conge };
          adminConges.push(adConge);
        }
      });
      this.congesRequestsNotValidated.data = adminConges;

      this.conges = adminConges;

      // Get invalidated conges
      // this.congesRequestsNotValidated.data = adminConges.filter(adC => !adC.conge.validator);
    });
  }

  validateConge(adminConge: AdminConge): void {
    this.router.navigate(['/admin/congevalidation'], { state: adminConge });
  }

  onSelectionChange(event: MatSelectChange): void {
    if (!this.conges) return;

    switch (event.value) {
      case 'inprogress':
        this.congesRequestsNotValidated.data = this.conges!.filter(adC => !adC.conge.validated && !adC.conge.validator);
        break;

      case 'validated':
        this.congesRequestsNotValidated.data = this.conges!.filter(adC => adC.conge.validated && adC.conge.validator);
        break;

      case 'invalidated':
        this.congesRequestsNotValidated.data = this.conges!.filter(adC => !adC.conge.validated && adC.conge.validator);
        break;

      default:
        this.congesRequestsNotValidated.data = this.conges!;
        break;
    }
  }

}
