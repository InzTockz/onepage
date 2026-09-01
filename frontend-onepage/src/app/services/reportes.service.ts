import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ReportesService {

  // private apiReporte = 'http://localhost:8080/api/v1/reportes';
  private apiReporte = '/api/v1/reportes';

  constructor(private http: HttpClient) { }

  reporteGeneralDeFacturas(): Observable<Blob> {
    return this.http.get(`${this.apiReporte}/facturas-totales`,
      { responseType: 'blob' }
    )
  };

  facturasPorVendedor(slpCode: number): Observable<Blob> {
    const parametros = new HttpParams().set('vendedor', slpCode)

    return this.http.get(`${this.apiReporte}/excel/estado-cuenta-por-vendedor`, {
      params: parametros, responseType: 'blob'
    });
  }

  estadoCuentaPorVendedorPdf(slpCode: number): Observable<Blob> {
    const parametros = new HttpParams().set('vendedor', slpCode)
    return this.http.get(`${this.apiReporte}/pdf/estado-cuenta-por-vendedor`, {
      params: parametros, responseType: 'blob'
    })
  }

  reporteFactuasYLetrasCanceladasCSV(idBanco: number): Observable<Blob> {
    return this.http.get(`${this.apiReporte}/csv/reporte-fac-let-canceladas/${idBanco}`, {
      responseType: 'blob'
    });
  }

}
