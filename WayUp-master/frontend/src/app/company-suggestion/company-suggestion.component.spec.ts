import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompanySuggestionComponent } from './company-suggestion.component';

describe('CompanySuggestionComponent', () => {
  let component: CompanySuggestionComponent;
  let fixture: ComponentFixture<CompanySuggestionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompanySuggestionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompanySuggestionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
