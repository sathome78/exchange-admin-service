import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PopupService {
  onExternalBalancesPopupListener = new Subject<boolean>();
  onMonitoringPopupListener = new Subject<boolean>();

  constructor() { }

  toggleExternalBalancesPopup(state: boolean) {
    this.onExternalBalancesPopupListener.next(state);
  }

  toggleMonitoringPopup(state: boolean) {
    this.onMonitoringPopupListener.next(state);
  }

  getExternalBalancesPopupListener(): Subject<boolean> {
    return this.onExternalBalancesPopupListener;
  }

  getMonitoringPopupListener(): Subject<boolean> {
    return this.onMonitoringPopupListener;
  }
}
