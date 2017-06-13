import {Component, OnInit} from '@angular/core';
import {ProductosService} from '../../services/productos.service';
import {AuthGuard} from '../../guards/auth.guard';
import {MdDialog} from '@angular/material';
import {DescripcionProductoComponent} from '../descripcion-producto/descripcion-producto.component';


@Component({
  selector: 'app-products',
  templateUrl: './productos.component.html',
  styleUrls: ['./productos.component.scss']
})
export class ProductosComponent implements OnInit {

  public productos = [];
  public loadingProducts = true;
  public totalPaginas: Number = 0;
  public totalElementos: Number = 0;
  public pagina = 0;
  public tamanioPagina = 10;

  constructor(private productService: ProductosService,
              private authGuard: AuthGuard,
              public dialog: MdDialog) {}

  ngOnInit() {
    this.getProductos();
    this.productService.productosService.subscribe(
      dataSearch => {
        this.productos = dataSearch;
        this.totalPaginas = this.productService.totalPaginas;
        this.pagina = this.productService.pagina;
      });

    this.productService.loadingProducts.subscribe(
      ( data: boolean ) => { this.loadingProducts = data; }
    );
  }

  getProductos() {
    this.authGuard.canActivate();
    this.productService.pagina = this.pagina;
    this.productService.tamanioPagina = this.tamanioPagina;
    this.productService.getProductos()
      .subscribe(data => {
          data.content.forEach((v) => { this.productos.push(v); });
          this.totalPaginas = data.totalPages;
          this.totalElementos = data.totalElements;
        });
  }

  masProductos() {
    if ((this.pagina + 1) < this.totalPaginas) {
      this.pagina++;
      this.getProductos();
    }
  }

  openDialog(p) {
    console.log(p);
    this.dialog.open(DescripcionProductoComponent);
  }
}
