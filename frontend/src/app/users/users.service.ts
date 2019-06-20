import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UsersService {
  baseUrl: string = 'http://localhost:7777/api/user-information';

  constructor(private http: HttpClient) { }

  getUsers(query?) {
    return this.http.get(this.baseUrl + `/all`, { params: query ? query : {} });
  }

  getReports(query?) {
    return this.http.get(this.baseUrl + `/report`, { params: query ? query : {} });
  }

}
