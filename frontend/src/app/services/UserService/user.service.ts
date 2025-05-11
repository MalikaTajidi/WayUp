import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

 private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getSkillsByUserId(userId: number): Observable<any[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    return this.http.get<any[]>(`${this.baseUrl}/skills/${userId}`, { headers });
  }

updateSkill(skillId: number, updated: any): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    return this.http.put(`${this.baseUrl}/skills/${skillId}`, updated, { headers });
  }}
