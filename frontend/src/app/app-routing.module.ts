import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {WellcomeAdminComponent} from './wellcome-admin/wellcome-admin.component';

const routes: Routes = [
  // permit all
  {path: 'login', component: LoginComponent},
  {path: 'welcome-admin', component: WellcomeAdminComponent},

  {path: '', pathMatch: 'full', redirectTo: 'login'},
  {path: '**', redirectTo: 'login'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {onSameUrlNavigation: 'reload'})],
  exports: [RouterModule]
})
export class AppRoutingModule {}
