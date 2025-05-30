import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule,RouterLink],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent {
 isMenuVisible: boolean = false;

 stat1: number = 1000;
  stat2: number = 1000;
  stat3: number = 1000;



  ngOnInit(): void {
    // Lancer l'animation pour chaque statistique
    this.animateStat('stat1', 1200, 500); // 1200 utilisateurs en 2 secondes
    this.animateStat('stat2', 98, 500);   // 98% en 2 secondes
    this.animateStat('stat3', 450, 500);  // 450 projets en 2 secondes

  }
    animateStat(stat: 'stat1' | 'stat2' | 'stat3', target: number, duration: number): void {
    let start = 0;
    let step = target / duration;
    let interval = setInterval(() => {
      start += step;

      if (start >= target) {
        this[stat ] = target; 
        clearInterval(interval);
      } else {
        this[stat] = Math.floor(start); // Met à jour la valeur de la statistique
      }
    }, 1);
  }

  activeMemberId: number | null = null;

  teamMembers = [
    { id: 0, name: 'Safae', role: 'Frontend Developer', image: 'assets/safae.png' },
    { id: 1, name: 'Ayoub', role: 'Backend Developer', image: 'assets/ayoub.png' },
    { id: 2, name: 'Nada', role: 'Product Owner', image: 'assets/nada.png' }
  ];

  toggleMenu() {
    this.isMenuVisible = !this.isMenuVisible;
  }

  selectMember(id: number) {
    this.activeMemberId = id;
  }
}
