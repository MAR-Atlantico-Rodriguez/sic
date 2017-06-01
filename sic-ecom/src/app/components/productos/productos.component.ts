import {Component, OnInit} from '@angular/core';
import {ProductosService} from '../../services/productos.service';
import {AuthGuard} from '../../guards/auth.guard';

@Component({
  selector: 'app-products',
  templateUrl: './productos.component.html',
  styleUrls: ['./productos.component.scss']
})
export class ProductosComponent implements OnInit {

  public productos = [];
  public loadingProducts = false;
  public totalPaginas: Number = 0;
  public totalElementos: Number = 0;
  public pagina = 0;
  public tamanioPagina = 10;

  constructor(private productService: ProductosService, private authGuard: AuthGuard) {}

  ngOnInit() {
    this.getProductos();
    this.productService.productosService.subscribe(
      dataSearch => {
        this.productos = dataSearch;
        this.totalPaginas = this.productService.totalPaginas;
        this.pagina = this.productService.pagina;
      });
  }

  getProductos() {
    this.authGuard.canActivate();
    this.loadingProducts = true;
    this.productService.pagina = this.pagina;
    this.productService.tamanioPagina = this.tamanioPagina;
    this.productService.getProductos()
      .subscribe(data => {
          data.content.forEach((v) => { this.productos.push(v); });
          this.totalPaginas = data.totalPages;
          this.totalElementos = data.totalElements;
          this.loadingProducts = false;
        });
  }

  masProductos() {
    if ((this.pagina + 1) < this.totalPaginas) {
      this.pagina++;
      this.getProductos();
    }
  }
}
