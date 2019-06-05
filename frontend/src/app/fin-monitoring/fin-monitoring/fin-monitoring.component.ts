import { Component, OnInit } from '@angular/core';



@Component({
  selector: 'app-fin-monitoring',
  templateUrl: './fin-monitoring.component.html',
  styleUrls: ['./fin-monitoring.component.scss']
})
export class FinMonitoringComponent implements OnInit {

  tabs = {
    SUM: 'sum',
    DIFF: 'diff',
  };

  currTab = this.tabs.DIFF;

  constructor() { }

  ngOnInit() {
  }

  toggleTab(tab: string): void {
    this.currTab = tab;
  }

}
