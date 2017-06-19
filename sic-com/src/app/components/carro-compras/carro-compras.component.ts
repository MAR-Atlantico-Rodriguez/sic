import {Component, OnInit} from '@angular/core';
import {SidenavService} from '../../services/sidenav.service';
import {AuthGuard} from '../../guards/auth.guard';
import {CarroService} from '../../services/carro.service';

@Component({
  selector: 'app-carro-compras',
  templateUrl: './carro-compras.component.html',
  styleUrls: ['./carro-compras.component.scss', '../home/home.component.scss']
})
export class CarroComprasComponent implements OnInit {
  public carrito = [];
  public totalLista = 0;
  public totalContado = 0;
  public cantidadArticulos = 0;

  constructor(private authGuard: AuthGuard, private sidenavService: SidenavService, private carroService: CarroService) {
    this.authGuard.canActivate();
  }

  ngOnInit() {
    this.carrito = this.carroService.carrito;
    this.sumarTotales();
  }

  sumarTotales() {
    let TL = 0;    let TC = 0;    let CA = 0;
    this.carrito.forEach(function(prod) {
      TL += parseFloat(prod.prod.precioLista) * parseFloat(prod.cant);
      TC += parseFloat(prod.prod.precioVentaPublico) * parseFloat(prod.cant);
      CA += parseInt(prod.cant);
    });
    this.totalLista = TL;
    this.totalContado = TC;
    this.cantidadArticulos = CA;
  }
}
