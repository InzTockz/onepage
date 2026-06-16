import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-facturas-canceladas',
  imports: [],
  templateUrl: './facturas-canceladas.component.html',
  styleUrl: './facturas-canceladas.component.css',
})
export class FacturasCanceladasComponent {
  bancos = [
    { id: 1, codigo: 'BBVA', nombre: 'BBVA Perú', color: 'blue' },
    { id: 2, codigo: 'BCP', nombre: 'Banco de Crédito del Perú', color: 'orange' },
    { id: 3, codigo: 'SCOTIA', nombre: 'Scotiabank Perú', color: 'red' },
    { id: 4, codigo: 'IBK', nombre: 'Interbank', color: 'green' },
  ];

  bancoActivo = signal<number>(1);
  pagos: Pago[] = [];
  filtroTexto = '';

  // Modal de carga
  modalAbierto = signal(false);
  bancoSeleccionadoCarga = '';
  archivoSeleccionado: File | null = null;
  cargandoArchivo = signal(false);

  ngOnInit(): void {
    this.cargarPagos();
  }

  cargarPagos() {
    // Aquí conectas tu servicio real
    this.pagos = [];
  }

  get pagosFiltrados(): Pago[] {
    let resultado = this.pagos.filter(p => p.idBanco === this.bancoActivo());

    if (this.filtroTexto !== '') {
      const texto = this.filtroTexto.toLowerCase();
      resultado = resultado.filter(p =>
        p.aceptante.toLowerCase().includes(texto) ||
        p.nroFactura.toLowerCase().includes(texto) ||
        p.nroTransaccion.toLowerCase().includes(texto)
      );
    }

    return resultado;
  }

  get totalImporte(): number {
    return this.pagosFiltrados.reduce((acc, p) => acc + p.importe, 0);
  }

  get bancoSeleccionadoData() {
    return this.bancos.find(b => b.id === this.bancoActivo())!;
  }

  cambiarBanco(id: number) {
    this.bancoActivo.set(id);
    this.filtroTexto = '';
  }

  abrirModal() {
    this.modalAbierto.set(true);
    this.bancoSeleccionadoCarga = '';
    this.archivoSeleccionado = null;
  }

  cerrarModal() {
    this.modalAbierto.set(false);
  }

  onArchivoSeleccionado(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.archivoSeleccionado = input.files[0];
    }
  }

  subirArchivo() {
    if (!this.bancoSeleccionadoCarga || !this.archivoSeleccionado) return;

    this.cargandoArchivo.set(true);

    // Aquí conectas tu servicio real de carga
    setTimeout(() => {
      this.cargandoArchivo.set(false);
      this.cerrarModal();
      this.cargarPagos();
    }, 2000);
  }

  getEstadoClase(estado: string): string {
    switch (estado) {
      case 'CANCELADO': return 'bg-green-50 text-green-700';
      case 'AMORTIZADO': return 'bg-blue-50 text-blue-700';
      case 'PROTESTADO': return 'bg-red-50 text-red-700';
      case 'DEVUELTO': return 'bg-amber-50 text-amber-700';
      default: return 'bg-gray-50 text-gray-600';
    }
  }

  getColorBanco(codigo: string): string {
    switch (codigo) {
      case 'BBVA': return 'text-blue-700';
      case 'BCP': return 'text-orange-600';
      case 'SCOTIA': return 'text-red-600';
      case 'IBK': return 'text-green-700';
      default: return 'text-gray-700';
    }
  }
}
