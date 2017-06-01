import {Component, OnInit} from '@angular/core';
import {RubrosService} from '../../services/rubros.service';
import {ProductService} from '../../services/productos.service';
import {SidenavService} from '../../services/sidenav.service';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss']
})
export class SidenavComponent implements OnInit {

  public rubros = [];
  public rubroActivado = false;
  public loadingSidebar = false;

  constructor(private rubrosService: RubrosService,
              private productService: ProductService,
              private sidenavService: SidenavService) {}

  ngOnInit() {
    this.loadingSidebar = true;
    this.rubrosService.getRubros().subscribe(
      data => {
        this.rubros = data;
        this.loadingSidebar = false;
      }
    );
  }

  getFiltrarRubro(id) {
    this.productService.pagina = 0;
    this.rubroActivado = (this.rubroActivado !== id) ? id : false;
    this.productService.getRubro(id);
    this.sidenavService.close().then(() => {});
  }
}
