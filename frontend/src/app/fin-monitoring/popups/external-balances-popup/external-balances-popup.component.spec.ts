import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ExternalBalancesPopupComponent } from './external-balances-popup.component';

describe('ExternalBalancesPopupComponent', () => {
  let component: ExternalBalancesPopupComponent;
  let fixture: ComponentFixture<ExternalBalancesPopupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ExternalBalancesPopupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExternalBalancesPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
