import { Component } from '@angular/core';
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
export class InternshipListComponent {
  jobTitle: string = '';
  internships: any[] = [];
  loading: boolean = false;
  errorMessage: string = '';

  // Exemple de données simulées
  mockInternships = [
    {
      title: 'Développeur Web',
      company: 'TechCompany',
      location: 'Casablanca, Maroc',
      duration: '6 mois',
      description: 'Développement d\'applications web et mobile',
      requirements: 'JavaScript, Angular, Node.js',
    },
    {  
      title: 'Développeur Web',
      company: 'TechCompany',
      location: 'Casablanca, Maroc',
      duration: '6 mois',
      description: 'Développement d\'applications web et mobile',
      requirements: 'JavaScript, Angular, Node.js',
    },
    {
      title: 'Ingénieur DevOps',
      company: 'DevOpsCorp',
      location: 'Rabat, Maroc',
      duration: '3 mois',
      description: 'Mise en place et gestion des pipelines CI/CD',
      requirements: 'Docker, Kubernetes, Jenkins',
    },
    {
      title: 'Data Analyst',
      company: 'DataSolutions',
      location: 'Tanger, Maroc',
      duration: '4 mois',
      description: 'Analyse des données pour les rapports clients',
      requirements: 'Python, SQL, Tableau',
    },
  ];

  search() {
    this.loading = true;
    this.errorMessage = '';

    // Simuler la recherche avec les données fictives
    setTimeout(() => {
      if (this.jobTitle) {
        // Filtrer les stages en fonction du titre du job
        this.internships = this.mockInternships.filter((stage) =>
          stage.title.toLowerCase().includes(this.jobTitle.toLowerCase())
        );
        if (this.internships.length === 0) {
          this.errorMessage = 'Aucun stage trouvé.';
        }
      } else {
        this.errorMessage = 'Veuillez entrer un titre de poste.';
      }
      this.loading = false;
    }, 1000); // Simuler un délai de recherche
  }
}