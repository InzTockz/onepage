import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LotePedido } from '../models/borrador/lote-pedido.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class BorradoresService {

  private apiBorrador: string = `${environment.apiUrl}/borradores`

  constructor(private http: HttpClient) { };

  getPedidosDiarios() {
    return this.http.get<LotePedido[]>(`${this.apiBorrador}/lista/pedidos-diarios`);
  }

  listaLoteGenerado() {
    return this.http.get<LotePedido[]>(`${this.apiBorrador}/lista/lote-generado`);
  }

  listaPedidosEnviados() {
    return this.http.get<LotePedido[]>(`${this.apiBorrador}/lista/lote-enviado`);
  }

  agregarComentario(idBorrador: number, lotePedido: LotePedido) {
    return this.http.put<LotePedido>(`${this.apiBorrador}/pedidos-diarios/comentario/${idBorrador}`, lotePedido);
  }

  registrarPedidos() {
    return this.http.post<void>(`${this.apiBorrador}/registro-pedidos`, null);
  }

  generarLote(borradoresRequest: LotePedido[]): Observable<void> {
    return this.http.put<void>(`${this.apiBorrador}/pedidos-diarios/generar-lote`, borradoresRequest);
  }

  enviarLote(): Observable<void> {
    return this.http.put<void>(`${this.apiBorrador}/pedidos-diarios/enviar-lote`, null);
  }
}
