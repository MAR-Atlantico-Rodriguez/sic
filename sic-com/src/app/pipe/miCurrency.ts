import {Pipe, PipeTransform} from '@angular/core';
@Pipe({name: 'miCurrency'})
export class MiCurrencyPipe implements PipeTransform {
  transform(value: number): string {
    return '$' + value.toFixed(2).replace('.',',').replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1.');
  }
}
