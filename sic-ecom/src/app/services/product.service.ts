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
        /*Conforma la url para la busqueda*/
        let url = this.url + this.getCriteria(); 
        console.log(url);
        return this.authHttp.get(url).map(data => data.json());
    }

    //Buscador NavBar
    getBuscador(palabraBuscar:string){
        this.busquedaDescripcion = palabraBuscar;
        this.getProductos().subscribe(
            data => {
                this.productosService.next(data);
            }
        );
    }

    //Rubros SideBar y Menu
    getRubro(rubroBuscar:string){
        this.busquedaRubro = (rubroBuscar != this.busquedaRubro)?rubroBuscar:'';
        this.getProductos().subscribe(
            data => {
                this.productosService.next(data);
            }
        );
    }

    getCriteria():string{
        let criteria = '&';            
        if(String(this.busquedaRubro).length > 0){
            criteria += 'idRubro='+this.busquedaRubro;
        }
        if(this.busquedaDescripcion.length > 0){            
            let ampersan = (String(criteria).length > 1)?'&':'';
            console.log(ampersan);
            criteria += ampersan+'descripcion='+this.busquedaDescripcion;
        }        
        return criteria;        
    }
}
