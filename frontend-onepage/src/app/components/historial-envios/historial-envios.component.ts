import { Component, OnInit, signal } from '@angular/core';
import { LotePedido } from '../../models/borrador/lote-pedido.model';
import { BorradoresService } from '../../services/borradores.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-historial-envios',
  imports: [CommonModule, FormsModule],
  templateUrl: './historial-envios.component.html',
  styleUrl: './historial-envios.component.css',
})
export class HistorialEnviosComponent implements OnInit {

  historial: LotePedido[] = [];
  filtroTexto: string = '';
  itemExpandido = signal<string | null>(null);

  // Paginación
  paginaActual = signal(1);
  itemsPorPagina = 10;

  constructor(private borradoresService: BorradoresService) { }

  ngOnInit(): void {
    this.cargarHistorial();
  }

  cargarHistorial() {
    this.borradoresService.listaPedidosEnviados().subscribe(
      data => {
        console.log('Data recibida:', data);
        this.historial = data;
      }
    );
  }

  get historialFiltrado(): LotePedido[] {
    if (this.filtroTexto === '') return this.historial;

    const texto = this.filtroTexto.toLowerCase();
    return this.historial.filter(h =>
      h.nombre.toLowerCase().includes(texto) ||
      h.codCliente.toLowerCase().includes(texto)
    );
  }

  get totalPaginas(): number {
    return Math.ceil(this.historialFiltrado.length / this.itemsPorPagina);
  }

  get historialPaginado(): LotePedido[] {
    const inicio = (this.paginaActual() - 1) * this.itemsPorPagina;
    const fin = inicio + this.itemsPorPagina;
    return this.historialFiltrado.slice(inicio, fin);
  }

  get rangoMostrado(): { desde: number, hasta: number } {
    if (this.historialFiltrado.length === 0) return { desde: 0, hasta: 0 };
    const desde = (this.paginaActual() - 1) * this.itemsPorPagina + 1;
    const hasta = Math.min(this.paginaActual() * this.itemsPorPagina, this.historialFiltrado.length);
    return { desde, hasta };
  }

  cambiarPagina(pagina: number) {
    if (pagina >= 1 && pagina <= this.totalPaginas) {
      this.paginaActual.set(pagina);
      this.itemExpandido.set(null);
    }
  }

  onFiltroChange() {
    this.paginaActual.set(1);
    this.itemExpandido.set(null);
  }

  toggleItem(codCliente: string) {
    if (this.itemExpandido() === codCliente) {
      this.itemExpandido.set(null);
    } else {
      this.itemExpandido.set(codCliente);
    }
  }

  formatHora(docTime: number): string {
    if (!docTime) return '—';
    const horas = Math.floor(docTime / 100);
    const minutos = docTime % 100;
    return `${horas.toString().padStart(2, '0')}:${minutos.toString().padStart(2, '0')}`;
  }

  get paginasArray(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, i) => i + 1);
  }
}
