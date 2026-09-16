import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LotePedido } from '../models/borrador/lote-pedido.model';


@Injectable({
  providedIn: 'root',
})
export class LotePedidoService {

  // private apiLotePedido: string = "http://192.168.1.139:8080/api/v1/lote-pedido";
  private apiLotePedido: string = "/api/v1/lote-pedido";

  constructor(private http: HttpClient) { }

  getLotePedidos(): Observable<LotePedido[]> {
    return this.http.get<LotePedido[]>(`${this.apiLotePedido}/listar`);
  }

  postLotePedidos(): Observable<void> {
    return this.http.post<void>(`${this.apiLotePedido}/registrar`, null);
  }

  getGenerarEnvio() {
    return this.http.put<void>(`${this.apiLotePedido}/generar-envio`, null);
  }
}
