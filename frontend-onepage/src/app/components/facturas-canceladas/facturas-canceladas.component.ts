import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PagosResponse } from '../../models/pagos/pagos-response.model';
import { PagosService } from '../../services/pagos.service';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-facturas-canceladas',
  imports: [CommonModule, FormsModule],
  templateUrl: './facturas-canceladas.component.html',
  styleUrl: './facturas-canceladas.component.css',
})
export class FacturasCanceladasComponent implements OnInit {
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
  pagos: PagosResponse[] = [];
  filtroTexto = '';

  // Paginación
  paginaActual = signal(1);   // 👈 NUEVO
  tamanioPagina = 10;         // 👈 NUEVO (cámbialo si quieres otro tamaño)

  // Modal de carga
  modalAbierto = signal(false);
  bancoSeleccionadoCarga = '';
  archivoSeleccionado: File | null = null;
  cargandoArchivo = signal(false);
  errorCarga = signal<string | null>(null); // 👈 NUEVO: mensaje visible en el modal

  constructor(private pagoService: PagosService, private toastService: ToastrService) { }

  ngOnInit(): void {
    this.cargarPagos();
  }

  cargarPagos() {
    this.pagoService.listarPagos().subscribe((data) => {
      this.pagos = data;
      this.paginaActual.set(1);
    });
  }

  get pagosFiltrados(): PagosResponse[] {
    let resultado = this.pagos.filter((p) => p.idBanco === this.bancoActivo());

    if (this.filtroTexto !== '') {
      const texto = this.filtroTexto.toLowerCase();
      resultado = resultado.filter(
        (p) =>
          p.aceptante.toLowerCase().includes(texto) ||
          p.nroFactura.toLowerCase().includes(texto) ||
          p.nroTransaccion.toLowerCase().includes(texto),
      );
    }

    return resultado;
  }

  // 👇 NUEVO: solo las 10 filas de la página actual
  get pagosPaginados(): PagosResponse[] {
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

  /** Páginas a mostrar con ventana + elipsis cuando hay muchas. */
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

  get totalImporte(): number {
    return this.pagosFiltrados.reduce((acc, p) => acc + p.importe, 0);
  }

  get bancoSeleccionadoData() {
    return this.bancos.find((b) => b.id === this.bancoActivo())!;
  }

  cambiarBanco(id: number) {
    this.bancoActivo.set(id);
    this.filtroTexto = '';
    this.paginaActual.set(1);   // 👈 reinicia al cambiar de banco
  }

  // 👇 NUEVO: actualiza el filtro y vuelve a la página 1
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
      // Guard en cliente: extensión equivocada (el "accept" no es garantía)
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

    // Aquí conectas tu servicio real de carga
    this.pagoService.subirArchivoBanco(this.bancoSeleccionadoCarga, this.archivoSeleccionado).subscribe({
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
          // Archivo equivocado: mensaje específico del backend + se queda visible en el modal
          this.errorCarga.set(mensaje);
          this.toastService.warning(mensaje, 'Archivo no válido');
        } else {
          // Otros errores (500, red, etc.)
          this.toastService.error(mensaje, 'Error');
        }
      },
    });
  }

  /** Extrae el mensaje del backend de forma robusta ante distintas formas de respuesta. */
  private extraerMensajeError(err: HttpErrorResponse): string {
    const cuerpo = err.error;
    if (cuerpo && typeof cuerpo === 'object') {
      if (typeof cuerpo.error === 'string') return cuerpo.error;     // GlobalExceptionHandler → { error: "..." }
      if (typeof cuerpo.message === 'string') return cuerpo.message; // estructura por defecto de Spring
    }
    if (typeof cuerpo === 'string' && cuerpo.trim()) return cuerpo;  // cuerpo de texto plano
    if (err.status === 0) return 'No se pudo conectar con el servidor. Verifica tu conexión.';
    return 'No se pudo cargar el documento. Intenta nuevamente.';
  }

  getEstadoClase(estado: string): string {
    switch (estado) {
      case 'CANCELADO':
        return 'bg-green-50 text-green-700';
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
