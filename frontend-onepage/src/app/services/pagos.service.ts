import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PagosResponse } from '../models/pagos/pagos-response.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PagosService {

  private apiPago = `${environment.apiUrl}/bancos`;

  constructor(private http: HttpClient) { }

  listarPagos() {
    return this.http.get<PagosResponse[]>(`${this.apiPago}/listar`);
  }

  subirArchivoBanco(codigoBanco: string, archivo: File) {
    const formData = new FormData();
    formData.append('archivo', archivo)
    formData.append('banco', codigoBanco)
    return this.http.post<void>(`${this.apiPago}/cargar-excel`, formData);
  }
}
