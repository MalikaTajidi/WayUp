import { Component } from '@angular/core';
import { UserService } from '../services/UserService/user.service';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, SidebarComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  user: any;
  skills: any[] = [];
  userId: number = 1; // à ajuster dynamiquement selon l'utilisateur connecté
  metier: string | undefined;
  score: number = 0;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    console.log('useeeeeeeeeeeer', JSON.parse(localStorage.getItem('user') || '{}'));

    const user = JSON.parse(localStorage.getItem('user') || '{}');
    this.user = user;
    this.userId = user.id;
    this.metier = user.metierSugg;

    this.userService.getSkillsByUserId(this.userId).subscribe(
      skills => {
        this.skills = skills.map((skill: any) => ({
          id: skill.id,
          label: skill.name,
          validated: skill.acquired // Utilise acquired ou validated, mais cohérent partout
        }));
        console.log(this.skills);  // Vérification des données récupérées
        this.updateScore();  // Mettre à jour le score après récupération des compétences
      },
      error => {
        console.error('Erreur lors de la récupération des compétences :', error);
      }
    );
  }

  toggleSkill(skill: any): void {
    console.log('before', skill.validated);

    // Inverser l'état de la compétence
    skill.validated = !skill.validated;
    console.log('after', skill.validated);

    // Mettre à jour l'état de la compétence dans la base de données
    this.userService.updateSkill(skill.id, {
      acquired: skill.validated  // Mettre à jour la compétence avec la nouvelle valeur
    }).subscribe({
      next: () => {
        console.log(`Skill ${skill.label} updated.`);
        this.updateScore();  // Mettre à jour le score après modification
      },
      error: err => {
        console.error('Erreur lors de la mise à jour :', err);
      }
    });
  }

  updateScore(): void {
    // Calculer le score en pourcentage
    const validatedCount = this.skills.filter(skill => skill.validated).length;  // Assure-toi que 'validated' est utilisé
    this.score = (validatedCount / this.skills.length) * 100;
  }
}
