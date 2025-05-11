import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { CompanySuggestionComponent } from './company-suggestion/company-suggestion.component';
import { FormationComponentComponent } from './components/formation-component/formation-component.component';
import { FormTestComponent } from './form-test/form-test.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TestComponent } from './test/test.component';

export const routes: Routes = [ 
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'company-suggestion', component: CompanySuggestionComponent },
    { path: 'formation', component: FormationComponentComponent },
    { path: 'faire-test', component: FormTestComponent },
    { path: 'dashboard', component: DashboardComponent },
    { path: 'test', component: TestComponent },
];
