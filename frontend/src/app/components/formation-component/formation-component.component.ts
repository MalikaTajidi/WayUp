import { Component } from '@angular/core';
import { FormationService } from '../../services/formation.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-formation-component',
  standalone: true,
  imports: [FormsModule,CommonModule],
  templateUrl: './formation-component.component.html',
  styleUrls: ['./formation-component.component.css']  // Correction ici
})
export class FormationComponentComponent {
  metier: string = '';
  formations: any[] = [];
  error: string = '';

  constructor(private formationService: FormationService) {}

  onSubmit() {
    if (this.metier) {
      this.formationService.getFormations(this.metier).subscribe({
        next: (response) => {
          this.formations = response;  // Mettre à jour avec la réponse obtenue
          this.error = '';
          console.log('Formations reçues:', response);
        },
        error: (err) => {
          this.error = 'Erreur lors de la récupération des formations';
          this.formations = [];
          console.error('Erreur API:', err);
        }
      });
    }
  }
}
