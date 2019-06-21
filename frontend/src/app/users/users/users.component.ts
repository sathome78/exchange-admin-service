import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { UsersService } from '../users.service';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {

  fieldKeys = [];
  fields = {
    ip: 'IP',
    email: 'E-mail',
    country: 'Страна',
    balance_usd: 'Сумма на балансе в $',
    registration: 'Дата регистрации',
    lastEntry: 'Последний вход',
    phone: 'Номер телефона',
    verificationStatus: 'Статус верификации',
    type: 'Тип юзера',
    activity_status: 'Статус активности'
  };

  users = [{
    id: '234567890',
    ip: '123452322543543544',
    email: 'qwerty@gmail.com',
    country: 'Страна',
    balance_usd: '10000',
    registration: '19/02/19',
    lastEntry: '18/05/19',
    phone: '+380961923734',
    verificationStatus: '',
    type: 'Тип юзера',
    activity_status: 'Статус активности'
  },
  {
    id: '234567890',
    ip: '123452322543543544',
    email: 'qwerty@gmail.com',
    country: 'Страна',
    balance_usd: '10000',
    registration: '19/02/19',
    lastEntry: '18/05/19',
    phone: '+380961923734',
    verificationStatus: '',
    type: 'Тип юзера',
    activity_status: 'Статус активности'
  }]

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private usersService: UsersService
  ) { }

  ngOnInit() {
    this.fieldKeys = Object.keys(this.fields);
    // this.usersService.getUsers();
  }

  showUserDetails(id) {
    this.router.navigate([id], { relativeTo: this.activatedRoute });
  }
}
