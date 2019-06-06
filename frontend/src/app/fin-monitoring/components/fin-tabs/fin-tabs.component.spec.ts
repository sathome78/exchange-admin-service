import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FinTabsComponent } from './fin-tabs.component';

describe('FinTabsComponent', () => {
  let component: FinTabsComponent;
  let fixture: ComponentFixture<FinTabsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FinTabsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FinTabsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
