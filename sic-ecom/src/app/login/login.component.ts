import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AlertService } from '../services/alert.service';
import { AuthenticationService } from '../services/authentication.service';

@Component({
    templateUrl: 'login.component.html',
    //encapsulation: ViewEncapsulation.None,
    styleUrls: ['login.component.css']
})

export class LoginComponent implements OnInit {
    model: any = {};
    loading = false;
    returnUrl: string;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private authenticationService: AuthenticationService,
        private alertService: AlertService) { 
        
    }

    ngOnInit() {
        // Reiniciamos el estado del Login
        //debugger
        this.authenticationService.logout();

        // get return url from route parameters or default to '/'
        this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    }

    login() {
        this.loading = true;
        this.authenticationService.login(this.model.username, this.model.password)
            .subscribe(result => {
                this.loading = false;
                if (result === true) {
                    this.router.navigate(['/']);
                }
            },
            err => {
                this.loading = false;
                alert(err['_body']);
            });
    }
}