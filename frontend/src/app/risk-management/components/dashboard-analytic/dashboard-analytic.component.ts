import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-dashboard-analytic',
  templateUrl: './dashboard-analytic.component.html',
  styleUrls: ['./dashboard-analytic.component.scss']
})
export class DashboardAnalyticComponent implements OnInit {

  @Input() items;
  @Input() chartData;

  public pieChartOptions = {
    responsive: true,
    legend: {
      position: 'top',
    },
    plugins: {
      datalabels: {
        formatter: (value, ctx) => {
          const label = ctx.chart.data.labels[ctx.dataIndex];
          return label;
        },
      },
    }
  };
  public pieChartData = [300, 500, 100];
  public pieChartType = 'pie';
  public pieChartLegend = true;

  constructor() { }

  ngOnInit() {
  }

}
