import { Component } from '@angular/core';
import { RubrosService } from '../../../services/rubros.service';
import { ProductService } from '../../../services/product.service';
import {AuthGuard} from '../../../guards/auth.guard';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
	
	public rubros = [];

	public rubroActivado: false;

	public loadingSidebar = false;

	constructor(public rubrosService:RubrosService,
				public productService:ProductService,
				public authGuard:AuthGuard) {

	}

	ngOnInit(){
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

	getFiltrarRubro(id){
		this.authGuard.canActivate();
		this.rubroActivado = (this.rubroActivado != id)?id:false;
		this.productService.getRubro(id);
	}


}
