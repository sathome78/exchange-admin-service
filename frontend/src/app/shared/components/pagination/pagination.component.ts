import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent implements OnInit {
  @Input() public total: number;
  @Input() public limit: number;
  @Input() public offset: number;
  @Output() public changePage: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit() {
  }

  get pages() {
    return Math.ceil(this.total / this.limit);
  }

  get pagesArray() {
    return (Array.apply(null, {length: this.pages + 1}).map(Number.call, Number)).slice(1);
  }

  get activePage() {
    return Math.ceil((this.offset + 1) / this.limit);
  }

  onChangePage(e, page: number): void {
    e.preventDefault();
    const newOffset = (page - 1) * this.limit;
    this.changePage.emit(newOffset);
  }
}
