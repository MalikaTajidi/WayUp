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
  const user = localStorage.getItem('user');
  const userId = user ? JSON.parse(user)?.id : null;
  const metier = user ? JSON.parse(user)?.metierSugg : null;

  console.log("User ID:", userId);
  console.log("Métier:", metier);

  if (metier && userId) {
    this.formationService.getFormations(metier, +userId).subscribe({
      next: (response) => {
        // Vérification du statut de la réponse
        if (response && response.status === 'success') {
          this.error = '';
          console.log('Réponse de l\'API :', response.message);
          // Traitement des formations de l'utilisateur
          this.formationService.getUserFormations(+userId).subscribe({
            next: (userFormations) => {
              this.formations = userFormations;
            
  //  
          },
            error: (err) => {
              this.error = 'Erreur lors de la récupération des formations';
              console.error('Erreur API:', err);
            }
          });
        } else {
          // Si le statut est 'error', afficher le message d'erreur
          this.error = response?.message || 'Erreur inconnue';
          console.error('Erreur API:', response?.message);
        }
      },
      error: (err) => {
    
      }
    });
  } else {
    this.error = 'Veuillez vous connecter pour récupérer les formations.';
  }
    // this.router.navigate(['/formation']);
    
}

}
