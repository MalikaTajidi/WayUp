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
export class CompanySuggestionComponent //implements OnInit {
 {
   companies: CompanySuggestion[] = [];

  ngOnInit() {
    // Dummy data for testing
    this.companies = [
      {
        id: 1,
        name: 'TechNova',
        description: 'Innovative solutions in cloud computing.',
        industry: 'Information Technology',
        location: 'San Francisco, CA',
        size: '201-500',
        foundedYear: 2015
      },
      {
        id: 2,
        name: 'GreenFields',
        description: 'Sustainable agriculture and food production.',
        industry: 'Agriculture',
        location: 'Austin, TX',
        size: '51-200',
        foundedYear: 2010
      }
    ];
  }

 }


