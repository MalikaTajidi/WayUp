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
  const user = localStorage.getItem('user');
  const userId = user ? JSON.parse(user)?.id : null;

  console.log("holaa", userId);

  if (userId) {
    // Réinitialiser l'état de chargement avant chaque tentative
    this.isLoading = true;

    this.formationService.getUserFormations(+userId).subscribe({
      next: (response) => {
        // Vérifier si la réponse est null, vide ou un tableau vide
        if (!response || (Array.isArray(response) && response.length === 0)) {
          // Ne rien faire si l'utilisateur n'a pas de formations
          console.log('Aucune formation trouvée pour cet utilisateur.');
          this.isLoading = false;
          return;
        }
        // Sinon, traiter normalement la réponse
        this.formations = response;
        this.isLoading = false;
        console.log('Formations utilisateur:', response);
      },
      error: (err) => {
        // Ne pas considérer les erreurs 404 comme des erreurs graves
        if (err.status === 404) {
          console.log('Aucune formation trouvée pour cet utilisateur.');
          this.isLoading = false;
          return;
        }
        // Afficher uniquement les erreurs sérieuses dans la console
        console.error('Erreur lors de la récupération des formations:', err);
        this.isLoading = false;
      }
    });
  } else {
    this.isLoading = false;
    console.log('Veuillez vous connecter pour récupérer les formations.');
  }
}


  

onSubmit() {
    const user = localStorage.getItem('user');
  const userId = user ? JSON.parse(user)?.id : null;
     const metier = user ? JSON.parse(user)?.metierSugg : null;

console.log("metieer",userId)
console.log("metieer",metier)
    if (metier && userId) {
      this.formationService.getFormations(metier, +userId).subscribe({
        next: (response) => {
          console.log('Formations enregistrées:');
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
