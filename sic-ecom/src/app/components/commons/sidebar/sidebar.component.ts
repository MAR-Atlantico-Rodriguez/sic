import {Component, OnInit} from '@angular/core';
import {RubrosService} from '../../../services/rubros.service';
import {ProductService} from '../../../services/product.service';
import {AuthGuard} from '../../../guards/auth.guard';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {

  public rubros = [];
  public rubroActivado = false;
  public loadingSidebar = false;

  constructor(private rubrosService: RubrosService,
              private productService: ProductService,
              private authGuard: AuthGuard) {}

  ngOnInit() {
    this.authGuard.canActivate();
    this.loadingSidebar = true;
    this.rubrosService.getRubros().subscribe(
      data => {
        this.rubros = data;
        this.loadingSidebar = false;
      },
      error => console.log(error)
    );
  }

  getFiltrarRubro(id) {
    this.authGuard.canActivate();
    this.productService.pagina = 0;
    this.rubroActivado = (this.rubroActivado !== id) ? id : false;
    this.productService.getRubro(id);
  }
}
