import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { PopupService } from 'src/app/shared/services/popup.service';

@Component({
  selector: 'app-fin-diff-table',
  templateUrl: './fin-diff-table.component.html',
  styleUrls: ['./fin-diff-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FinDiffTableComponent implements OnInit {

  constructor(
    public popupService: PopupService
  ) { }

  ngOnInit() { }

  showDetails() {
    this.popupService.toggleMonitoringPopup(true);
  }

}
