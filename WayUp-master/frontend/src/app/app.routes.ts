import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { CompanySuggestionComponent } from './company-suggestion/company-suggestion.component';
import { NavbarComponent } from './navbar/navbar.component';
import { InternshipListComponent } from './internship-list/internship-list.component';
import { SidebarComponent } from './sidebar/sidebar.component';
export const routes: Routes = [
    { path: 'login', component: LoginComponent},
    { path: 'register', component: RegisterComponent},
    { path: 'company-suggestion', component: CompanySuggestionComponent},
    {path:'navbar',component:NavbarComponent},
    {path:'inter',component:InternshipListComponent},
       { path: 'sidbar', component:SidebarComponent},
];
