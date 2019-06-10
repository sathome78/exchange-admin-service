import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable} from 'rxjs/index';

@Injectable()
export class ApiService {

  constructor(private http: HttpClient) { }
  baseUrl: string = 'http://localhost:7777/users/';

  login(loginPayload) {
    const creds = btoa('admin:$2a$10$sHGQ5i.izbR/zOOibOdqP.Z48Hz4Vpu.nMqjbB.FrUP0jROrpep9.');
    console.log('CREDS: ' + creds);
    const headers = {
      'Authorization': 'Basic YWRtaW46YWRtaW4tc2VjcmV0',
      'Content-type': 'application/x-www-form-urlencoded'
    }
    return this.http.post('http://localhost:7777/' + 'oauth/token', loginPayload, {headers});
  }

  getUsers() {
    return this.http.get(this.baseUrl + 'user?access_token=' + JSON.parse(window.localStorage.getItem('token')).access_token);
  }
  
  // getUserById(id: number) {
  //   return this.http.get(this.baseUrl + 'user/' + id + '?access_token=' + JSON.parse(window.sessionStorage.getItem('token')).access_token);
  // }
  //
  // createUser(user: User){
  //   return this.http.post(this.baseUrl + 'user?access_token=' + JSON.parse(window.sessionStorage.getItem('token')).access_token, user);
  // }
  //
  // updateUser(user: User) {
  //   return this.http.put(this.baseUrl + 'user/' + user.id + '?access_token=' + JSON.parse(window.sessionStorage.getItem('token')).access_token, user);
  // }
  //
  // deleteUser(id: number){
  //   return this.http.delete(this.baseUrl + 'user/' + id + '?access_token=' + JSON.parse(window.sessionStorage.getItem('token')).access_token);
  // }
}
