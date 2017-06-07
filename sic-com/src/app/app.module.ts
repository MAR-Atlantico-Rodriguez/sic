import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';
/*import {MaterialModule} from '@angular/material';*/
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
import {SidenavService} from './services/sidenav.service';
import {MiCurrencyPipe} from './pipe/miCurrency';
import {MiMaterialModule} from './miMaterial.module';


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
    MiMaterialModule,
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
