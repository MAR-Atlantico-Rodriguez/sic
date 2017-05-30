import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/map';

@Injectable()
export class ProductService {

  public productosService = new Subject<any>();
  public url = 'https://sic-api.herokuapp.com/api/v1/productos/busqueda/criteria?idEmpresa=1';
  public busquedaDescripcion: String = '';
  public busquedaRubro: String = '';
  public pagina:number = 0;
  public tamanio:number = 2;
  public totalPaginas:number = 0;
  public totalElementos:number = 0;

  constructor(public authHttp: AuthHttp) {}

  getProductos() {
    const url = this.url + this.getCriteria();
    return this.authHttp.get(url).map(data => data.json());
  }

  getBuscador(palabraBuscar: string) {
    this.busquedaDescripcion = palabraBuscar;
    this.getProductos().subscribe(
      data => {
        this.totalPaginas = data.totalPages;
        this.totalElementos = data.totalElements;
        this.productosService.next(data.content);
      }
    );
  }

  getRubro(rubroBuscar: string) {
    this.busquedaRubro = (rubroBuscar !== this.busquedaRubro) ? rubroBuscar : '';
    this.getProductos().subscribe(
      data => {
        this.totalPaginas = data.totalPages;
        this.totalElementos = data.totalElements;
        this.productosService.next(data.content);
      },
      error => {
        console.log(error);
      }
    );
  }

  getCriteria(): string {
    let criteria = '&pagina='+this.pagina+'&tamanio='+this.tamanio+'&';
    if (String(this.busquedaRubro).length > 0) {
      criteria += 'idRubro=' + this.busquedaRubro;
    }
    if (this.busquedaDescripcion.length > 0) {
      const ampersan = (String(criteria).length > 1) ? '&' : '';
      criteria += ampersan + 'descripcion=' + this.busquedaDescripcion;
    }
    return criteria;
  }
}
