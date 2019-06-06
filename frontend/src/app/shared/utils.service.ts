import { Injectable } from '@angular/core';
import { ValidatorFn, AbstractControl } from '@angular/forms';

@Injectable()
export class UtilsService {

  // tslint:disable-next-line: max-line-length
  private pattern = /(^$|(^([^<>()\[\]\\,;:\s@"]+(\.[^<>()\[\]\\,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$)/;
  // tslint:disable-next-line: max-line-length
  private passwordPattern = /(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]|(?=.*[A-Za-z][!@#\$%\^&\*<>\.\(\)\-_=\+\'])[A-Za-z!@#\$%\^&\*<>\.\(\)\-_=\+\'\d]{8,40}/ig;
  private checkCyrillic = /[а-яА-ЯёЁ]/ig;

  emailValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      const forbidden = new RegExp(this.pattern).test(control.value ? control.value.trim() : '');
      const excludeCyrillic = new RegExp(this.checkCyrillic).test(control.value ? control.value.trim() : '');
      return forbidden && !excludeCyrillic ? null : { 'emailInvalid': {value: control.value.trim()} } ;
    };
  }

  passwordCombinationValidator(): ValidatorFn {
    return (control: AbstractControl): {[key: string]: any} | null => {
      const value = control.value ? control.value.trim() : ''
      const result  = new RegExp(this.passwordPattern).test(value);
      const excludeCyrillic = new RegExp(this.checkCyrillic).test(value)
      return result && !excludeCyrillic ? null : {'passwordValidation': true};
    };
  }

}
