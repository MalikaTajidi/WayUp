import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
<<<<<<< HEAD
import { CompanySuggestionComponent } from './company-suggestion/company-suggestion.component';
import { FormationComponentComponent } from './components/formation-component/formation-component.component';
=======
import { FormTestComponent } from './form-test/form-test.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { TestComponent } from './test/test.component';
>>>>>>> origin/safae

export const routes: Routes = [
    { path: 'login', component: LoginComponent},
    { path: 'register', component: RegisterComponent},
<<<<<<< HEAD
    { path: 'company-suggestion', component: CompanySuggestionComponent},
       { path: 'Formation', component: FormationComponentComponent},
=======
    { path: 'faire-test', component: FormTestComponent},
    { path: 'dashboard', component: DashboardComponent},
        { path: 'test', component: TestComponent},

>>>>>>> origin/safae
];
