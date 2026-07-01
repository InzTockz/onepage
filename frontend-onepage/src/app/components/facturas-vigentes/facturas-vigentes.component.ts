import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { PagosVigenteResponse } from '../../models/pagos/pagos-vigente-response.model';
import { PagoVigenteService } from '../../services/pago-vigente.service';

interface FacturaVigente {
  idPago: number;
  idBanco: number;
  nroTransaccion: string;
  nroFactura: string;
  aceptante: string;
  fechaOperacion: string;
  fechaVencimiento: string | null;
  moneda: string;
  importe: number;
  estado: string;
}

@Component({
  selector: 'app-facturas-vigentes',
  imports: [CommonModule, FormsModule],
  templateUrl: './facturas-vigentes.component.html',
  styleUrl: './facturas-vigentes.component.css',
})
export class FacturasVigentesComponent implements OnInit {

  bancos = [
    { id: 1, codigo: 'BBVA', nombre: 'BBVA Perú', color: 'blue', logo: 'assets/logos/bbva.svg' },
    {
      id: 2,
      codigo: 'BCP',
      nombre: 'Banco de Crédito del Perú',
      color: 'orange',
      logo: 'assets/logos/bcp.svg',
    },
    {
      id: 3,
      codigo: 'SCOTIA',
      nombre: 'Scotiabank Perú',
      color: 'red',
      logo: 'assets/logos/scotiabank.svg',
    },
    {
      id: 4,
      codigo: 'IBK',
      nombre: 'Interbank',
      color: 'green',
      logo: 'assets/logos/interbank.svg',
    },
  ];

  bancoActivo = signal<number>(1);
  pagos: PagosVigenteResponse[] = [];
  filtroTexto = '';

  // Paginación
  paginaActual = signal(1);
  tamanioPagina = 10;

  // Modal de carga
  modalAbierto = signal(false);
  bancoSeleccionadoCarga = '';
  archivoSeleccionado: File | null = null;
  cargandoArchivo = signal(false);
  errorCarga = signal<string | null>(null);

  constructor(private pagoVigenteService: PagoVigenteService, private toastService: ToastrService) { }

  ngOnInit(): void {
    this.cargarPagos();
  }

  cargarPagos() {
    this.pagoVigenteService.getPagosVigentes().subscribe((data) => {
      this.pagos = data;
      this.paginaActual.set(1);
    });
  }

  get pagosFiltrados(): PagosVigenteResponse[] {
    let resultado = this.pagos.filter((p) => p.idBanco === this.bancoActivo());

    if (this.filtroTexto !== '') {
      const texto = this.filtroTexto.toLowerCase();
      resultado = resultado.filter(
        (p) =>
          p.aceptante.toLowerCase().includes(texto) ||
          p.nroFactura.toLowerCase().includes(texto) ||
          p.nroUnico.toLowerCase().includes(texto),
      );
    }

    return resultado;
  }

  get pagosPaginados(): PagosVigenteResponse[] {
    const inicio = (this.paginaActual() - 1) * this.tamanioPagina;
    return this.pagosFiltrados.slice(inicio, inicio + this.tamanioPagina);
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.pagosFiltrados.length / this.tamanioPagina));
  }

  get rangoInicio(): number {
    return this.pagosFiltrados.length === 0 ? 0 : (this.paginaActual() - 1) * this.tamanioPagina + 1;
  }

  get rangoFin(): number {
    return Math.min(this.paginaActual() * this.tamanioPagina, this.pagosFiltrados.length);
  }

  get paginasVisibles(): (number | '…')[] {
    const total = this.totalPaginas;
    const actual = this.paginaActual();
    if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1);

    const paginas: (number | '…')[] = [1];
    const inicio = Math.max(2, actual - 1);
    const fin = Math.min(total - 1, actual + 1);

    if (inicio > 2) paginas.push('…');
    for (let i = inicio; i <= fin; i++) paginas.push(i);
    if (fin < total - 1) paginas.push('…');
    paginas.push(total);
    return paginas;
  }

  irAPagina(p: number | '…') {
    if (p === '…') return;
    if (p < 1 || p > this.totalPaginas) return;
    this.paginaActual.set(p);
  }

  paginaAnterior() { this.irAPagina(this.paginaActual() - 1); }
  paginaSiguiente() { this.irAPagina(this.paginaActual() + 1); }

  get bancoSeleccionadoData() {
    return this.bancos.find((b) => b.id === this.bancoActivo())!;
  }

  cambiarBanco(id: number) {
    this.bancoActivo.set(id);
    this.filtroTexto = '';
    this.paginaActual.set(1);
  }

  onFiltroChange(valor: string) {
    this.filtroTexto = valor;
    this.paginaActual.set(1);
  }

  abrirModal() {
    this.modalAbierto.set(true);
    this.bancoSeleccionadoCarga = '';
    this.archivoSeleccionado = null;
    this.errorCarga.set(null);
  }

  cerrarModal() {
    this.modalAbierto.set(false);
  }

  onArchivoSeleccionado(event: Event) {
    const input = event.target as HTMLInputElement;
    this.errorCarga.set(null);
    if (input.files && input.files.length > 0) {
      const archivo = input.files[0];
      if (!/\.(xlsx|xls)$/i.test(archivo.name)) {
        this.archivoSeleccionado = null;
        this.errorCarga.set('El archivo seleccionado no es un Excel válido (.xlsx o .xls).');
        input.value = '';
        return;
      }
      this.archivoSeleccionado = archivo;
    }
  }

  subirArchivo() {
    if (!this.bancoSeleccionadoCarga || !this.archivoSeleccionado) return;

    this.cargandoArchivo.set(true);

    this.pagoVigenteService.postCargarPagosVigentes(this.archivoSeleccionado, this.bancoSeleccionadoCarga).subscribe({
      next: () => {
        this.cargarPagos();
        this.toastService.success('Documento cargado', 'Exito')
        setTimeout(() => {
          this.cargandoArchivo.set(false);
          this.cerrarModal();
        }, 1000);
      },
      error: (err: HttpErrorResponse) => {
        this.cargandoArchivo.set(false);
        const mensaje = this.extraerMensajeError(err);

        if (err.status === 400) {
          this.errorCarga.set(mensaje);
          this.toastService.warning(mensaje, 'Archivo no válido');
        } else {
          this.toastService.error(mensaje, 'Error');
        }
      },
    });
  }

  private extraerMensajeError(err: HttpErrorResponse): string {
    const cuerpo = err.error;
    if (cuerpo && typeof cuerpo === 'object') {
      if (typeof cuerpo.error === 'string') return cuerpo.error;
      if (typeof cuerpo.message === 'string') return cuerpo.message;
    }
    if (typeof cuerpo === 'string' && cuerpo.trim()) return cuerpo;
    if (err.status === 0) return 'No se pudo conectar con el servidor. Verifica tu conexión.';
    return 'No se pudo cargar el documento. Intenta nuevamente.';
  }

  getEstadoClase(estado: string): string {
    switch (estado) {
      case 'VIGENTE':
        return 'bg-amber-50 text-amber-700';
      case 'INGRESADO':
        return 'bg-indigo-50 text-indigo-700';
      case 'RENOVADO':
        return 'bg-purple-50 text-purple-700';
      case 'AMORTIZADO':
        return 'bg-blue-50 text-blue-700';
      case 'PROTESTADO':
        return 'bg-red-50 text-red-700';
      case 'DEVUELTO':
        return 'bg-amber-50 text-amber-700';
      default:
        return 'bg-gray-50 text-gray-600';
    }
  }

  getColorBanco(codigo: string): string {
    switch (codigo) {
      case 'BBVA':
        return 'text-blue-700';
      case 'BCP':
        return 'text-orange-600';
      case 'SCOTIA':
        return 'text-red-600';
      case 'IBK':
        return 'text-green-700';
      default:
        return 'text-gray-700';
    }
  }
}
