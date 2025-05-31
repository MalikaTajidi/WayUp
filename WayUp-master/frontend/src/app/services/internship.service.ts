import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { InternshipSuggestion } from '../models/internship-suggestion.model';

@Injectable({
  providedIn: 'root'
})
export class InternshipService {
  
  private baseUrl = 'http://localhost:8080/api/internships';
  private internshipUrl = `${this.baseUrl}/me`;

  constructor(private http: HttpClient) {}

  getUserInternships(): Observable<InternshipSuggestion[]> {
    const token = localStorage.getItem('authToken');
    
    if (!token) {
      console.error('Token manquant.');
      return throwError(() => new Error('Token manquant. Veuillez vous connecter.'));
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    return this.http.get<InternshipSuggestion[]>(this.internshipUrl, { headers })
      .pipe(
        catchError(error => {
          console.error('Erreur lors de la récupération des stages:', error);
          let errorMessage = 'Erreur de chargement des stages.';
          
          if (error.status === 401) {
            errorMessage = 'Session expirée. Veuillez vous reconnecter.';
          } else if (error.status === 400) {
            errorMessage = error.error || 'Préférences de poste non définies.';
          } else if (error.status === 404) {
            errorMessage = 'Utilisateur introuvable.';
          }
          
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  // Méthode de test pour l'API
  testInternshipApi(jobTitle: string): Observable<any> {
    const token = localStorage.getItem('authToken');
    
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    return this.http.get(`${this.baseUrl}/test-internship-api/${jobTitle}`, { headers });
  }
}