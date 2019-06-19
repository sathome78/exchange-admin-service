import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsersComponent } from './users/users.component';
import { UserDetailsComponent } from './user-details/user-details.component';
import { UserBalanceTableComponent } from './components/user-balance-table/user-balance-table.component';
import { UserLogsTableComponent } from './components/user-logs-table/user-logs-table.component';
import { UserLinksTableComponent } from './components/user-links-table/user-links-table.component';
import { UserReferalTableComponent } from './components/user-referal-table/user-referal-table.component';

@NgModule({
  declarations: [
    UsersComponent,
    UserDetailsComponent,
    UserBalanceTableComponent,
    UserLogsTableComponent,
    UserLinksTableComponent,
    UserReferalTableComponent
  ],
  imports: [
    CommonModule
  ]
})
export class UsersModule { }
