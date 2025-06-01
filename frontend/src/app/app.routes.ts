import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { CompanySuggestionComponent } from './company-suggestion/company-suggestion.component';
import { FormationComponentComponent } from './components/formation-component/formation-component.component';
import { FormTestComponent } from './form-test/form-test.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TestComponent } from './test/test.component';
import { LandingComponent } from './landing/landing.component';
import { InternshipListComponent } from './internship-list/internship-list.component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent},
    { path: 'register', component: RegisterComponent},
    { path: 'faire-test', component: FormTestComponent},
    { path: 'dashboard', component: DashboardComponent},
        { path: 'test', component: TestComponent},
        { path: '', component: LandingComponent},
    { path: 'company-suggestion', component: CompanySuggestionComponent },
    { path: 'formation', component: FormationComponentComponent },
    {path:'internship',component:InternshipListComponent}
];
