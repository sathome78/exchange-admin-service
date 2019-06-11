import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-joint-account',
  templateUrl: './joint-account.component.html',
  styleUrls: ['./joint-account.component.scss']
})
export class JointAccountComponent implements OnInit {

  dashboard = [
    {
      name: 'Новый счет',
      value: 33345
    },
    {
      name: 'Пополнение счета',
      value: 211
    },
    {
      name: 'Конвертация P/N',
      value: 3
    },
    {
      name: 'Активные счета',
      value: 5
    },
    {
      name: 'Счета с обнуленным балансом',
      value: 1
    },
    {
      name: 'Реанимированный счет',
      value: 0
    },
    {
      name: 'Доход от комиссии',
      value: '$1234567'
    },
    {
      name: 'Доход от объема торгов',
      value: '$111234567'
    }
  ]

  constructor() { }

  ngOnInit() {
  }

}
