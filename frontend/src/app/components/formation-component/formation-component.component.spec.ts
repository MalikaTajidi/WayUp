import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormationComponentComponent } from './formation-component.component';  

describe('FormationComponentComponent', () => {
  let component: FormationComponentComponent;
  let fixture: ComponentFixture<FormationComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormationComponentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormationComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
