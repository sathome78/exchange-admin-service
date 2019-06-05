import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FinDiffCardsComponent } from './fin-diff-cards.component';

describe('FinDiffCardsComponent', () => {
  let component: FinDiffCardsComponent;
  let fixture: ComponentFixture<FinDiffCardsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FinDiffCardsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FinDiffCardsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
