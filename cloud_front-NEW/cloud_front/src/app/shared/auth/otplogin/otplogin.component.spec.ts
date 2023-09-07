import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OTPloginComponent } from './otplogin.component';

describe('OtploginComponent', () => {
  let component: OTPloginComponent;
  let fixture: ComponentFixture<OTPloginComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OTPloginComponent]
    });
    fixture = TestBed.createComponent(OTPloginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
