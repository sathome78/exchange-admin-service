import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FinMonitoringComponent } from './fin-monitoring.component';

describe('FinMonitoringComponent', () => {
  let component: FinMonitoringComponent;
  let fixture: ComponentFixture<FinMonitoringComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FinMonitoringComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FinMonitoringComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
