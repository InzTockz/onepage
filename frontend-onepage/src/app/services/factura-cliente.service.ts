import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FacturasPorCobrar } from '../models/factura-cliente/facturas-por-cobrar.model';
import { FacturasPorCobrarTopDiez } from '../models/factura-cliente/facturas-por-cobrar-top-diez.model';
import { ResumenCarteraSap } from '../models/factura-cliente/resumen-cartera-sap.model';

@Injectable({
  providedIn: 'root',
})
export class FacturaClienteService {
  // private facturaClienteApi = 'http://localhost:8080/api/v1/facturas-cliente';
  private facturaClienteApi = "/api/v1/facturas-cliente";

  constructor(private http: HttpClient) { }

  getFacturasPorCobrar() {
    return this.http.get<FacturasPorCobrar[]>(`${this.facturaClienteApi}/facturas-por-cobrar`);
  }

  getFacturasPorCobrarCliente(ruc: string) {
    return this.http.get<FacturasPorCobrar[]>(
      `${this.facturaClienteApi}/facturas-por-cobrar/cliente/${ruc}`,
    );
  }

  getFacturasPorCobrarVendedor(slpCode: number) {
    return this.http.get<FacturasPorCobrar[]>(
      `${this.facturaClienteApi}/facturas-por-cobrar/vendedor/${slpCode}`,
    );
  }

  getFacturasPorCobrarPorVendedorYCliente(slpCode: number, ruc: string) {
    return this.http.get<FacturasPorCobrar[]>(
      `${this.facturaClienteApi}/facturas-por-cobrar/vendedor/${slpCode}/cliente/${ruc}`,
    );
  }

  getFacturasPorCobrarTopDiez() {
    return this.http.get<FacturasPorCobrarTopDiez[]>(
      `${this.facturaClienteApi}/facturas-por-cobrar/top-diez`,
    );
  }

  getFacturasPorCobrarVencidasTopDiez() {
    return this.http.get<any[]>(`${this.facturaClienteApi}/facturas-por-cobrar/vencidos/top-diez`);
  }

  getResumenCartera() {
    return this.http.get<ResumenCarteraSap[]>(`${this.facturaClienteApi}/resumen-cartera`);
  }


}
