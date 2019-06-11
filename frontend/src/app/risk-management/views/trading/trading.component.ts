import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-trading',
  templateUrl: './trading.component.html',
  styleUrls: ['./trading.component.scss']
})
export class TradingComponent implements OnInit {

  dashboard = [
    {
      name: 'Обьем лимин ордеров BUY',
      value: '$35,343,213',
      bottomValue: '705 BTC'
    },
    {
      name: 'Обьем лимин ордеров SELL',
      value: '$35,343,213',
      bottomValue: '705 BTC'
    },
    {
      name: 'Обьем завершенных ордеров',
      value: '$35,343,213',
      bottomValue: '705 BTC'
    },
    {
      name: 'Support BTC',
      value: '$1,168,213',
      bottomValue: '705 BTC'
    },
    {
      name: 'Resistense BTC',
      value: '$1,348,000',
      bottomValue: '705 BTC'
    }
  ]
  constructor() { }

  ngOnInit() {
  }

}
