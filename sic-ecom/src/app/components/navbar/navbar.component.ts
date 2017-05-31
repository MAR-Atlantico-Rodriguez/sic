import {Component, OnInit} from '@angular/core';
import {ProductService} from '../../services/product.service';
import {RubrosService} from '../../services/rubros.service';
import {SidenavService} from "../../services/sidenav.service";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {

  public rubros = [];

  constructor(private productService: ProductService,
              private rubrosService: RubrosService,
              private sidenavService: SidenavService) {}

  ngOnInit() {
    this.rubrosService.getRubros().subscribe(
      data => this.rubros = data
    );
  }

  buscadorProductos(palabraBuscar: string) {
    this.productService.pagina = 0;
    this.productService.getBuscador(palabraBuscar);
  }

  toggleSidenav(){
    this.sidenavService.toggle().then(() => {});
  }
}
