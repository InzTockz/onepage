import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Factura } from '../models/factura-cliente/factura.model';
import { ResumenCartera } from '../models/factura-cliente/resumen-cartera.model';

@Injectable({
  providedIn: 'root',
})
export class FacturaService {

  private apiFactura: string = "http://192.168.1.139:8080/api/v1/factura";
  // private apiFactura: string = "/api/v1/factura";

  constructor(private http: HttpClient) { }

  getFacturasPorCobrar(anio: number, periodo: number) {
    return this.http.get<Factura[]>(`${this.apiFactura}/factura-cliente?anio=${anio}&periodo=${periodo}`);
  }

  getFacturasPorCobrarPorAnio(anio: number) {
    return this.http.get<Factura[]>(`${this.apiFactura}/facturas-por-cobrar/anio/${anio}`);
  }

  getResumenCartera() {
    return this.http.get<ResumenCartera[]>(`${this.apiFactura}/resumen-cartera`);
  }

  getResumenCarteraPorPeriodo(periodo: number) {
    return this.http.get<ResumenCartera[]>(`${this.apiFactura}/resumen-cartera/periodo?periodo=${periodo}`)
  }
  getResumenCarteraPorPeriodoYVendedor(periodo: number, vendedor: string) {
    return this.http.get<ResumenCartera[]>(`${this.apiFactura}/resumen-cartera/periodo?periodo=${periodo}&vendedor=${vendedor}`)
  }
  getResumenCarteraPorVendedor(vendedor: string) {
    return this.http.get<ResumenCartera[]>(`${this.apiFactura}/resumen-cartera/vendedor?vendedor=${vendedor}`)
  }
}


