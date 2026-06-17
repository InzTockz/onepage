import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PagosResponse } from '../models/pagos/pagos-response.model';

@Injectable({
  providedIn: 'root',
})
export class PagosService {
  private apiPago = 'http://localhost:8080/api/v1/bancos';

  constructor(private http: HttpClient) {}

  listarPagos() {
    return this.http.get<PagosResponse[]>(`${this.apiPago}/listar`);
  }
}
