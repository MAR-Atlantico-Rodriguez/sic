import {Injectable} from '@angular/core';
import {Http, Headers, RequestOptions, Response} from '@angular/http';
import {AuthenticationService} from './authentication.service';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/do';

@Injectable()
export class ProductService {

  constructor(private http: Http, private authenticationService: AuthenticationService) {
  }

  private productsJson: any[] = [
    {
      "codigo": "001",
      "descripcion": "Tornillo bolsita",
      "cantidad": "1700",
      "precio": "23",
      "medida": 15,
      "imageUrl": "assets/placeholderProducto.png"
    },
    {
      "codigo": "002",
      "descripcion": "Cemento bolsa",
      "cantidad": "500",
      "precio": "15",
      "medida": 15,
      "imageUrl": "assets/placeholderProducto.png"
    },
    {
      "codigo": "003",
      "descripcion": "pintura tacho 5L",
      "cantidad": "70",
      "precio": "634",
      "medida": 15,
      "imageUrl": "assets/placeholderProducto.png"
    },
    {
      "codigo": "005",
      "descripcion": "pinza amperometrica hasta 20 A.",
      "cantidad": "160",
      "precio": "2000",
      "medida": 15,
      "imageUrl": "assets/placeholderProducto.png"
    },
    {
      "codigo": "006",
      "descripcion": "taladro told",
      "cantidad": "10",
      "precio": "1023",
      "medida": 15,
      "imageUrl": "assets/placeholderProducto.png"
    },
    {
      "codigo": "009",
      "descripcion": "videt para el baño",
      "cantidad": "47",
      "precio": "3549",
      "medida": 15,
      "imageUrl": "assets/placeholderProducto.png"
    }
  ]

  getProducts() {
    let url = 'https://sic-api.herokuapp.com/api/v1/productos/busqueda/criteria?idEmpresa=1&rubro=ferreteria&descripcion=termica';
    // add authorization header with jwt token
    const headers = new Headers();
    headers.append('Authorization', 'Basic '+this.authenticationService.getToken());
    headers.append('Access-Control-Allow-Origin', '*');
    headers.append('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept');

    const options = new RequestOptions({ headers });

    // get users from api
    return this.http.get(url, options)
        .map((response: Response) => response.json());
  }

  handleError(error: Response) {
    console.error(error);
    const message = `Error status code ${error.status} at ${error.url}`;
    return Observable.throw(message);
  }

  /*getProducts() {
    return this.productsJson;
  }*/
}
