import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserLinksTableComponent } from './user-links-table.component';

describe('UserLinksTableComponent', () => {
  let component: UserLinksTableComponent;
  let fixture: ComponentFixture<UserLinksTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserLinksTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserLinksTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
