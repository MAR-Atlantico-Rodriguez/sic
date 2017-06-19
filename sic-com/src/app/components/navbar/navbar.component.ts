import {Component, OnInit} from '@angular/core';
import {ProductosService} from '../../services/productos.service';
import {RubrosService} from '../../services/rubros.service';
import {SidenavService} from '../../services/sidenav.service';
import {CarroService} from '../../services/carro.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  public rubros = [];
  public cantidadItemsCarro = 0;

  constructor(private productosService: ProductosService,
              private rubrosService: RubrosService,
              private sidenavService: SidenavService,
              private carroService: CarroService) {}

  ngOnInit() {
    this.carroService.carritoCant
      .subscribe( data => {
        this.cantidadItemsCarro = data;
      });
    this.rubrosService.getRubros().subscribe( data => this.rubros = data );
  }

  buscarProductos(palabraBuscar: string) {
    this.productosService.pagina = 0;
    this.productosService.getBuscador(palabraBuscar);
  }

  toggleSidenav() {
    this.sidenavService.toggle().then(() => {});
  }
}
