import { Injectable, NgModule } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { PreloadAllModules, RouterModule, RouterStateSnapshot, Routes, TitleStrategy } from '@angular/router';
import { AuthGuard } from './guard/auth.guard';
import { HomeComponent } from './shared/home/home.component';
import { MainComponent } from './views/main/main.component';
import { HomeMainComponent } from './views/main/home/home.component';
import { AccountComponent } from './views/main/account/account.component';
import { RegisterComponent } from './shared/auth/register/register.component';
import { LoginComponent } from './shared/auth/login/login.component';
import { FilesComponent } from './views/main/files/files.component';
import { PricingComponent } from './shared/pricing/pricing.component';
import { CheckoutComponent } from './checkout/checkout.component';
import { ResetPasswordComponent } from './shared/auth/reset-password/reset-password.component';

@Injectable({ providedIn: 'root' })
export class TemplatePageTitleStrategy extends TitleStrategy {
  constructor(private readonly title: Title) {
    super();
  }

  override updateTitle(routerState: RouterStateSnapshot) {
    const split = routerState.url.split('/');
    // Title is set from the last url element
    this.title.setTitle(`Tuxit Cloud | ${split[split.length - 1]}`);
  }
}

const routes: Routes = [
  { path: '', component: HomeComponent },

  {
    path: 'main', component: MainComponent, canActivate: [AuthGuard],
    children: [
      { path: '', component: HomeMainComponent },
      { path: 'account', component: AccountComponent },
      { path: 'files', component: FilesComponent }
    ]
  },

  { path: 'pricing', component: PricingComponent },

  { path: 'checkout/:id', component: CheckoutComponent },

  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'reset-password', component: ResetPasswordComponent },

  { path: 'account', redirectTo: 'main/account' },

  { path: '**', component: HomeComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'enabled', preloadingStrategy: PreloadAllModules, useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
