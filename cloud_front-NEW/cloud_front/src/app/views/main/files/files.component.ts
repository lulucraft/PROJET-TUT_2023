import { SelectionModel } from '@angular/cdk/collections';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSelectChange } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { File } from 'src/app/models/file';
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
  private filesCpt: File[] = [
    { name: 'Fichier1', creationDate: new Date(2000), modificationDate: new Date(2000), data: 'Hello, world!' },
  ];
  public filesDataSource: MatTableDataSource<File> = new MatTableDataSource<File>(this.filesCpt);
  public displayedColumns: string[] = ['select', 'label', 'credate', 'editdate', 'btn_download', 'btn_del'];

  // Unchanged files list (not filtered)
  private files?: File[];

  public selection = new SelectionModel<File>(true, []);

  // Files request table
  // public filesRequestsNotValidated: MatTableDataSource<File> = new MatTableDataSource<File>();
  // public columnsFilesRequestsNotValidated: string[] = ['creationDate', 'editDate', 'btn_del'];
  // public filterNotValidated: string = 'all';


  constructor(private dataService: DataService, private router: Router, private authService: AuthService) { }

  ngOnInit(): void {
  }

  ngAfterViewInit() {
    if (!this.isAdmin()) {
      // USER
      // this.filesRequestsNotValidated.paginator = this.paginator;
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

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    let numSelected = this.selection.selected.length;
    let numRows = this.filesDataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
    } else {
      this.filesDataSource.data.forEach((row: File) => this.selection.select(row));
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
          alert("Erreur lors de la suppression du fichier");
        }
      });
  }

  downloadFiles(files: SelectionModel<File>): void {
    for (let file of files.selected) {
      this.dataService.downloadFile(file);
    }
  }

  downloadFile(file: File): void {
    this.dataService.downloadFile(file);
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
        // this.filesRequestsNotValidated.data = this.files;
        break;
    }
  }

  showActions(selection: SelectionModel<File>) {
    console.log('selection', selection.selected);
  }

}
