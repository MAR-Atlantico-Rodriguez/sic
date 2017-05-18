import {Routes, RouterModule} from '@angular/router';
import {LoginComponent} from './login/login.component'
import {AuthGuard} from './_guards/auth.guard';
import {HomeComponent} from './home/home.component'

const appRoutes: Routes = [
	{path:'login', component: LoginComponent},
	{path:'', component:HomeComponent, canActivate:[AuthGuard]},
	{path:'**', redirectTo:''}
];

export const routing = RouterModule.forRoot(appRoutes)