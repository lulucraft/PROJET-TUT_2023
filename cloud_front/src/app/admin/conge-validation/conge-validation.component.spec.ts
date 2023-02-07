import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CongeValidationComponent } from './conge-validation.component';

describe('ValidateCongeComponent', () => {
  let component: CongeValidationComponent;
  let fixture: ComponentFixture<CongeValidationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CongeValidationComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CongeValidationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
