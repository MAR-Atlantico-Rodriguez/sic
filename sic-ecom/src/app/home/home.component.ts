import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

public token: string;
  constructor() { 
  	
        
  }

  ngOnInit() {
  	this.token = JSON.parse(localStorage.getItem('currentUser'));
  }




}
