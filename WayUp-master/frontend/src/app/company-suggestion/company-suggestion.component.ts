import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { CompanySuggestionService } from '../services/company-suggestion/company-suggestion.service';
import { CompanySuggestion } from '../models/company-suggestion';

@Component({
  selector: 'app-company-suggestion',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './company-suggestion.component.html',
  styleUrl: './company-suggestion.component.css'
})
export class CompanySuggestionComponent implements OnInit 
 {
   private service = inject(CompanySuggestionService);

  companies: CompanySuggestion[] = [];
  userId!: number;

  ngOnInit() {
    const userData = localStorage.getItem('authUser');
   if (userData) {
      const parsedUser = JSON.parse(userData);
      this.userId = parsedUser.id;
      this.service.getSuggestions(this.userId).subscribe({
        next: (data) => (this.companies = data),
        error: (err) => console.error('Error fetching companies', err),
      });
    } else {
      console.error('No user data found in localStorage');
    }
    }
  }

 


