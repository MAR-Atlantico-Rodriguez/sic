import { Component } from '@angular/core';
import {ProductService} from '../../../services/product.service';
import { RubrosService } from '../../../services/rubros.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
	
	public rubros = [];
	public rubroActivado: false;

  	constructor(public productService:ProductService,
  				public rubrosService:RubrosService) {}

  	ngOnInit(){
		this.rubrosService.getRubros().subscribe(
			data => this.rubros = data,
			error => console.log(error)
		);
	}

  	buscadorProductos(palabraBuscar: string){
  		this.productService.getBuscador(palabraBuscar);
  	}

  	getFiltrarRubroNavbar(id){
  		this.rubroActivado = (this.rubroActivado != id)?id:false;
		this.productService.getRubro(id);
	}
}
