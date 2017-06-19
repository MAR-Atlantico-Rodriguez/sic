import {Injectable} from '@angular/core';
import {MdSnackBar, MdSnackBarConfig} from '@angular/material';
import {Subject} from 'rxjs/Subject';

@Injectable()
export class CarroService {
  public carrito = [];

  private carritoSubject = new Subject<any>();
  public carritoCant = this.carritoSubject.asObservable();

  constructor(public snackBar: MdSnackBar) {}

  addCarrito(nro) {
    this.carritoSubject.next(nro);
  }

  agregarAlCarro(prod, cant) {
    const indexOf: number = this.indexOfProducto(prod.descripcion);
    let msj = '';
    let action = '';
    if (cant > 0 && prod.cantidad >= cant) {
      if (indexOf > -1) {
        if (prod.cantidad >= cant) {
          this.carrito[indexOf].cant = parseInt(this.carrito[indexOf].cant) + parseInt(cant);
          prod.cantidad = prod.cantidad - cant;
          msj = 'Se modifico la cantidad correctamente';
          action = '';
        } else {
          msj = 'Supero la cantidad en STOCK, Disponemos de ' + (prod.cantidad - this.carrito[indexOf].cant) + ' Productos.';
          action = 'ERROR';
        }
      } else {
        this.carrito.push({prod, cant});
        this.addCarrito(this.carrito.length);
        prod.cantidad = prod.cantidad - cant;
        msj = 'Se agrego al presupuesto';
        action = '';
      }
    } else {
      msj = 'La cantidad debe ser mayor a 0, Hay en STOCK: ' + ((prod.cantidad > 0) ? prod.cantidad : 0);
      action = 'ERROR';
    }
    this.openSnackBar(msj, action);
  }

  indexOfProducto(descripcion) {
    return this.carrito.map(function (e) {
      return e.prod.descripcion;
    }).indexOf(descripcion);
  }

  openSnackBar(message: string, action: string) {
    const config = new MdSnackBarConfig();
    config.duration = 3500;
    config.extraClasses = ['app-comSnackBar'];
    this.snackBar.open(message, action, config);
  }

}
