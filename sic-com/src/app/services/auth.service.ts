import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import {tokenNotExpired} from 'angular2-jwt';
import {environment} from '../../environments/environment';
import {HttpInterceptorService} from 'ng-http-interceptor';
import {Router} from '@angular/router';
import {Subject} from 'rxjs/Subject';

@Injectable()
export class AuthService {

  private url = environment.apiUrl + '/api/v1/login';
  private token: string;
  private mensajeSubject = new Subject<string>();
  public msjError = this.mensajeSubject.asObservable();

  constructor(private http: Http, private httpInterceptor: HttpInterceptorService, private router: Router) {
    this.httpInterceptor.response().addInterceptor(this.responseInterceptor);
    const token = localStorage.getItem('token');
  }

  private responseInterceptor = (o: Observable<Response>, method: string): Observable<Response> => {
    o.subscribe(
      (r: Response) => {
        // console.log(r.status);
      },
      (e: Response) => {
        if (e.status === 401) {
          // redirect user to login screen here
          //console.log(e);
        }
        this.mensajeErrorFunction(e.text());
        this.router.navigate(['login']);
      });
    return o;
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

  mensajeErrorFunction(data: string) {
    this.mensajeSubject.next(data);
  }
}
