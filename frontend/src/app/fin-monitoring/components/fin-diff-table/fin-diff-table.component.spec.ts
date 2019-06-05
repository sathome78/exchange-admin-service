import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FinDiffTableComponent } from './fin-diff-table.component';

describe('FinDiffTableComponent', () => {
  let component: FinDiffTableComponent;
  let fixture: ComponentFixture<FinDiffTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FinDiffTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FinDiffTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
