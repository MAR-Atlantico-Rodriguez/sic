import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {ProductService} from '../../services/product.service';

@Component({
  selector: 'app-products',
  //encapsulation: ViewEncapsulation.None,
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent implements OnInit {

  products: any[] = [];
  users: any[];
  errorMessage: string;

  constructor(private _productService: ProductService) {
  }

  ngOnInit() {
    this.products = this._productService.getProducts();
    //this.getUsers();
  }

  /*getUsers() {
    this._productService.getUsers()
      .subscribe(
        users => this.users = users,
        error => this.errorMessage = <any>error
      );
  }*/
}
