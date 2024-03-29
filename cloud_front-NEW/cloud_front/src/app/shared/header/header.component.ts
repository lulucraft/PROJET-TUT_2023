import { Component } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {

  public settingsMenuOpened: boolean = false;
  public leftMenuOpened: boolean = false;

  constructor(private authService: AuthService) { }

  openSettingsMenu(): void {
    this.settingsMenuOpened = !this.settingsMenuOpened;
    this.leftMenuOpened = false;
  }

  openLeftMenu() {
    this.leftMenuOpened = !this.leftMenuOpened;
    this.settingsMenuOpened = false;
  }

  hasOffer(): boolean {
    return !!this.authService.currentUserValue?.offer;
  }

}
