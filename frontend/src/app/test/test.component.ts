import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { QuestionService } from '../services/QuestionService/question.service';

@Component({
  selector: 'app-test',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './test.component.html',
  styleUrl: './test.component.css'
})
export class TestComponent implements OnInit {
  
    constructor(private questionService: QuestionService,private router:Router) {}
  questions: any[] = [];
  userId: number = 0; 


 ngOnInit() {
   const user = JSON.parse(localStorage.getItem('user') || '{}');
    this.userId = user.id;
    this.questionService.getQuestions().subscribe((data) => {
      this.questions = data;
      console.log('qsts',this.questions)
    });
  }
  

  answers: string[] = new Array(this.questions.length).fill('');


  submitTest() {
    const payload = {
      questions: this.questions,
      answers: this.answers
    };
     this.questionService.submitTest(this.userId, payload).subscribe({
      next: (response) => {
        console.log('Réponse du serveur :', response);
          this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Erreur lors de l’envoi du test :', error);
      }
    });
  }
 }
