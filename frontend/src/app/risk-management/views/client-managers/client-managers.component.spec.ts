import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientManagersComponent } from './client-managers.component';

describe('ClientManagersComponent', () => {
  let component: ClientManagersComponent;
  let fixture: ComponentFixture<ClientManagersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ClientManagersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClientManagersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
