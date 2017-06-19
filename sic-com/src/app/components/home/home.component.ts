import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {AuthGuard} from '../../guards/auth.guard';
import {SidenavService} from '../../services/sidenav.service';
import {MdSidenav} from '@angular/material';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  constructor(private authGuard: AuthGuard) {
    this.authGuard.canActivate();
  }
}
