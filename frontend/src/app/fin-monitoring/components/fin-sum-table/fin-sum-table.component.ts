import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { PopupService } from 'src/app/shared/services/popup.service';

@Component({
  selector: 'app-fin-sum-table',
  templateUrl: './fin-sum-table.component.html',
  styleUrls: ['./fin-sum-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FinSumTableComponent implements OnInit {
  constructor(
    public popupService: PopupService
  ) { }

  ngOnInit() {}

  showDetails() {
    this.popupService.toggleExternalBalancesPopup(true);
  }

}
