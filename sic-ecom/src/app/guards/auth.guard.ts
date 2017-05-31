import {Injectable} from '@angular/core';
import {Router, CanActivate} from '@angular/router';
import {AuthService} from '../services/auth.service';

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(public auth: AuthService, private router: Router) {}

  canActivate() {
    if (this.auth.loggedIn()) {
      return true;
    } else {
      this.router.navigate(['login'])
        .then(() => { return false; });
    }
  }
}
