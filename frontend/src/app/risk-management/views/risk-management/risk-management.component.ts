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
    user_id: 43242432,
    email: 'not found',
    deposit: 2,
    withdrawal: 2,
    deposit_more10: false,
    withdrawal_more10: false,
    commission_trading_per_day: 0,
    commission_transfer_per_day: 0,
    commission_deposit_withdrawal_per_day: 0,
    commission_trading_per_week: 0,
    commission_transfer_per_week: 0,
    commission_deposit_withdrawal_per_week: 0,
    commission_trading_per_month: 0,
    commission_transfer_per_month: 0,
    commission_deposit_withdrawal_per_month: 0,
    commission_trading_per_year: 2,
    commission_transfer_per_year: 0.4,
    commission_deposit_withdrawal_per_year: 0.4,
    change_balance_per_day: 0,
    change_balance_per_week: 0,
    change_balance_per_month: 0,
    change_balance_per_year: -20,
    trade_number_per_day: 0,
    trade_amount_per_day: 0,
    trade_number_with_refill_per_day: 0,
    trade_number_with_withdraw_per_day: 0,
    trade_number_per_week: 0,
    trade_amount_per_week: 0,
    trade_number_with_refill_per_week: 0,
    trade_number_with_withdraw_per_week: 0,
    trade_number_per_month: 0,
    trade_amount_per_month: 0,
    trade_number_with_refill_per_month: 0,
    trade_number_with_withdraw_per_month: 0,
    trade_number_per_year: 16,
    trade_amount_per_year: 40,
    trade_number_with_refill_per_year: 16,
    trade_number_with_withdraw_per_year: 16,
    no_deals_but_refilled_per_day: false,
    no_deals_but_withdraw_and_refilled_per_day: false,
    no_deals_but_refilled_per_week: false,
    no_deals_but_withdraw_and_refilled_per_week: false,
    no_deals_but_refilled_per_month: false,
    no_deals_but_withdraw_and_refilled_per_month: false,
    no_deals_but_refilled_per_year: false,
    no_deals_but_withdraw_and_refilled_per_year: false
  }
  ]

  constructor() { }

  ngOnInit() {
  }

}
