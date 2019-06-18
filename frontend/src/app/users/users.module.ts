import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsersComponent } from './users/users.component';
import { UserDetailsComponent } from './user-details/user-details.component';
import { RouterModule } from '@angular/router';
import { UserTableComponent } from './components/user-table/user-table.component';

const routes = [
  {
    path: ':id',
    component: UserDetailsComponent
  }
]

@NgModule({
  declarations: [
    UsersComponent,
    UserDetailsComponent,
    UserTableComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes)
  ]
})
export class UsersModule { }
