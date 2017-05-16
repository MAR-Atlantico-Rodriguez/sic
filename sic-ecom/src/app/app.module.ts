import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule, Http, RequestOptions } from '@angular/http';
import { MaterialModule } from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { AuthGuard } from './_guards/auth.guard';
import { AlertComponent } from './alert/alert.component';
import { NavbarComponent } from './components/commons/navbar/navbar.component';
import { SidebarComponent } from './components/commons/sidebar/sidebar.component';
import { ProductsComponent } from './components/products/products.component';

import { ProductService } from './services/product.service';
import { AuthenticationService } from './services/authentication.service';
import { AlertService } from './services/alert.service';

import {routing} from './app.routing';
import { HomeComponent } from './home/home.component';
import { AuthModule } from './login/auth.module';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    LoginComponent,
    AlertComponent,
    SidebarComponent,
    ProductsComponent,
    HomeComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    MaterialModule,
    FlexLayoutModule,
    BrowserAnimationsModule,
    AuthModule,
    //RouterModule.forRoot(appRoutes)
    routing
  ],
  providers: [
    AuthGuard,
    AlertService,
    AuthenticationService,
    ProductService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
