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


getUserFormations(userId: number): Observable<any> {
  const token = localStorage.getItem('authToken'); // Récupérer le token dans le localStorage
  console.log("id dans service", userId);
  console.log("Token:", token);

  if (!token) {
    console.error('Token manquant. Vous devez vous authentifier.');
    return new Observable(observer => observer.error('Token manquant. Veuillez vous connecter.'));
  }

  // Définir l'en-tête Authorization avec le token
  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`  // Ajouter le token d'authentification
  });

  // Construire l'URL avec l'ID utilisateur
  const url = `http://localhost:8080/api/userFormations/${userId}`;

  // Effectuer la requête GET avec l'URL et les en-têtes
  return this.http.get<any>(url, { headers });
}


}