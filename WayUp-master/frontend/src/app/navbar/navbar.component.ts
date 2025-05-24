import { Component } from '@angular/core';
import { NgFor } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [NgFor], 
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  navLinks = [
    { title: 'Accueil', url: '#' },
    { title: 'Métiers', url: '#' },
    { title: 'Stages', url: '#' },
    { title: 'Entreprises', url: '#' },
    { title: 'À propos', url: '#' }
  ];

}
