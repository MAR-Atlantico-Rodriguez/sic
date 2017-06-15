import {Routes, RouterModule} from '@angular/router';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {HomeComponent} from './components/home/home.component';
import {CarroComprasComponent} from './components/carro-compras/carro-compras.component';

const appRoutes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: '', component: HomeComponent, canActivate: [AuthGuard]},
  {path: 'carro', component: CarroComprasComponent, canActivate: [AuthGuard]},
  {path: '**', redirectTo: ''}
];

export const routing = RouterModule.forRoot(appRoutes);
