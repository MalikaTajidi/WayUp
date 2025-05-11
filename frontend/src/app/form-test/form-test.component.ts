import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-form-test',
  standalone: true,
  imports: [],
  templateUrl: './form-test.component.html',
  styleUrl: './form-test.component.css'
})
export class FormTestComponent {
constructor(private router: Router) {}

  commencerTest(): void {
    console.log('Test démarré');
    // Redirection vers une autre route, par exemple "/questions"
    this.router.navigate(['/test']);
  }
}
