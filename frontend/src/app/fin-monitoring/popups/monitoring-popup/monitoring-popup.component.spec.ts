import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MonitoringPopupComponent } from './monitoring-popup.component';

describe('MonitoringPopupComponent', () => {
  let component: MonitoringPopupComponent;
  let fixture: ComponentFixture<MonitoringPopupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MonitoringPopupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MonitoringPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
