import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {MdSidenav} from '@angular/material';
import {SidenavService} from './services/sidenav.service';
import {AuthGuard} from './guards/auth.guard';

@Component({
  selector: 'app-com',
  templateUrl: './app.component.html',
  styleUrls: ['app.component.scss']
})
export class AppComponent implements AfterViewInit {
  @ViewChild('sidenav') public sidenav: MdSidenav;

  constructor(private authGuard: AuthGuard, private sidenavService: SidenavService) {
    this.authGuard.canActivate();
  }

  ngAfterViewInit() {
    this.sidenavService.setSidenav(this.sidenav);
  }
}
