import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PagosVigenteResponse } from '../models/pagos/pagos-vigente-response.model';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PagoVigenteService {

  private apiPagoVigente: string = `${environment.apiUrl}/pagos-vigentes`;

  constructor(private http: HttpClient) { }

  getPagosVigentes() {
    return this.http.get<PagosVigenteResponse[]>(`${this.apiPagoVigente}/listado`);
  }

  postCargarPagosVigentes(archivo: File, codigoBanco: string): Observable<void> {
    const formData = new FormData();
    formData.append("archivo", archivo);
    formData.append("codigoBanco", codigoBanco)
    return this.http.post<void>(`${this.apiPagoVigente}/cargar`, formData);
  }
}
