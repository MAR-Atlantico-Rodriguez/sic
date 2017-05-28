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

    public loadingProducts = false;

  constructor(private productService: ProductService) {}

  ngOnInit(){
    
  	this.getProductos();
  	this.productService.productosService.subscribe(
  		dataSearch => {
  			this.productos = dataSearch;            
  		},
  		error => {}
  	);
  }

  getProductos(){
    this.loadingProducts = true;
  	this.productService.getProductos()
      .subscribe(data => {      	
        this.productos = data;
        this.loadingProducts = false;
      });
  }
}
