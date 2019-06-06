import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FinSumCardsComponent } from './fin-sum-cards.component';

describe('FinSumCardsComponent', () => {
  let component: FinSumCardsComponent;
  let fixture: ComponentFixture<FinSumCardsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FinSumCardsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FinSumCardsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
