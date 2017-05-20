import {Component, OnInit} from '@angular/core';
import {ProductService} from '../../services/product.service';
import {Observable} from 'rxjs/Observable';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent{

  productos = [];
  constructor(private productService: ProductService) {
  	this.getData();
  }

  getData(){
  	this.productService.getProductos('')
  	.subscribe(data => this.productos = data);
  	console.log(this.productos);
  }
}
