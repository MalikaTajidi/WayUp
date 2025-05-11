import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class QuestionService {

 private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // Méthode pour récupérer toutes les questions
 getQuestions(): Observable<any[]> {
  const headers = {
    Authorization: 'Bearer ' + localStorage.getItem('authToken') 
  };
  return this.http.get<any[]>(`${this.apiUrl}/questions`, { headers });
}

submitTest(userId: number, testData: any): Observable<any> {
    const token = localStorage.getItem('authToken');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

return this.http.post(`${this.apiUrl}/resultat/${userId}`, testData, {
  headers: headers,
  responseType: 'text' as const
});
  }
}
