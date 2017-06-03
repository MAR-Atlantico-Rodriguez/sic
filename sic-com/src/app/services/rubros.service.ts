import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt';
import 'rxjs/add/operator/map';
import {AuthGuard} from '../guards/auth.guard';
import {environment} from '../../environments/environment';

@Injectable()
export class RubrosService {

  public url = environment.apiUrl + '/api/v1/rubros/empresas/1';

  constructor(private authHttp: AuthHttp, private authGuard: AuthGuard) {}

  getRubros() {
    this.authGuard.canActivate();
    return this.authHttp.get(this.url).map(data => data.json());
  }
}
