import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {ProductService} from '../../services/product.service';



@Component({
  selector: 'app-products',
  //encapsulation: ViewEncapsulation.None,
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit {

  products: any[];
  
  errorMessage: string;

  constructor(private _productService: ProductService) {
  }

  ngOnInit() {    
    this.getProducts();
  }

  getProducts() {
    this._productService.getProducts().subscribe(
        products => this.products = products,
        error => this.errorMessage = <any>error
      );
  }

  
}
