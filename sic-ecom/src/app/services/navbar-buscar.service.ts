import { Injectable } from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import 'rxjs/add/operator/map';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/do';

@Injectable()
export class NavbarBuscarService {

  newMessages: Subject<any> = new Subject<any>();
  busq: String = "hola";

  constructor() { 
      debugger
  }

  agregarBusqueda(busqueda: String, ) {
    debugger
    this.newMessages.next(busqueda);
    this.busq = busqueda;
  }

  getBusq() {
      return this.busq;
  }

  setBusq(bu: String) {
      this.busq = bu;
  }

}
