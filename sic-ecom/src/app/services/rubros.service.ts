import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt';
import 'rxjs/add/operator/map';

@Injectable()
export class RubrosService {

  public url = 'https://sic-api.herokuapp.com/api/v1/rubros/empresas/1';

  constructor(private authHttp: AuthHttp) {}

  getRubros() {
    return this.authHttp.get(this.url).map(data => data.json());
  }
}
