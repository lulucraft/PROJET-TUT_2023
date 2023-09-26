import { Injectable, NgModule } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { PreloadAllModules, RouterModule, RouterStateSnapshot, Routes, TitleStrategy } from '@angular/router';
import { AuthGuard } from './guard/auth.guard';
import { HomeComponent } from './shared/home/home.component';
import { UserComponent } from './views/user/user.component';
import { HomeUserComponent } from './views/user/home/home.component';
import { AccountComponent } from './views/account/account.component';
import { RegisterComponent } from './shared/auth/register/register.component';
import { LoginComponent } from './shared/auth/login/login.component';
import { FilesComponent } from './views/user/files/files.component';
import { PricingComponent } from './shared/pricing/pricing.component';
import { CheckoutComponent } from './checkout/checkout.component';
import { ResetPasswordComponent } from './shared/auth/reset-password/reset-password.component';
import { CgvComponent } from './shared/cgv/cgv.component';
import { OTPloginComponent } from './shared/auth/otplogin/otplogin.component';
import { UsersComponent } from './views/user/users/users.component';

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

  { path: 'account', component: AccountComponent, canActivate: [AuthGuard] },

  {
    path: 'user', component: UserComponent, canActivate: [AuthGuard],
    children: [
      { path: '', component: HomeUserComponent },
      // { path: 'account', component: AccountComponent },
      { path: 'users', component: UsersComponent },
      { path: 'files', component: FilesComponent },
      { path: 'shared_files', component: FilesComponent }
    ]
  },

  { path: 'cgv', component: CgvComponent },
  { path: 'pricing', component: PricingComponent },

  { path: 'checkout/:id', component: CheckoutComponent },

  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'otplogin', component: OTPloginComponent },
  { path: 'reset-password', component: ResetPasswordComponent },

  // { path: 'account', redirectTo: 'user/account' },

  { path: '**', component: HomeComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'enabled', preloadingStrategy: PreloadAllModules, useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
