import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { UtilsService } from 'src/app/shared/utils.service';

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
  }

}
