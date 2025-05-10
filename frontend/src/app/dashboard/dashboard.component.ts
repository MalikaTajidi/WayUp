import { Component } from '@angular/core';
import { UserService } from '../services/UserService/user.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
user: any;
  skills: any[] = [];
  userId: number = 1; // à ajuster dynamiquement selon l'utilisateur connecté
  metier:string | undefined;
  constructor(private userService: UserService) {}

  ngOnInit(): void {
    console.log('useeeeeeeeeeeer',JSON.parse(localStorage.getItem('user') || '{}'));

    const user = JSON.parse(localStorage.getItem('user') || '{}');

      this.user = user;
    this.userId = user.id;
    this.userService.getSkillsByUserId(this.userId).subscribe(
      skills => {
        // Extraire uniquement les informations pertinentes (name, acquired)
        this.skills = skills.map((skill: any) => ({
          id: skill.id,
          label: skill.name,
          validated: skill.acquired
        }));
        console.log(this.skills);  // Pour vérifier ce qui est récupéré
      },
      error => {
        console.error('Erreur lors de la récupération des compétences :', error);
      }
    );
  }

toggleSkill(skill: any): void {
       console.log('before',skill.validated);

  skill.validated = !skill.validated;
     console.log('hhhhh',skill.validated);
 // ou validated = !validated si tu utilises ce nom
  this.userService.updateSkill(skill.id, {
    acquired: skill.validated
    
  }).subscribe({
    next: () => console.log(`Skill ${skill.label} updated.`),
    error: err => console.error('Erreur lors de la mise à jour :', err)
  });
}

}
