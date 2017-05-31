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
  public rubroActivado = false;

  constructor(private productService: ProductService,
              private rubrosService: RubrosService,
              private sidenavService: SidenavService) {}

  ngOnInit() {
    this.rubrosService.getRubros().subscribe(
      data => this.rubros = data,
      error => console.log(error)
    );
  }

  buscadorProductos(palabraBuscar: string) {
    this.productService.pagina = 0;
    this.productService.getBuscador(palabraBuscar);
  }

  getFiltrarRubroNavbar(id) {
    this.productService.pagina = 0;
    this.rubroActivado = (this.rubroActivado !== id) ? id : false;
    this.productService.getRubro(id);
  }

  toggleSidenav(){
    this.sidenavService.toggle().then(() => {});
  }
}
