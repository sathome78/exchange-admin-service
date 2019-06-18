import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss']
})
export class UserDetailsComponent implements OnInit {

  userTypes = [ 'User', 'Admin', 'Super Admin', 'VIP User', 'BOT'];

  constructor() { }

  ngOnInit() {
  }

}
