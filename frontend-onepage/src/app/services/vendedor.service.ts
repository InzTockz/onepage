import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Vendedor } from '../models/vendedor/vendedor.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class VendedorService {

  private vendedorApi: string = `${environment.apiUrl}/vendedor`;

  constructor(private http: HttpClient) { }

  getVendedor() {
    return this.http.get<Vendedor[]>(`${this.vendedorApi}`);
  }
}
