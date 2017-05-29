import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import {tokenNotExpired,JwtHelper} from 'angular2-jwt';

@Injectable()
export class AuthService {

  private urlApi = 'https://sic-api.herokuapp.com/api/v1/login';
  private token: string;

  jwtHelper: JwtHelper = new JwtHelper();

  constructor(private http: Http) {
    const token = localStorage.getItem('token');
  }

  login(username: string, password: string): Observable<boolean> {
    return this.http.post(this.urlApi, {username: username, password: password})
      .map((response: Response) => {
        const token = response.text();
        if (token) {
          localStorage.setItem('token', token);
          return true;
        } else {
          return false;
        }
      });
  }

  logout(): void {
    this.token = null;
    localStorage.removeItem('token');
  }

  loggedIn(): boolean {
    return tokenNotExpired();
  }



  useJwtHelper() {
    let token = localStorage.getItem('token');
    console.log(this.jwtHelper.decodeToken(token),
                this.jwtHelper.getTokenExpirationDate(token),
                this.jwtHelper.isTokenExpired(token));

    return this.jwtHelper.isTokenExpired(token);
  }
}
