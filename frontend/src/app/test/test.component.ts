import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { QuestionService } from '../services/QuestionService/question.service';
import { LocalizedString } from '@angular/compiler';
import { UserService } from '../services/UserService/user.service';

@Component({
  selector: 'app-test',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.css']
})
export class TestComponent implements OnInit {

  constructor(private questionService: QuestionService, private userService: UserService, private router: Router) {}

  questions: any[] = [];
  userId: number = 0;

  // Pagination variables
  currentPage: number = 0;
  pageSize: number = 3;
  answers: string[] = [];

  ngOnInit() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    this.userId = user.id;

    this.questionService.getQuestions().subscribe((data) => {
      this.questions = data;
      this.answers = new Array(this.questions.length).fill(''); // Initialiser les réponses vides
      console.log('Questions:', this.questions);
    });
  }

  get currentQuestions(): any[] {
    const start = this.currentPage * this.pageSize;
    return this.questions.slice(start, start + this.pageSize);
  }

  nextPage() {
    if ((this.currentPage + 1) * this.pageSize < this.questions.length) {
      this.currentPage++;
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
    }
  }

  isLastPage(): boolean {
    return (this.currentPage + 1) * this.pageSize >= this.questions.length;
  }

  submitTest() {
    const payload = {
      questions: this.questions,
      answers: this.answers
    };

    this.questionService.submitTest(this.userId, payload).subscribe({
      next: () => {
        // 🔄 Recharger l'utilisateur mis à jour depuis le backend
        this.userService.getUserById(this.userId).subscribe(updatedUser => {
          localStorage.setItem('user', JSON.stringify(updatedUser));
          this.router.navigate(['/dashboard']);
        });
      },
      error: (error) => {
        console.error('Erreur lors de l’envoi du test :', error);
      }
    });
  }
}
