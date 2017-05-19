import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';
import {MaterialModule} from '@angular/material';
import {FlexLayoutModule} from '@angular/flex-layout';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AppComponent} from './app.component';
import {LoginComponent} from './login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {NavbarComponent} from './components/commons/navbar/navbar.component';
import {SidebarComponent} from './components/commons/sidebar/sidebar.component';
import {ProductsComponent} from './components/products/products.component';
import {ProductService} from './services/product.service';
import {AuthService} from './services/auth.service';
import {routing} from './app.routing';
import {HomeComponent} from './home/home.component';
import {AuthModule} from './auth.module';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    NavbarComponent,
    SidebarComponent,
    ProductsComponent
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
    ProductService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
