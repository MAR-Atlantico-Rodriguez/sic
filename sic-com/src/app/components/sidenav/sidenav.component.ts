import {Component, OnInit} from '@angular/core';
import {RubrosService} from '../../services/rubros.service';
import {ProductosService} from '../../services/productos.service';
import {SidenavService} from '../../services/sidenav.service';
import {CarroService} from '../../services/carro.service';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss']
})
export class SidenavComponent implements OnInit {

  public rubros = [];
  public rubroActivado = false;
  public loadingSidebar = false;
  public cantidadItemsCarro = 0;

  constructor(private rubrosService: RubrosService,
              private productService: ProductosService,
              private sidenavService: SidenavService,
              private carroService: CarroService) {}

  ngOnInit() {
    this.loadingSidebar = true;
    this.rubrosService.getRubros().subscribe(
      data => {
        this.rubros = data;
        this.loadingSidebar = false;
      }
    );
    this.carroService.carritoCant
      .subscribe( data => {
        this.cantidadItemsCarro = data;
      });
  }

  getFiltrarRubro(id) {
    this.productService.pagina = 0;
    this.rubroActivado = (this.rubroActivado !== id) ? id : false;
    this.productService.getRubro(id);
    this.toggleSidenav();
  }

  toggleSidenav() {
    this.sidenavService.toggle().then(() => {});
  }
}
