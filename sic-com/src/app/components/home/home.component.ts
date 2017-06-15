import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {AuthGuard} from '../../guards/auth.guard';
import {SidenavService} from '../../services/sidenav.service';
import {MdSidenav} from '@angular/material';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements AfterViewInit {

  @ViewChild('sidenav') public sidenav: MdSidenav;

  constructor(private authGuard: AuthGuard, private sidenavService: SidenavService) {
    this.authGuard.canActivate();
  }

  ngAfterViewInit() {
    this.sidenavService.setSidenav(this.sidenav);
  }
}
