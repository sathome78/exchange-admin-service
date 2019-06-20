import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserReferalTableComponent } from './user-referal-table.component';

describe('UserReferalTableComponent', () => {
  let component: UserReferalTableComponent;
  let fixture: ComponentFixture<UserReferalTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserReferalTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserReferalTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
