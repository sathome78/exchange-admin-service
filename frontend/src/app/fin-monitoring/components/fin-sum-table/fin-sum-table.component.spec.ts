import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FinSumTableComponent } from './fin-sum-table.component';

describe('FinSumTableComponent', () => {
  let component: FinSumTableComponent;
  let fixture: ComponentFixture<FinSumTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FinSumTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FinSumTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
