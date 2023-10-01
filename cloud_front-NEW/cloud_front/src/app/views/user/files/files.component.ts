import { SelectionModel } from '@angular/cdk/collections';
import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSelectChange } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Navigation, Router } from '@angular/router';
import { File } from 'src/app/models/file';
import { User } from 'src/app/models/user';
import { DataService } from 'src/app/services/data.service';

@Component({
  selector: 'app-files',
  templateUrl: './files.component.html',
  styleUrls: ['./files.component.scss']
})
export class FilesComponent implements OnInit, AfterViewInit {

  @ViewChild('paginator') paginator!: MatPaginator;

  // Files counter table
  private filesCpt: File[] = [
    // { name: 'Fichier1', creationDate: new Date(2000), modificationDate: new Date(2000), data: 'Hello, world!' },
  ];
  public filesDataSource: MatTableDataSource<File> = new MatTableDataSource<File>(this.filesCpt);
  public displayedColumns: string[] = ['select', 'label', 'credate', 'editdate', 'btn_check', 'btn_download', 'btn_del'];
  public sharedMode: boolean = false;
  public usersSharer?: User[];

  // Unchanged files list (not filtered)
  private files: File[] = [];
  private sharedDownloadRight: boolean = false;
  private sharedDeleteRight: boolean = false;
  private sharedAddFileRight: boolean = false;
  private sharedCheckHashRight: boolean = false;

  public selection = new SelectionModel<File>(true, []);
  public selectedUserSharer?: User;

  // Files request table
  // public filesRequestsNotValidated: MatTableDataSource<File> = new MatTableDataSource<File>();
  // public columnsFilesRequestsNotValidated: string[] = ['creationDate', 'editDate', 'btn_del'];
  // public filterNotValidated: string = 'all';


  constructor(private dataService: DataService, private router: Router, private route: ActivatedRoute, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    if (this.route.routeConfig?.path === "shared_files") {//window.location.href.split('/').pop()
      this.sharedMode = true;
    }
  }

  ngAfterViewInit() {
    // let navig: Navigation | null = this.router.getCurrentNavigation();
    // if (navig && navig.extras && navig.extras.state) {
    // this.sharedMode = navig.extras.state["mode"] == 1 ? true : false;
    if (this.sharedMode) {//window.location.href.split('/').pop()
      // Load shared files after getting pagination reference (pagination must be loaded before the table dataSource)
      // this.loadSharedFiles();
      this.loadUsersSharer();
    } else {
      // Load files after getting pagination reference (pagination must be loaded before the table dataSource)
      this.loadFiles();
    }
  }

  loadFiles(): void {
    // if (!this.isAdmin()) {
    // USER
    this.dataService.getFiles().subscribe((files: File[]) => {
      console.log(files);
      this.files = files;
      this.filesCpt = this.files;
      this.filesDataSource.data = this.filesCpt;
    });
    // }
  }

  loadUsersSharer(): void {
    this.dataService.getUsersSharer().subscribe((usersSharer: User[]) => {
      this.usersSharer = usersSharer;
      if (usersSharer.length == 1) {
        // Init the first sharer as default
        this.selectedUserSharer = this.usersSharer.at(0);
        this.loadSharedFiles();
      }
    });
  }

  loadSharedFiles(): void {
    if (!this.selectedUserSharer || !this.selectedUserSharer.id) {
      this.snackBar.open("Veuillez sélectionner un utilisateur partageur", '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      return;
    }

    // if (this.selectedUserSharer && this.selectedUserSharer.id) {
    this.dataService.hasUserSharerRight(this.selectedUserSharer.id, "Télécharger").subscribe((hasDownloadRight: boolean) => this.sharedDownloadRight = hasDownloadRight);
    this.dataService.hasUserSharerRight(this.selectedUserSharer.id, "Supprimer").subscribe((hasDeleteRight: boolean) => this.sharedDeleteRight = hasDeleteRight);
    this.dataService.hasUserSharerRight(this.selectedUserSharer.id, "Ajouter").subscribe((hasAddFileRight: boolean) => this.sharedAddFileRight = hasAddFileRight);
    this.dataService.hasUserSharerRight(this.selectedUserSharer.id, "Vérifier intégrité").subscribe((hasCheckHashRight: boolean) => this.sharedCheckHashRight = hasCheckHashRight);
    // } else {
    //   console.info("Pas d'utilisateur partageur sélectionné ou l'utilisateur partageur n'a pas d'id")
    // }

    this.dataService.getSharedFiles(this.selectedUserSharer.id).subscribe((sharedFiles: File[]) => {
      console.log(sharedFiles);
      this.files = sharedFiles;
      this.filesCpt = this.files;
      this.filesDataSource.data = this.filesCpt;
    });
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

  // isAdmin(): boolean {
  //   return this.authService.isUserAdmin();
  // }

  hasDownloadRight(): boolean {
    return !this.sharedMode || this.sharedDownloadRight;
  }

  hasDeleteRight(): boolean {
    return !this.sharedMode || this.sharedDeleteRight;
  }

  hasAddFileRight(): boolean {
    return !this.sharedMode || this.sharedAddFileRight;
  }

  hasCheckHashRight(): boolean {
    return !this.sharedMode || this.sharedCheckHashRight;
  }

  isSelectionVisible(): boolean {
    return !this.sharedMode || this.hasDownloadRight() || this.hasDeleteRight();
  }

  deleteFile(file: File): void {
    this.snackBar.open("Suppression du fichier en cours...", '', { duration: 4000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container'] });

    this.dataService.deleteFile(file)
      .subscribe({
        next: (resp: string) => {
          console.info(resp);
          this.snackBar.open("Fichier supprimé", '', { duration: 4000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'success'] });

          // Remove file request from unfilter files list
          this.files = this.files!.filter(c => c.id !== file.id);
          this.filesCpt = this.files;
          this.filesDataSource.data = this.filesCpt;
        },
        error: (err: any) => {
          console.error(err);
          this.snackBar.open("Erreur lors de la suppression du fichier", '', { duration: 4000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
        }
      });
  }

  deleteFiles(files: SelectionModel<File>): void {
    this.snackBar.open("Suppression des fichier en cours...", '', { duration: 4000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container'] });

    for (let file of files.selected) {
      this.dataService.deleteFile(file)
        .subscribe({
          next: (resp: string) => {
            console.info(resp);
            this.snackBar.open("Fichier supprimé", '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'success'] });

            // Remove file request from unfilter files list
            this.files = this.files!.filter(c => c.id !== file.id);
            this.filesCpt = this.files;
            this.filesDataSource.data = this.filesCpt;
          },
          error: (err: any) => {
            console.error(err);
            this.snackBar.open("Erreur lors de la suppression du fichier", '', { duration: 3000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
          }
        });
    }
  }

  addFile(event: any): void {
    if (!event.target || !event.target.files) return;
    let files: FileList = event.target.files;

    this.snackBar.open("Ajout du fichier en cours...", '', { duration: 4000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container'] });

    for (let i = 0; i < files.length; i++) {
      let fileData: globalThis.File = files[i];
      console.info(fileData)

      let creationDate: Date = new Date();
      let file: File = { name: fileData.name, creationDate: creationDate, size: fileData.size };

      if (this.sharedMode) {
        // Page fichiers partagés
        if (!this.selectedUserSharer || !this.selectedUserSharer.id) {
          alert("Erreur : partageur invalide")
          return;
        }

        this.dataService.addSharedFile(file, fileData, this.selectedUserSharer!.id).then(fileId => {
          if (fileId === undefined) {
            // file.id === undefined après requête 'api/user/file'
            this.snackBar.open("Erreur lors de l'enregistrement du fichier dans la base de données", '', { duration: 3000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
            return;
          } else if (!fileId) {
            this.snackBar.open("Erreur lors de la tentative de l'enregistrement du fichier en base", '', { duration: 3000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
            return;
          }
          console.info(fileId);
          this.snackBar.open("Fichier ajouté", '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'success'] });

          // On défini l'id avant d'ajouter le fichier dans la liste du table
          file.id = fileId;
          // Ajout du fichier dans le table
          this.files.push(file)
          this.filesCpt = this.files;
          this.filesDataSource.data = this.filesCpt;
        });
      }
      else {
        // Page fichiers
        this.dataService.addFile(file, fileData).then(fileId => {
          if (fileId === undefined) {
            // file.id === undefined après requête 'api/user/file'
            this.snackBar.open("Erreur lors de l'enregistrement du fichier dans la base de données", '', { duration: 3000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
            return;
          } else if (!fileId) {
            this.snackBar.open("Erreur lors de la tentative de l'enregistrement du fichier en base", '', { duration: 3000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
            return;
          }
          console.info(fileId);

          // On défini l'id avant d'ajouter le fichier dans la liste du table
          file.id = fileId;
          // Ajout du fichier dans le table
          this.files.push(file)
          this.filesCpt = this.files;
          this.filesDataSource.data = this.filesCpt;
        });
      }
    }
  }

  downloadFiles(files: SelectionModel<File>): void {
    this.snackBar.open("Téléchargement des fichiers en cours...", '', { duration: 4000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container'] });
    for (let file of files.selected) {
      this.dataService.downloadFile(file).subscribe();
    }
  }

  downloadFile(file: File): void {
    this.snackBar.open("Téléchargement du fichier en cours...", '', { duration: 4000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container'] });
    this.dataService.downloadFile(file).subscribe();
  }

  // Vérifier intégrité
  checkFileHash(file: File): void {
    this.snackBar.open("Vérification de l'intégrité du fichier en cours...", '', { duration: 4000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container'] });
    this.dataService.checkFileHash(file).subscribe(async (hashPromise: Promise<string | null>) => {
      let hash: string | null = await hashPromise;
      if (hash === file.hash) {
        this.snackBar.open("Le fichier actuel est identique à celui lors de sa création en base de données", '', { duration: 2000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'success'] });
      } else {
        this.snackBar.open("Le fichier a été modifié depuis son ajout en base de données !", '', { duration: 3000, horizontalPosition: 'right', verticalPosition: 'top', panelClass: ['snack-bar-container', 'warn'] });
      }
    });
  }

  onSelectionChange(event: MatSelectChange): void {
    console.log(event.value)
    console.log(this.usersSharer)
    console.log(this.selectedUserSharer)

    this.loadSharedFiles();

    // this.dataService.getSharedFiles(event.value).subscribe((sharedFiles: File[]) => {
    //   this.files = sharedFiles;
    //   console.log(sharedFiles)
    // });
  }

  showActions(selection: SelectionModel<File>) {
    console.log('selection', selection.selected);
  }

}
