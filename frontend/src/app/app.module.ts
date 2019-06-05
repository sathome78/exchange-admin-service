import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {LoginComponent} from './login/login.component';

import {ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {WellcomeAdminComponent} from './wellcome-admin/wellcome-admin.component';
import {AppRoutingModule} from './app-routing.module';
import {ApiService} from './services/api.service';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    WellcomeAdminComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [ApiService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
