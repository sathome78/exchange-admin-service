import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FinMonitoringComponent } from './fin-monitoring/fin-monitoring.component';
import { FinTabsComponent } from './components/fin-tabs/fin-tabs.component';
import { FinSumTableComponent } from './components/fin-sum-table/fin-sum-table.component';
import { FinSumCardsComponent } from './components/fin-sum-cards/fin-sum-cards.component';
import { FinDiffCardsComponent } from './components/fin-diff-cards/fin-diff-cards.component';
import { FinDiffTableComponent } from './components/fin-diff-table/fin-diff-table.component';

@NgModule({
  declarations: [
    FinMonitoringComponent,
    FinTabsComponent,
    FinSumTableComponent,
    FinSumCardsComponent,
    FinDiffCardsComponent,
    FinDiffTableComponent
  ],
  imports: [
    CommonModule
  ]
})
export class FinMonitoringModule { }
