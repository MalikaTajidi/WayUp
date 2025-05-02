import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {

   }
   login(email: string, password: string): Observable<any> {
    const loginData = { email, password }; 
    console.log(loginData);
    return this.http.post(`${this.apiUrl}/login`, loginData, { headers: { 'Content-Type': 'application/json' },  responseType: 'json' });
  }

  registerUser(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userData);
  }
  getToken() {
    return localStorage.getItem('authToken');
  }
}
