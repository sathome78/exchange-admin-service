import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TradingComponent } from './views/trading/trading.component';
import { RiskManagementComponent } from './views/risk-management/risk-management.component';
import { ClientManagersComponent } from './views/client-managers/client-managers.component';
import { JointAccountComponent } from './views/joint-account/joint-account.component';
import { DashboardAnalyticComponent } from './components/dashboard-analytic/dashboard-analytic.component';

@NgModule({
  declarations: [
    RiskManagementComponent,
    ClientManagersComponent,
    JointAccountComponent,
    TradingComponent,
    DashboardAnalyticComponent
  ],
  imports: [
    CommonModule
  ]
})
export class RiskManagementModule { }
