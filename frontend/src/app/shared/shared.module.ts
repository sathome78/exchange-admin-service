import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UtilsService } from './services/utils.service';
import { ApiService } from './services/api.service';
import { HttpClientModule } from '@angular/common/http';
import { LoaderService } from './services/loader.service';
import { PaginationComponent } from './components/pagination/pagination.component';

@NgModule({
  declarations: [
    PaginationComponent
  ],
  imports: [
    CommonModule,
    HttpClientModule
  ],
  providers: [
    UtilsService,
    LoaderService,
    ApiService
  ],
  exports: [
    PaginationComponent,
  ]
})
export class SharedModule { }
