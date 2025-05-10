import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FormationService {

  private apiUrl = 'http://localhost:8080/api/getFormations';

  constructor(private http: HttpClient) {}

  getFormations(metier: string): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    // Envoyer le métier dans un objet JSON pour que le backend le comprenne correctement
    const body = JSON.stringify({ metier });

    return this.http.post<any>(this.apiUrl, body, { headers });
  }
}
