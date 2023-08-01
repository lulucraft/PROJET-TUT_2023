import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule, isDevMode } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ServiceWorkerModule } from '@angular/service-worker';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { environment } from 'src/environments/environment';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { TokenHttpInterceptorInterceptor } from './interceptor/token-http-interceptor.interceptor';
import { MaterialModule } from './material/material.module';
import { RegisterComponent } from './shared/auth/register/register.component';
import { LoginComponent } from './shared/auth/login/login.component';
import { FooterComponent } from './shared/footer/footer.component';
import { HeaderComponent } from './shared/header/header.component';
import { SidebarLeftComponent } from './shared/sidebar/sidebar-left/sidebar-left.component';
import { SidebarSettingsComponent } from './shared/sidebar/sidebar-settings/sidebar-settings.component';
import { HomeComponent } from './shared/home/home.component';
import { MainComponent } from './views/main/main.component';
import { AccountComponent } from './views/main/account/account.component';
import { FilesComponent } from './views/main/files/files.component';
import { PricingComponent } from './shared/pricing/pricing.component';
import { CheckoutComponent } from './checkout/checkout.component';
import { ResetPasswordComponent } from './shared/auth/reset-password/reset-password.component';
import { NgxPayPalModule } from 'ngx-paypal';

registerLocaleData(localeFr, 'fr-FR');

@NgModule({
  declarations: [
    AppComponent,

    RegisterComponent,
    LoginComponent,
    HeaderComponent,
    FooterComponent,
    SidebarSettingsComponent,
    SidebarLeftComponent,
    HomeComponent,

    MainComponent,
    AccountComponent,

    FilesComponent,
     PricingComponent,
     CheckoutComponent,
     ResetPasswordComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,

    MaterialModule,

    NgxPayPalModule,

    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: !isDevMode(),
      // Register the ServiceWorker as soon as the application is stable
      // or after 30 seconds (whichever comes first).
      registrationStrategy: 'registerWhenStable:30000'
    }),
  ],
  providers: [
    { provide: "API_BASE_URL", useValue: environment.apiRoot },
    { provide: HTTP_INTERCEPTORS, useClass: TokenHttpInterceptorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
