import {
  MdListModule, MdIconModule, MdSidenavModule, MdInputModule, MdToolbarModule,
  MdProgressSpinnerModule, MdButtonModule, MdSnackBarModule, MdDialogModule
} from '@angular/material';
import {NgModule} from '@angular/core';

@NgModule({
  imports: [MdToolbarModule, MdProgressSpinnerModule, MdListModule, MdIconModule,
    MdSidenavModule, MdInputModule, MdButtonModule, MdSnackBarModule, MdDialogModule],
  exports: [MdToolbarModule, MdProgressSpinnerModule, MdListModule, MdIconModule,
    MdSidenavModule, MdInputModule, MdButtonModule, MdSnackBarModule, MdDialogModule]
})
export class SicComMaterialModule {}
