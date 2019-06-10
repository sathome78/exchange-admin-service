import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FinMonitoringComponent } from './fin-monitoring/fin-monitoring.component';
import { FinTabsComponent } from './components/fin-tabs/fin-tabs.component';
import { FinSumTableComponent } from './components/fin-sum-table/fin-sum-table.component';
import { FinSumCardsComponent } from './components/fin-sum-cards/fin-sum-cards.component';
import { FinDiffCardsComponent } from './components/fin-diff-cards/fin-diff-cards.component';
import { FinDiffTableComponent } from './components/fin-diff-table/fin-diff-table.component';
import { ExternalBalancesPopupComponent } from './popups/external-balances-popup/external-balances-popup.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MonitoringPopupComponent } from './popups/monitoring-popup/monitoring-popup.component';

@NgModule({
  declarations: [
    FinMonitoringComponent,
    FinTabsComponent,
    FinSumTableComponent,
    FinSumCardsComponent,
    FinDiffCardsComponent,
    FinDiffTableComponent,
    ExternalBalancesPopupComponent,
    MonitoringPopupComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule
  ]
})
export class FinMonitoringModule { }
