import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable()
export class ApiService {
  public apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  login(loginPayload) {
    const creds = btoa('admin:admin-secret');
    console.log('CREDS: ' + creds);
    const headers = {
      Authorization: 'Basic ' + creds,
      'Content-type': 'application/x-www-form-urlencoded'
    };
    return this.http.post('http://localhost:7777/' + 'oauth/token', loginPayload, {headers});
  }

  // getUsers() {
  //   return this.http.get(this.baseUrl + 'user?access_token=' + JSON.parse(window.sessionStorage.getItem('token')).access_token);
  // }
  //

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
