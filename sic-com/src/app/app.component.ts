import {Component} from '@angular/core';
import {AuthGuard} from './guards/auth.guard';

@Component({
  selector: 'app-com',
  templateUrl: './app.component.html',
  styleUrls: ['app.component.scss']
})
export class AppComponent {
  constructor(public authGuard: AuthGuard) {}
}
