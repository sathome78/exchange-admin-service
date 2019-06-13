import { Component, OnInit } from '@angular/core';
import { fields } from '../../constants/client-manager.constant';

@Component({
  selector: 'app-client-managers',
  templateUrl: './client-managers.component.html',
  styleUrls: ['./client-managers.component.scss']
})
export class ClientManagersComponent implements OnInit {

  dashboard = [
    {
      name: 'Новый счет',
      value: 33345
    },
    {
      name: 'Наполнение счета',
      value: 211
    },
    {
      name: 'Счета пополненые первый раз',
      value: 3
    },
    {
      name: 'Неактивные счета',
      value: 5
    },
    {
      name: 'Счета с обнуленным балансом',
      value: 1
    },
    {
      name: 'Реанимированный счет',
      value: 1
    },
    {
      name: 'Активные счета',
      value: 12
    }
  ];
  fields = fields;

  values = [{
    user_id: '1234',
    email: 'qwerty@gmail.com',
    phone: '+123456789012',
    messanger: '@qwertyuio',
    visit: '1',
    open_account: '0',
    new_account: '1',
    new_account_not_refilled_48h: '',
    new_account_not_refilled_7d: '',
    new_account_not_refilled_30d: '',
    new_account_not_refilled_90d: '',
    refilled_first_time: '',
    refilled_first_time_48h: '',
    refilled_first_time_7d: '',
    refilled_first_time_30d: '',
    refilled_first_time_90d: '',
    account_replenished_first_operation: '',
    active_client: '',
    account_refilled_second_time: '',
    recharge_basic: '',
    recharge_profi: '',
    recharge_gold: '',
    recharge_executive: '',
    bargaining_basic: '',
    bargaining_profi: '',
    bargaining_gold: '',
    bargaining_executive: '',
    no_bargaining: '',
    client_not_trade_7d: '',
    client_not_trade_30d: '',
    client_not_trade_90d: '',
    client_not_trade_and_leaves: '',
    client_not_trade_7d_withdraw_money:'',
    client_not_trade_30d_withdraw_money: '',
    client_not_trade_90d_withdraw_money: '',
    client_reset_balance: '',
    customer_resuscitation: '',
    customer_activity: '',
    client_active_30d: '',
    client_active_90d: '',
    client_active_1y: '',
    client_active_3y: '',
    client_active_5y: ''
  }];

  constructor() { }

  ngOnInit() {
  }

}
