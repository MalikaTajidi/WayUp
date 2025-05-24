import { TestBed } from '@angular/core/testing';

import { CompanySuggestionService } from './company-suggestion.service';

describe('CompanySuggestionService', () => {
  let service: CompanySuggestionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CompanySuggestionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
