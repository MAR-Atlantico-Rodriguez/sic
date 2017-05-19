import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt';

@Injectable()
export class ProductService {

  constructor(public authHttp: AuthHttp) {
    console.log(authHttp);
  }

  getProducts() {
    const url = 'https://sic-api.herokuapp.com/api/v1/productos/busqueda/criteria?idEmpresa=1&rubro=ferreteria&descripcion=termica';
    this.authHttp.get(url)
      .subscribe(
        data => console.log(data),
        err => console.log(err)
      );
  }
}
