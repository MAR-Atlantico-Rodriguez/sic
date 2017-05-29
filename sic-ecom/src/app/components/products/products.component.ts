import {Component, OnInit} from '@angular/core';
import {ProductService} from '../../services/product.service';
import {AuthGuard} from '../../guards/auth.guard';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})

export class ProductsComponent implements OnInit {

  public productos: Array<any>;
  public loadingProducts = false;

  constructor(private productService: ProductService, private authGuard: AuthGuard) {}

  ngOnInit() {
    this.getProductos();
    this.productService.productosService.subscribe(
      dataSearch => {
        this.productos = dataSearch;
      });
  }

  getProductos() {
    this.authGuard.canActivate();
    this.loadingProducts = true;
    this.productService.getProductos()
      .subscribe(data => {
          this.productos = data;
          this.loadingProducts = false;
        });
  }
}
