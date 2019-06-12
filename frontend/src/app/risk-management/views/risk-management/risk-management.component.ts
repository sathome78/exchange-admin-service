import { Component, OnInit } from '@angular/core';
import { fields } from '../../constants/risk-managemant.constant';

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
  ];
  fields = fields;

  values = [{
    user_id: '1234',
    deposit: '1344312',
    withdrawal: '1321',
    deposit_more10: '1232132',
    withdrawal_more10: '1232131',
    commission_trading_per_day: '124324',
    commission_transfer_per_day: '154325',
    commission_deposit_withdrawal_per_day: '1241224',
    commission_trading_per_week: '12412',
    commission_transfer_per_week: '142214',
    commission_deposit_withdrawal_per_week: '14143',
    commission_trading_per_month: '1242',
    commission_transfer_per_month: '21432',
    commission_deposit_withdrawal_per_month: '14325432',
    change_balance_per_day: '3241',
    change_balance_per_week: '4531',
    change_balance_per_month: '235452',
    change_balance_per_year: '543654343',
    number_transactions_per_day: '4363',
    amount_transactions_per_day: '256256',
    number_transactions_per_day_flag: true,
    amount_transactions_per_day_flag: false,
    number_transactions_per_week: '32',
    amount_transactions_per_week: '543254',
    number_transactions_per_week_flag: '4364364',
    amount_transactions_per_week_flag: '252634',
    number_transactions_per_month: '5432534',
    amount_transactions_per_month: '25423',
    number_transactions_per_month_flag: '2345',
    amount_transactions_per_month_flag: '2345',
    number_transactions_per_year: '2462623',
    amount_transactions_per_year: '',
    number_transactions_per_year_flag: '',
    amount_transactions_per_year_flag: '',
    no_deals_per_day: '',
    no_deals_per_week: '',
    no_deals_per_month: '',
    no_deals_per_year: '',
  }
]

  constructor() { }

  ngOnInit() {
  }

}
