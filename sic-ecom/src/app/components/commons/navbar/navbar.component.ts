import { Component } from '@angular/core';
import {ProductService} from '../../../services/product.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
	
  	constructor(public productService:ProductService) {}

  	buscadorProductos(palabraBuscar: string){
  		this.productService.getBuscador(palabraBuscar);
  	}
}
