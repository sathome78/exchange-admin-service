import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app-component/app.component';
import { HeaderComponent } from './header/header.component';
import { FinMonitoringModule } from '../fin-monitoring/fin-monitoring.module';
import { AnalyticsModule } from '../analytics/analytics.module';
import { NotificationsModule } from '../notifications/notifications.module';
import { UsersModule } from '../users/users.module';
import { LiquidityModule } from '../liquidity/liquidity.module';
import { AuthModule } from '../auth/auth.module';
import { SharedModule } from '../shared/shared.module';
import { AuthInterceptor } from '../services/auth.interceptor';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    SharedModule,
    FinMonitoringModule,
    AnalyticsModule,
    NotificationsModule,
    UsersModule,
    LiquidityModule,
    AuthModule,
  ],
  providers: [ {
    provide: HTTP_INTERCEPTORS,
    useClass: AuthInterceptor,
    multi: true
}],
  bootstrap: [AppComponent]
})
export class AppModule { }
