import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Vendedor } from '../models/vendedor/vendedor.model';

@Injectable({
  providedIn: 'root',
})
export class VendedorService {

  // private vendedorApi: string = "http://localhost:8080/api/v1/vendedor";
  private vendedorApi: string = "/api/v1/vendedor";

  constructor(private http: HttpClient) { }

  getVendedor() {
    return this.http.get<Vendedor[]>(`${this.vendedorApi}`);
  }
}
