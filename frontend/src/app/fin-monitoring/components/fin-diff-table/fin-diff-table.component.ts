import { Component, OnInit } from '@angular/core';
import { PopupService } from 'src/app/services/popup.service';

@Component({
  selector: 'app-fin-diff-table',
  templateUrl: './fin-diff-table.component.html',
  styleUrls: ['./fin-diff-table.component.scss']
})
export class FinDiffTableComponent implements OnInit {

  constructor(
    private popupService: PopupService
    ) { }

  ngOnInit() {
  }

  showDetails() {
    this.popupService.toggleMonitoringPopup(true);
  }

}
