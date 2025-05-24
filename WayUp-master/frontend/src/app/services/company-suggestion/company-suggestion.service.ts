import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CompanySuggestion } from '../../models/company-suggestion';

@Injectable({
  providedIn: 'root'
})
export class CompanySuggestionService {

   private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  getSuggestions(userId: number): Observable<CompanySuggestion[]> {
    const url = `${this.baseUrl}/${userId}/suggested-companies`;
    return this.http.get<CompanySuggestion[]>(url);
  }
}
