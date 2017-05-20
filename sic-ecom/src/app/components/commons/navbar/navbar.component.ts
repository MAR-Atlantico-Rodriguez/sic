import { Component } from '@angular/core';
import {ProductService} from '../../../services/product.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
	private productos: any[];
  	constructor(private productService:ProductService) {}

  	buscadorProductos(search: string){
  		console.log(search);
  		this.productService.getProductos(search)
  		.subscribe(data => this.productos = data);

  		console.log(this.productos);
  	}
}
