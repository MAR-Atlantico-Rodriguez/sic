import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {MdSnackBar, MdSnackBarConfig} from '@angular/material';

@Component({
  templateUrl: 'login.component.html',
  styleUrls: ['login.component.css']
})
export class LoginComponent implements OnInit {
  model: any = {};
  loading = false;
  msjError: string;

  constructor(private router: Router, private authService: AuthService, public snackBar: MdSnackBar) {}

  ngOnInit() {
    this.authService.logout();
    this.authService.msjError.subscribe(
      ( data: string ) => { this.msjError = data;}
    );
  }

  login() {
    this.loading = true;
    this.authService.login(this.model.username, this.model.password)
      .subscribe(
        data => {
          this.loading = false;
          if (data === true) {
            this.router.navigate(['/']);
          }
        },
        err => {
          this.loading = false;
          this.openSnackBar(this.msjError, 'ERROR');
        });
  }

  openSnackBar(message: string, action: string) {
    var config = new MdSnackBarConfig();
    config.duration = 3500;
    config.extraClasses = ['app-comSnackBar'];
    this.snackBar.open(message, action, config);
  }
}
