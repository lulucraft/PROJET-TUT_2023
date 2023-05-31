import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSelectChange } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-files',
  templateUrl: './files.component.html',
  styleUrls: ['./files.component.scss']
})
export class FilesComponent implements OnInit {

  @ViewChild('paginator') paginator!: MatPaginator;

  // Files counter table
  public filesCpt: File[] = [
  ];
  public displayedColumns: string[] = ['label', 'nbr'];

  // Unchanged files list (not filtered)
  private files?: File[];

  // Files request table
  public filesRequestsNotValidated: MatTableDataSource<File> = new MatTableDataSource<File>();
  public columnsFilesRequestsNotValidated: string[] = ['creationDate', 'editDate', 'btn_del'];
  public filterNotValidated: string = 'all';


  constructor(private dataService: DataService, private router: Router, private authService: AuthService) { }

  ngOnInit(): void {
  }

  ngAfterViewInit() {
    if (!this.isAdmin()) {
      // USER
      this.filesRequestsNotValidated.paginator = this.paginator;
    }

    // Load files after getting pagination reference (pagination must be loaded bedore the table dataSource)
    this.loadFiles();
  }

  loadFiles(): void {
    if (!this.isAdmin()) {
      // USER
      this.dataService.getFiles().subscribe((files: File[]) => {
        this.files = files;
      });
    }
  }

  isAdmin(): boolean {
    return this.authService.isUserAdmin();
  }

  demandeConges(): void {
    this.router.navigate(['/main/congesrequest']);
  }

  deleteFile(fileName: string): void {
    this.dataService.deleteFile(fileName)
      .subscribe({
        next: (resp: string) => {
          console.info(resp);

          // Remove file request from unfilter files list
          this.files = this.files!.filter(c => c.name !== fileName);
        },
        error: (err: any) => {
          console.error(err);
          alert("Erreur lors de la suppression de la demande de congés");
        }
      });
  }

  onSelectionChange(event: MatSelectChange): void {
    if (!this.files) return;

    switch (event.value) {
      case 'inprogress':
        // this.filesRequestsNotValidated.data = this.files.filter();
        break;

      case 'validated':
        // this.filesRequestsNotValidated.data = this.files.filter();
        break;

      case 'invalidated':
        // this.filesRequestsNotValidated.data = this.files.filter();
        break;

      default:
        this.filesRequestsNotValidated.data = this.files;
        break;
    }
  }

}
