import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FormationService {

  private apiUrl = 'http://localhost:8080/api/getFormations';
  private userFormationsUrl = 'http://localhost:8080/api/getUserFormations';

  constructor(private http: HttpClient) {}

  // Service pour récupérer les formations d'un métier
  getFormations(metier: string, userId: number): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'userId': userId.toString()  // Envoyer l'ID utilisateur dans les en-têtes
    });

    const body = { metier };  // Envoyer le métier sous forme d'objet JSON

    return this.http.post<any>(this.apiUrl, body, { headers });
  }

  // Service pour récupérer les formations d'un utilisateur
getUserFormations(userId: number): Observable<any> {
  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'user-id': userId.toString()  // Notez le camelCase dans l'en-tête
  });

  return this.http.get<any>(`${this.userFormationsUrl}`, { headers });
}


}
