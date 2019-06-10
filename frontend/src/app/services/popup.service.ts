import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PopupService {
  onExternatBalancesPopupListener = new Subject<boolean>();
  onMonitoringPopupListener = new Subject<boolean>();

  constructor() { }

  toggleExternatBalancesPopup(state: boolean) {
    this.onExternatBalancesPopupListener.next(state);
  }

  toggleMonitoringPopup(state: boolean) {
    this.onMonitoringPopupListener.next(state);
  }

  getExternatBalancesPopupListener(): Subject<boolean> {
    return this.onExternatBalancesPopupListener;
  }

  getMonitoringPopupListener(): Subject<boolean> {
    return this.onMonitoringPopupListener;
  }
}
