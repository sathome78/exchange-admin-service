import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  
  constructor() { }

  ngOnInit() {
  }

  get isLogin() {
    return window.location.pathname.indexOf('login') >= 0;
  }

  get isUserLoggedIn() {
    return !!window.localStorage.getItem('token');
  }

  logout(){
    window.localStorage.removeItem('token');
  }
}
