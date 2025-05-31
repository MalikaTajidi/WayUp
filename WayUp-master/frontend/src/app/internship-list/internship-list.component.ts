// internship-list.component.ts
import { Component, OnInit } from '@angular/core';
import { InternshipService } from '../services/internship.service';
import { InternshipSuggestion } from '../models/internship-suggestion.model';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-internship-list',
  imports: [CommonModule, FormsModule, HttpClientModule],
  standalone: true,
  templateUrl: './internship-list.component.html'
})
export class InternshipListComponent implements OnInit {
  internships: InternshipSuggestion[] = [];

  constructor(private internshipService: InternshipService) {}

  ngOnInit() {
    this.loadInternships();
  }

  loadInternships() {
    this.internshipService.getSuggestions('Développeur Web').subscribe(
      data => this.internships = data,
      error => console.error('Erreur lors du chargement des stages', error)
    );
  }
}