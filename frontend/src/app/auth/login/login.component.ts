import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { UtilsService } from 'src/app/shared/utils.service';
import {ApiService} from '../../services/api.service';
import {HttpParams} from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  loading = false;

  constructor(
    public utilsService: UtilsService,
    private apiService: ApiService
  ) { }

  ngOnInit() {
    this.initForm();
  }

  initForm() {
    this.loginForm =  new FormGroup({
      email: new FormControl('', {validators: [Validators.required, this.utilsService.emailValidator()]}),
      password: new FormControl('', {validators: [Validators.required, this.utilsService.passwordCombinationValidator()]})
    });
  }

  submitForm(e) {
    console.log(e.target.value);
    if (this.loginForm.invalid) {
      return;
    }
    const body = new HttpParams()
      .set('username', this.loginForm.controls.email.value)
      .set('password', this.loginForm.controls.password.value)
      .set('grant_type', 'password');

    this.apiService.login(body.toString()).subscribe(data => {
      window.sessionStorage.setItem('token', JSON.stringify(data));
      console.log(window.sessionStorage.getItem('token'));
    }, error => {
      alert(error.error);
    });
  }

}
