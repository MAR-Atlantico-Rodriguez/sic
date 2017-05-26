import { Component } from '@angular/core';
import { AuthGuard } from '../guards/auth.guard';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent  {
		

	constructor(public authGuard:AuthGuard) {
		this.authGuard.canActivate();
	}


}
