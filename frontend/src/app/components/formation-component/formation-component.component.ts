import { Component, OnInit } from '@angular/core';
import { FormationService } from '../../services/formation.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-formation-component',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './formation-component.component.html',
  styleUrls: ['./formation-component.component.css']
})
export class FormationComponentComponent implements OnInit {
  formations: any[] = [];
  error: string = '';
  isLoading: boolean = true;

  constructor(private formationService: FormationService, private router: Router) {}

  ngOnInit(): void {
    const userId = localStorage.getItem('userId');
//const userId='2';
    console.log("holaa",userId)
    if (userId) {
      this.formationService.getUserFormations(+userId).subscribe({
        next: (userFormations) => {
          this.formations = userFormations;
          this.isLoading = false;
          console.log('Formations utilisateur:', userFormations);
        },
        error: (err) => {
          this.error = 'Erreur lors de la récupération des formations';
          this.isLoading = false;
          console.error('Erreur API:', err);
        }
      });
    } else {
      this.isLoading = false;
      this.error = 'Veuillez vous connecter pour récupérer les formations.';
    }
  }

  onSubmit() {
    const metier = localStorage.getItem('metier');
    const userId = localStorage.getItem('userId');

    if (metier && userId) {
      this.formationService.getFormations(metier, +userId).subscribe({
        next: (response) => {
          console.log('Formations enregistrées:', response);
          this.formationService.getUserFormations(+userId).subscribe({
            next: (userFormations) => {
              this.formations = userFormations;
              this.error = '';
              console.log('Formations utilisateur:', userFormations);
            },
            error: (err) => {
              this.error = 'Erreur lors de la récupération des formations';
              console.error('Erreur API:', err);
            }
          });
        },
        error: (err) => {
          this.error = 'Erreur lors de l\'enregistrement des formations';
          console.error('Erreur API:', err);
        }
      });
    } else {
      this.error = 'Veuillez vous connecter pour récupérer les formations.';
    }
  }
}
