import {
  MdListModule, MdIconModule, MdSidenavModule,
  MdInputModule, MdToolbarModule, MdProgressSpinnerModule, MdButtonModule
} from '@angular/material';
import {NgModule} from '@angular/core';

@NgModule({
  imports: [MdToolbarModule, MdProgressSpinnerModule, MdListModule, MdIconModule,
            MdSidenavModule, MdInputModule, MdButtonModule],
  exports: [MdToolbarModule, MdProgressSpinnerModule, MdListModule, MdIconModule,
            MdSidenavModule, MdInputModule, MdButtonModule]
})
export class MiMaterialModule { }
