import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { CompanySuggestionComponent } from './company-suggestion/company-suggestion.component';
import { FormationComponentComponent } from './components/formation-component/formation-component.component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent},
    { path: 'register', component: RegisterComponent},
    { path: 'company-suggestion', component: CompanySuggestionComponent},
       { path: 'Formation', component: FormationComponentComponent},
];
