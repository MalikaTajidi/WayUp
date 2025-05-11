import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { FormTestComponent } from './form-test/form-test.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TestComponent } from './test/test.component';
import { CompanySuggestionComponent } from './company-suggestion/company-suggestion.component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent},
    { path: 'register', component: RegisterComponent},
    { path: 'faire-test', component: FormTestComponent},
    { path: 'dashboard', component: DashboardComponent},
        { path: 'test', component: TestComponent},

    { path: 'company-suggestion', component: CompanySuggestionComponent},
];
