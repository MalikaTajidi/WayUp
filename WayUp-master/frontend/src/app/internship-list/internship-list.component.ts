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
  allInternships: any[] = [];
  filteredInternships: any[] = [];
  loading: boolean = false;
  errorMessage: string = '';
  filterTitle: string = '';
  filterLocation: string = '';

  // Données des stages (élargie)
  mockInternships = [
    {
      title: 'Développeur Web Frontend',
      company: 'TechCompany',
      location: 'Casablanca, Maroc',
      duration: '6 mois',
      description: 'Développement d\'interfaces utilisateur modernes avec React et Angular. Collaboration avec l\'équipe UX/UI pour créer des expériences utilisateur exceptionnelles.',
      requirements: 'JavaScript, React, Angular, CSS, HTML',
    },
    {
      title: 'Développeur Web Backend',
      company: 'WebSolutions',
      location: 'Rabat, Maroc',
      duration: '4 mois',
      description: 'Développement d\'APIs REST et intégration de bases de données. Travail sur l\'architecture backend et l\'optimisation des performances.',
      requirements: 'Node.js, Express, MongoDB, PostgreSQL',
    },
    {
      title: 'Ingénieur DevOps',
      company: 'DevOpsCorp',
      location: 'Rabat, Maroc',
      duration: '3 mois',
      description: 'Mise en place et gestion des pipelines CI/CD. Automatisation des déploiements et monitoring des infrastructures cloud.',
      requirements: 'Docker, Kubernetes, Jenkins, AWS',
    },
    {
      title: 'Data Analyst',
      company: 'DataSolutions',
      location: 'Tanger, Maroc',
      duration: '4 mois',
      description: 'Analyse des données pour les rapports clients. Création de tableaux de bord interactifs et extraction d\'insights métier.',
      requirements: 'Python, SQL, Tableau, Power BI',
    },
    {
      title: 'Designer UX/UI',
      company: 'DesignStudio',
      location: 'Casablanca, Maroc',
      duration: '3 mois',
      description: 'Conception d\'interfaces utilisateur intuitives. Recherche utilisateur, wireframing et prototypage pour applications web et mobile.',
      requirements: 'Figma, Adobe XD, Sketch, Photoshop',
    },
    {
      title: 'Data Scientist',
      company: 'AI Innovations',
      location: 'Marrakech, Maroc',
      duration: '6 mois',
      description: 'Développement de modèles de machine learning. Analyse prédictive et traitement de données massives pour des projets d\'IA.',
      requirements: 'Python, R, TensorFlow, PyTorch, Pandas',
    },
    {
      title: 'Développeur Mobile',
      company: 'MobileFirst',
      location: 'Casablanca, Maroc',
      duration: '5 mois',
      description: 'Développement d\'applications mobiles natives et cross-platform. Intégration d\'APIs et optimisation des performances mobile.',
      requirements: 'React Native, Flutter, Swift, Kotlin',
    },
    {
      title: 'Cybersécurité',
      company: 'SecureTech',
      location: 'Rabat, Maroc',
      duration: '4 mois',
      description: 'Audit de sécurité et tests de pénétration. Mise en place de mesures de sécurité et formation des équipes.',
      requirements: 'Kali Linux, Wireshark, Metasploit, CISSP',
    }
  ];

  ngOnInit() {
    this.loadInternships();
  }

  loadInternships() {
    this.loading = true;
    this.errorMessage = '';
    
    // Simuler le chargement des données
    setTimeout(() => {
      try {
        this.allInternships = [...this.mockInternships];
        this.filteredInternships = [...this.allInternships];
        this.loading = false;
      } catch (error) {
        this.errorMessage = 'Erreur lors du chargement des stages.';
        this.loading = false;
      }
    }, 800);
  }

  filterInternships() {
    this.filteredInternships = this.allInternships.filter(internship => {
      const matchesTitle = !this.filterTitle || 
        internship.title.toLowerCase().includes(this.filterTitle.toLowerCase()) ||
        internship.requirements.toLowerCase().includes(this.filterTitle.toLowerCase());
      
      const matchesLocation = !this.filterLocation || 
        internship.location.toLowerCase().includes(this.filterLocation.toLowerCase());
      
      return matchesTitle && matchesLocation;
    });
  }

  clearFilters() {
    this.filterTitle = '';
    this.filterLocation = '';
    this.filteredInternships = [...this.allInternships];
  }
}