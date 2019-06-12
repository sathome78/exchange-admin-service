import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-analytics-table',
  templateUrl: './analytics-table.component.html',
  styleUrls: ['./analytics-table.component.scss']
})
export class AnalyticsTableComponent implements OnInit {

  @Input() fields;
  @Input() values = [{}, {}, {}];
  fieldKeys = [];

  constructor() { }

  ngOnInit() {
    this.fieldKeys = Object.keys(this.fields);
  }

}
