import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WellcomeAdminComponent } from './wellcome-admin.component';

describe('WellcomeAdminComponent', () => {
  let component: WellcomeAdminComponent;
  let fixture: ComponentFixture<WellcomeAdminComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WellcomeAdminComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WellcomeAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
