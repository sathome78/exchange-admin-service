import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UtilsService } from './utils.service';
import { ApiService } from '../services/api.service';
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    HttpClientModule
  ],
  providers: [
    UtilsService,
    ApiService
  ]
})
export class SharedModule { }
