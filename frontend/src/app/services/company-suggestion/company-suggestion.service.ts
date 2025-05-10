import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CompanySuggestion } from '../../models/company-suggestion';

@Injectable({
  providedIn: 'root'
})
export class CompanySuggestionService {

  private apiUrl = 'http://localhost:8080/api/company-suggestions';

  constructor(private http: HttpClient) { }

    getSuggestions(userId: number): Observable<CompanySuggestion[]> {
    return this.http.get<CompanySuggestion[]>(this.apiUrl);
  }
}
