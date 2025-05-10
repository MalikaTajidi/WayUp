import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule,
    ReactiveFormsModule,RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit {
  http: any;
  loginForm!: FormGroup;
  showPassword = false;

  constructor(private authService: AuthService, private router : Router) {
   
  }
  ngOnInit(): void {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required),
      rememberMe: new FormControl(false) 

    });
  }
  email: string = '';
  password: string = '';
  errorMessage: string = '';


  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const { email, password} = this.loginForm.value;
  
      this.authService.login(email, password).subscribe(
        (response: any) => {
          console.log('lets gooo', response);
  
          const token = response.token; 

            localStorage.setItem('authToken', token); 


          const user = { id: response.userId, email: response.email };
          
          localStorage.setItem('authUser', JSON.stringify(user));


            this.router.navigate(['/']);

        },
        (error: any) => {
          console.error('connexion error : ', error);
        }
      );
    } else {
      console.log('invalid Form');
    }
  }

}
