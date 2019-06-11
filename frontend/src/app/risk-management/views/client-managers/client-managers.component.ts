import { Component, OnInit } from '@angular/core';

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
  ]

  constructor() { }

  ngOnInit() {
  }

}
