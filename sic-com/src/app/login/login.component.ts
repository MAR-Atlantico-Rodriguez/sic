import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../services/auth.service';

@Component({
  templateUrl: 'login.component.html',
  styleUrls: ['login.component.css']
})
export class LoginComponent implements OnInit {
  model: any = {};
  loading = false;
  msjError: string;

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit() {
    this.authService.logout();
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
          this.msjError = err.text();
        });
  }
}
