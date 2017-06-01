import {Component, OnInit} from '@angular/core';
import {ProductosService} from '../../services/productos.service';
import {RubrosService} from '../../services/rubros.service';
import {SidenavService} from '../../services/sidenav.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {

  public rubros = [];

  constructor(private productosService: ProductosService,
              private rubrosService: RubrosService,
              private sidenavService: SidenavService) {}

  ngOnInit() {
    this.rubrosService.getRubros().subscribe(
      data => this.rubros = data
    );
  }

  buscarProductos(palabraBuscar: string) {
    this.productosService.pagina = 0;
    this.productosService.getBuscador(palabraBuscar);
  }

  toggleSidenav(){
    this.sidenavService.toggle().then(() => {});
  }
}
