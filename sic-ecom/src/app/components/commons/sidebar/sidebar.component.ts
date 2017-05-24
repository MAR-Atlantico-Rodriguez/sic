import { Component } from '@angular/core';
import { RubrosService } from '../../../services/rubros.service';
import { ProductService } from '../../../services/product.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
	
	public rubros = [];

	public rubroActivado: false;

	constructor(public rubrosService:RubrosService,
				public productService:ProductService) {
	}

	ngOnInit(){
		this.rubrosService.getRubros().subscribe(
			data => this.rubros = data,
			error => console.log(error)
		);
	}

	getFiltrarRubro(id){
		this.rubroActivado = (this.rubroActivado != id)?id:false;
		this.productService.getRubro(id);
	}


}
