import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-risk-management',
  templateUrl: './risk-management.component.html',
  styleUrls: ['./risk-management.component.scss']
})
export class RiskManagementComponent implements OnInit {

  dashboard = [
    {
      name: 'Перекрытия',
      value: '$35,343,213',
      bottomValue: '705 BTC'
    },
    {
      name: 'Внутренняя торговля',
      value: '$35,343,213',
      bottomValue: '705 BTC'
    },
    {
      name: 'Внешняя комиссия',
      value: '$35,343,213',
      bottomValue: '705 BTC'
    },
    {
      name: 'Торговый доход',
      value: '$1,168,213',
      bottomValue: '705 BTC'
    },
    {
      name: 'Доход от комиссий',
      value: '$1,348,000',
      bottomValue: '705 BTC'
    },
    {
      name: 'Колличество внешних клиентов',
      value: 748000
    }
  ]
  constructor() { }

  ngOnInit() {
  }

}
