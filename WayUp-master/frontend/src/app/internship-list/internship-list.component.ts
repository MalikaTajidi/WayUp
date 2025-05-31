import { Component, OnInit } from '@angular/core';
import { InternshipService } from '../services/internship.service';
import { InternshipSuggestion } from '../models/internship-suggestion.model';

import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from "../sidebar/sidebar.component";
 

@Component({
  selector: 'app-internship-list',
  templateUrl: './internship-list.component.html',
   standalone: true,
   imports: [CommonModule, FormsModule,SidebarComponent],
  styleUrls: ['./internship-list.component.css']
})
export class InternshipListComponent implements OnInit {
  internships: InternshipSuggestion [] = [];
  filteredInternships: InternshipSuggestion [] = [];
  
  filterTitle: string = '';
  filterLocation: string = '';
  isLoading: boolean = false;
  error: string = '';

  constructor(private internshipService: InternshipService) {}

  ngOnInit(): void {
    this.fetchInternships();
  }

  fetchInternships(): void {
    this.isLoading = true;
    this.error = '';
    
    this.internshipService.getUserInternships().subscribe({
      next: (data: InternshipSuggestion []) => {
        console.log('Données reçues:', data);
        this.internships = data;
        this.filteredInternships = [...data]; // Créer une copie
        this.isLoading = false;
        
        if (data.length === 0) {
          this.error = 'Aucune suggestion de stage trouvée pour votre profil.';
        }
      },
      error: (err: Error) => {
        console.error('Erreur lors du chargement:', err);
        this.error = err.message || 'Erreur de chargement des stages.';
        this.isLoading = false;
        this.internships = [];
        this.filteredInternships = [];
      }
    });
  }

  filterInternships(): void {
    this.filteredInternships = this.internships.filter(internship =>
      internship.title.toLowerCase().includes(this.filterTitle.toLowerCase()) &&
      internship.location.toLowerCase().includes(this.filterLocation.toLowerCase())
    );
  }

  clearFilters(): void {
    this.filterTitle = '';
    this.filterLocation = '';
    this.filteredInternships = [...this.internships];
  }

  // Méthode pour recharger les données
  refreshInternships(): void {
    this.fetchInternships();
  }

  // Méthode pour le trackBy
  trackByIndex(index: number, item: InternshipSuggestion ): any {
    return item.id || index;
  }
}