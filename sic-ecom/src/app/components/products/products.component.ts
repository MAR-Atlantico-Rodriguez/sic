import {Component, OnInit} from '@angular/core';
import {ProductService} from '../../services/product.service';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit {

  private products: any[];
  constructor(private productService: ProductService) {}

  ngOnInit() {
    this.productService.getProducts();
  }
}
