import {Component, OnInit} from '@angular/core';
import {ProductosService} from '../../services/productos.service';
import {CarroService} from '../../services/carro.service';

@Component({
  selector: 'app-descripcion-producto',
  templateUrl: 'descripcion-producto.component.html',
  styleUrls: ['descripcion-producto.component.scss'],
})
export class DescripcionProductoComponent implements OnInit {
  public producto;

  constructor(private productoService: ProductosService, private carroService: CarroService) {}

  ngOnInit() {
    this.producto = this.productoService.prodDesc;
  }

  cargarEnCarro(prod, cant) {
    this.carroService.agregarAlCarro(prod, cant);
  }
}
