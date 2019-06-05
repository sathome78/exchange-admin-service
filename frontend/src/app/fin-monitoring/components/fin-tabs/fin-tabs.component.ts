import { Component, OnInit, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-fin-tabs',
  templateUrl: './fin-tabs.component.html',
  styleUrls: ['./fin-tabs.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FinTabsComponent implements OnInit {

  @Input() public tabs;
  @Input() public currTab;
  @Output() public toggleTab: EventEmitter<string> = new EventEmitter();

  constructor() { }

  ngOnInit() {
  }

}
