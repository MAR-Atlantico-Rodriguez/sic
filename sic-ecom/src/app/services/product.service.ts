import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {Response} from '@angular/http'; 

@Injectable()
export class ProductService {
  public productos = [];

  constructor(public authHttp: AuthHttp) {
    
  }

  getProductos(busqueda:string):Observable <any>{
    let b = (busqueda.length > 0)?'&descripcion='+busqueda:'';
    const url = 'https://sic-api.herokuapp.com/api/v1/productos/busqueda/criteria?idEmpresa=1'+b;
    let a = this.authHttp.get(url)
    .map((r:Response)=>r.json());
    return a;
  }

}
