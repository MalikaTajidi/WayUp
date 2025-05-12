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
      'Content-Type': 'application/json'
    });

    const body = metier;  // Envoyer le métier directement comme chaîne de caractères

    // Construire l'URL avec l'ID utilisateur
    const url = `${this.apiUrl}/${userId}`;

    // Envoyer la requête POST avec le métier en tant que corps
    return this.http.post<any>(url, body, { headers });
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
