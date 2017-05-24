import {Component, OnInit} from '@angular/core';
import {ProductService} from '../../services/product.service';
import {Observable} from 'rxjs/Observable';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent{

  public productos: Array<any>;

  constructor(private productService: ProductService) {}

  ngOnInit(){
  	this.getProductos();
  	this.productService.productosService.subscribe(
  		dataSearch => {
  			//console.log(dataSearch.length);
  			this.productos = dataSearch;
  		},
  		error => {}
  	);
  }

  getProductos(){
  	this.productService.getProductos()
      .subscribe(data => {      	
        this.productos = data;
      });
  }
}
