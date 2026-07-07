import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Cliente } from '../models/cliente/cliente.model';
import { ClienteDeudor } from '../models/cliente/cliente-deudor.model';

@Injectable({
  providedIn: 'root',
})
export class ClienteService {

  // private clienteApi: string = "http://localhost:8080/api/v1/clientes";
  private clienteApi: string = "/api/v1/clientes";

  constructor(private http: HttpClient) { }

  getClientes() {
    return this.http.get<Cliente[]>(`${this.clienteApi}`);
  }

  getClientesPorVendedor(idVendedor: number) {
    return this.http.get<Cliente[]>(`${this.clienteApi}/vendedor/${idVendedor}`);
  }

  getDeudores() {
    return this.http.get<ClienteDeudor[]>(`${this.clienteApi}/deudor`);
  }

  getDeudoresPorVendedor(idVendedor: number) {
    return this.http.get<ClienteDeudor[]>(`${this.clienteApi}/deudor/vendedor?idVendedor=${idVendedor}`)
  }
}
