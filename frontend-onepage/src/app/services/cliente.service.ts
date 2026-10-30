import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Cliente } from '../models/cliente/cliente.model';
import { ClienteDeudor } from '../models/cliente/cliente-deudor.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ClienteService {

  private clienteApi: string = `${environment.apiUrl}/clientes`;

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
