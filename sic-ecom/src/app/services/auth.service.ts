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
    /*console.log(this.jwtHelper.decodeToken(token),
                this.jwtHelper.getTokenExpirationDate(token),
                this.jwtHelper.isTokenExpired(token));*/

    //Genera un Token de 24Horas - isTokenExpired pregunta si expiro el token
    //da falso, en caso contrario da TRUE 
    //lo nego al return para que ingrese, si da true entonces lo pasa a false 
    //y redirecciona al Login
    return !this.jwtHelper.isTokenExpired(token);
  }
}
