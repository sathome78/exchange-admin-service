import { Component, OnInit } from '@angular/core';
import { PopupService } from 'src/app/services/popup.service';

@Component({
  selector: 'app-fin-sum-table',
  templateUrl: './fin-sum-table.component.html',
  styleUrls: ['./fin-sum-table.component.scss']
})
export class FinSumTableComponent implements OnInit {

  constructor(
    private popupService: PopupService
    ) { }

  ngOnInit() {
  }

  showDetails(){
    this.popupService.toggleExternalBalancesPopup(true);
  }

}
