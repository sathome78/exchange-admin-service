import { Component, OnInit } from '@angular/core';
import { fields } from '../../constants/trading.constant';

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
  ];
  
  fields = fields;

  values = [{
    market: 'BTC/USD',
    type: 'Sell',
    category: 'Donor',
    price: '6 180',
    amount: '0.1',
    free: '0.00002',
    total: '0.10002',
    date_of_transaction: '12/12/18 16:10',
    balance: '14.09'
  },{
    market: 'ETH/BTC',
    type: 'Buy',
    category: 'Exchange',
    price: '0.036',
    amount: '10',
    free: '0.02',
    total: '10.02',
    date_of_transaction: '12/12/18 12:57',
    balance: '12.02'
  }];

  constructor() { }

  ngOnInit() {
  }

}
