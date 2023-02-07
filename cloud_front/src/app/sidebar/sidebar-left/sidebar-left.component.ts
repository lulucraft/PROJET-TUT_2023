import { animate, style, transition, trigger } from '@angular/animations';
import { Component, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HeaderComponent } from 'src/app/header/header.component';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-sidebar-left',
  templateUrl: './sidebar-left.component.html',
  styleUrls: ['./sidebar-left.component.scss', './../sidebar.scss'],
  animations: [
    trigger('fadeIn', [
      transition('void => *', [
        style({ opacity: 0 }),
        animate('0.2s ease-in-out', style({ opacity: 1 }))
      ])
    ])
  ]
})
export class SidebarLeftComponent implements OnInit {

  constructor(private appHeader: HeaderComponent, private authService: AuthService, private router: Router) { }

  ngOnInit(): void {
  }

  logout(): void {
    this.appHeader.leftMenuOpened = false;
    this.authService.logout();
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  isAdmin(): boolean {
    return this.authService.isUserAdmin();
  }

  getUserName(): string {
    if (!this.authService.currentUserValue) {
      // Default to display
      return 'Utilisateur';
    }
    return this.authService.currentUserValue?.username;
  }

  account(): void {
    this.appHeader.leftMenuOpened = false;
    this.router.navigate(['account']);
  }

  conges(): void {
    this.appHeader.leftMenuOpened = false;
    if (!this.authService.isUserAdmin()) {
      this.router.navigate(['/conges']);
    } else {
      this.router.navigate(['/adminconges']);
    }
  }

  newsletter(): void {
    this.appHeader.leftMenuOpened = false;
    this.router.navigate(['/main/newsletter']);
  }

  adminNewsletter(): void {
    this.appHeader.leftMenuOpened = false;
    this.router.navigate(['/admin/newsletter']);
  }

  adminComptes(): void {
    this.appHeader.leftMenuOpened = false;
    this.router.navigate(['/admin/accounts']);
  }

  @HostListener('click', ['$event.target'])
  closeSettingsMenu(el: HTMLElement) {
    if (el.className.includes("sidebar-close-background")) {
      // Close left menu
      this.appHeader.leftMenuOpened = false;
    }
  }

}
