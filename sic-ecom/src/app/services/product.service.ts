import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/map'; 

@Injectable()
export class ProductService {
  
    public productosService = new Subject<any>();
    public url = 'https://sic-api.herokuapp.com/api/v1/productos/busqueda/criteria?idEmpresa=1';

    /*Variables para conformar busquedas*/
        public busquedaDescripcion: String = '';
        public busquedaRubro: String = '';
    /*//////////////////////////////////*/
    
    constructor(public authHttp: AuthHttp) {}

    getProductos(){
        let url = this.url + this.getCriteria(); /*Conforma la url para la busqueda*/
        return this.authHttp.get(url).map(data => data.json());
    }

    //Buscador NavBar
    getBuscador(palabraBuscar:string){
        this.busquedaDescripcion = palabraBuscar;
        this.getProductos().subscribe(
            data => {
                this.productosService.next();
                this.productosService.next(data);
            }
        );
    }

    //Rubros SideBar y Menu
    getRubro(rubroBuscar:string){       
        this.getProductos().subscribe(
            data => {
                this.productosService.next(data);
            }
        );
    }

    getCriteria():string{
        let criteria = '&';
        if(this.busquedaRubro.length > 0){
            criteria += 'rubro='+this.busquedaRubro;
        }
        if(this.busquedaDescripcion.length > 0){            
            //let ampersan = (criteria.length > 1)?'&':'';
            //console.log(ampersan);
            criteria += 'descripcion='+this.busquedaDescripcion;
        }
        return criteria;        
    }
}
