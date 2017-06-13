import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Http, HttpModule} from '@angular/http';
import {FlexLayoutModule} from '@angular/flex-layout';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AppComponent} from './app.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {NavbarComponent} from './components/navbar/navbar.component';
import {SidenavComponent} from './components/sidenav/sidenav.component';
import {ProductosComponent} from './components/productos/productos.component';
import {ProductosService} from './services/productos.service';
import {AuthService} from './services/auth.service';
import {RubrosService} from './services/rubros.service';
import {routing} from './app.routing';
import {HomeComponent} from './components/home/home.component';
import {AuthModule} from './auth.module';
import {SidenavService} from './services/sidenav.service';
import {SicComCurrencyPipe} from './pipes/sicComCurrency';
import {SicComMaterialModule} from './sic.com.material.module';
import { DescripcionProductoComponent } from './components/descripcion-producto/descripcion-producto.component';
import {HttpInterceptorModule} from 'ng-http-interceptor';


@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    NavbarComponent,
    SidenavComponent,
    ProductosComponent,
    SicComCurrencyPipe,
    DescripcionProductoComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    SicComMaterialModule,
    FlexLayoutModule,
    BrowserAnimationsModule,
    AuthModule,
    routing,
    HttpInterceptorModule
  ],
  providers: [
    AuthGuard,
    AuthService,
    ProductosService,
    RubrosService,
    SidenavService
  ],
  entryComponents: [DescripcionProductoComponent],
  bootstrap: [AppComponent]
})
export class AppModule {
}
