import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import {tokenNotExpired, JwtHelper} from 'angular2-jwt';
import {environment} from '../../environments/environment';

@Injectable()
export class AuthService {

  private url = environment.apiUrl + '/api/v1/login';
  private token: string;
  private jwtHelper: JwtHelper = new JwtHelper();

  constructor(private http: Http) {
    const token = localStorage.getItem('token');
  }

  login(username: string, password: string): Observable<boolean> {
    return this.http.post(this.url, {username: username, password: password})
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
}
