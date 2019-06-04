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

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FinMonitoringModule,
    AnalyticsModule,
    NotificationsModule,
    UsersModule,
    LiquidityModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
