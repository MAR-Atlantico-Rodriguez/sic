import {BrowserModule} from '@angular/platform-browser';
import {NgModule, Pipe, PipeTransform} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';
import {MaterialModule} from '@angular/material';
import {FlexLayoutModule} from '@angular/flex-layout';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AppComponent} from './app.component';
import {LoginComponent} from './login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {NavbarComponent} from './components/navbar/navbar.component';
import {SidenavComponent} from './components/sidenav/sidenav.component';
import {ProductosComponent} from './components/productos/productos.component';
import {ProductosService} from './services/productos.service';
import {AuthService} from './services/auth.service';
import {RubrosService} from './services/rubros.service';
import {routing} from './app.routing';
import {HomeComponent} from './home/home.component';
import {AuthModule} from './auth.module';
import {SidenavService} from "./services/sidenav.service";

@Pipe({name: 'miCurrency'})
export class MiCurrencyPipe implements PipeTransform {
  /**
   *
   * @param value
   * @returns {number}
   */
  transform(value: number): string {
      return '$'+value.toFixed(2).replace(".", ",").replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1.");
  }
}

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    NavbarComponent,
    SidenavComponent,
    ProductosComponent,
    MiCurrencyPipe
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    MaterialModule,
    FlexLayoutModule,
    BrowserAnimationsModule,
    AuthModule,
    routing
  ],
  providers: [
    AuthGuard,
    AuthService,
    ProductosService,
    RubrosService,
    SidenavService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
