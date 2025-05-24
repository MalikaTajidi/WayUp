import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { InternshipSuggestion } from '../models/internship-suggestion.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InternshipService {

  private apiUrl = 'http://localhost:8080/api/internships'; // adapte si ton backend a une autre URL

  constructor(private http: HttpClient) {}

  getSuggestions(jobTitle: string): Observable<InternshipSuggestion[]> {
    return this.http.post<InternshipSuggestion[]>(this.apiUrl, { jobTitle });
  }
}
