import {Component, OnInit} from '@angular/core';
import {ProductService} from '../../services/product.service';
import {AuthGuard} from '../../guards/auth.guard';

@Component({
  selector: 'app-products',
  templateUrl: './productos.component.html',
  styleUrls: ['./productos.component.scss']
})

export class ProductsComponent implements OnInit {

  public productos = [];
  public loadingProducts = false;
  public totalPaginas:number = 0;
  public totalElementos:number = 0;
  public pagina:number = 0;
  public tamanio:number = 2;

  constructor(private productService: ProductService, private authGuard: AuthGuard) {}

  ngOnInit() {
    this.getProductos();
    this.productService.productosService.subscribe(
      dataSearch => {
        this.productos = dataSearch;
        this.totalPaginas = this.productService.totalPaginas;
        this.pagina = this.productService.pagina;
      });
  }

  /*getProductos() {
    this.authGuard.canActivate();
    this.loadingProducts = true;
    this.productService.getProductos()
      .subscribe(data => {
          this.productos = data;
          this.loadingProducts = false;
        });
  }*/

  getProductos() {
    this.authGuard.canActivate();
    this.loadingProducts = true;
    this.productService.pagina = this.pagina;
    this.productService.tamanio = this.tamanio;
    this.productService.getProductos()
      .subscribe(data => {
          data.content.forEach((v) => { this.productos.push(v); });

          this.totalPaginas = data.totalPages;
          this.totalElementos = data.totalElements;
          this.loadingProducts = false;
        });
  }

  masProductos(){
    if((this.pagina+1) < this.totalPaginas){
      this.pagina++;
      this.getProductos();
    }
  }
}
