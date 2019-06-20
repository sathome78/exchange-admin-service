import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { FinMonitoringComponent } from '../fin-monitoring/fin-monitoring/fin-monitoring.component';
import { LiquidityComponent } from '../liquidity/liquidity/liquidity.component';
import { UsersComponent } from '../users/users/users.component';
import { AnalyticsComponent } from '../analytics/analytics/analytics.component';
import { NotificationsComponent } from '../notifications/notifications/notifications.component';
import { LoginComponent } from '../auth/login/login.component';
import { RiskManagementComponent } from '../risk-management/views/risk-management/risk-management.component';
import { ClientManagersComponent } from '../risk-management/views/client-managers/client-managers.component';
import { JointAccountComponent } from '../risk-management/views/joint-account/joint-account.component';
import { TradingComponent } from '../risk-management/views/trading/trading.component';
import { UserDetailsComponent } from '../users/user-details/user-details.component';

const routes: Routes = [
  {path: 'fin-monitoring', component: FinMonitoringComponent},
  {path: 'liquidity', component: LiquidityComponent},
  {path: 'users', component: UsersComponent},
  {path: 'users/:id', component: UserDetailsComponent},
  {path: 'analytics', component: AnalyticsComponent},
  {path: 'notifications', component: NotificationsComponent},
  {path: 'login', component: LoginComponent},

  {path: 'risk-management', component: RiskManagementComponent},
  {path: 'client-managers', component: ClientManagersComponent},
  {path: 'joint-account', component: JointAccountComponent},
  {path: 'trading', component: TradingComponent},

  // {path: 'login', component: DashboardComponent},

  // {path: 'funds', loadChildren: 'app/funds/funds.module#FundsModule', canActivate: [AuthGuard], canActivateChild: [AuthGuard]}

  {path: '', pathMatch: 'full', redirectTo: 'fin-monitoring'},
  {path: '**', redirectTo: 'fin-monitoring'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
