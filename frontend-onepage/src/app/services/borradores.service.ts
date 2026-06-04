import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PedidosDiarios } from '../models/pedidos-diarios.model';

@Injectable({
  providedIn: 'root',
})
export class BorradoresService {
  
  // private apiBorrador:string = "http://192.168.1.139:8080/api/v1/borradores"
  private apiBorrador:string = "/api/v1/borradores"

  constructor(private http:HttpClient){};

  getPedidoDiario():Observable<PedidosDiarios[]>{
    return this.http.get<PedidosDiarios[]>(`${this.apiBorrador}/pedido-diario`);
  }
}
