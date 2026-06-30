import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class PagoVigenteService {

  private apiPagoVigente: string = "http://localhost:8080/api/v1/pagos-vigentes";

  constructor(private http: HttpClient) { }
}
