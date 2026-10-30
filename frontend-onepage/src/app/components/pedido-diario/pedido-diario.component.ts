import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { BorradoresService } from '../../services/borradores.service';
import { CommonModule } from '@angular/common';
import { LotePedidoService } from '../../services/lote-pedido.service';
import { LotePedido } from '../../models/borrador/lote-pedido.model';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { ColumnResizeDirective } from '../../directives/column-resize.directive';

interface ColumnaPedido {
  id: keyof LotePedido;
  titulo: string;
  anchoInicial: number;
  minimo: number;
  maximo: number;
  derecha?: boolean;
  fija?: boolean;
}

const COLUMNAS_PEDIDO: readonly ColumnaPedido[] = [
  { id: 'codCliente', titulo: 'RUC', anchoInicial: 120, minimo: 120, maximo: 360, fija: true },
  { id: 'nombre', titulo: 'Nombre', anchoInicial: 250, minimo: 160, maximo: 800, fija: true },
  { id: 'condicionPago', titulo: 'C. Pago', anchoInicial: 140, minimo: 100, maximo: 600 },
  { id: 'montoTotalPedido', titulo: 'Total Pedido', anchoInicial: 140, minimo: 110, maximo: 600, derecha: true },
  { id: 'lineaCredito', titulo: 'L. Crédito', anchoInicial: 140, minimo: 110, maximo: 600, derecha: true },
  { id: 'fechaUltimaFacturaPagada', titulo: 'Últ. Pago', anchoInicial: 130, minimo: 115, maximo: 400 },
  { id: 'montoVencido', titulo: 'Vencido', anchoInicial: 140, minimo: 110, maximo: 600, derecha: true },
  { id: 'montoTotalDeuda', titulo: 'Deuda', anchoInicial: 140, minimo: 110, maximo: 600, derecha: true },
  { id: 'lineaCreditoUtilizada', titulo: 'Utilización de LC', anchoInicial: 165, minimo: 130, maximo: 500, derecha: true },
  { id: 'mora', titulo: 'Mora', anchoInicial: 95, minimo: 80, maximo: 400, derecha: true },
  { id: 'nroFacturasVencidas', titulo: 'F. Vencidas', anchoInicial: 120, minimo: 100, maximo: 400, derecha: true },
  { id: 'montoPorCobrar', titulo: 'Por Vencer', anchoInicial: 140, minimo: 110, maximo: 600, derecha: true },
  { id: 'fechaFacturaVencidaMasAntigua', titulo: 'Más Vencida', anchoInicial: 135, minimo: 115, maximo: 400 },
  { id: 'docTime', titulo: 'Hora Ingreso', anchoInicial: 130, minimo: 110, maximo: 400 },
  { id: 'fechaCreacionPedido', titulo: 'Fecha Ingreso', anchoInicial: 145, minimo: 115, maximo: 400 },
  { id: 'estadoBorrador', titulo: 'Estado', anchoInicial: 140, minimo: 90, maximo: 400 },
  { id: 'comentario', titulo: 'Comentario', anchoInicial: 260, minimo: 180, maximo: 1000 },
];

@Component({
  selector: 'app-pedido-diario',
  imports: [CommonModule, FormsModule, ColumnResizeDirective],
  templateUrl: './pedido-diario.component.html',
  styleUrl: './pedido-diario.component.css',
})
export class PedidoDiarioComponent implements OnInit, OnDestroy {

  @ViewChild('tableContainer') tableContainer!: ElementRef<HTMLDivElement>;
  readonly anchoSeleccion = 40;
  readonly columnas = COLUMNAS_PEDIDO.map(columna => ({ ...columna, ancho: columna.anchoInicial }));
  private readonly claveAnchos = 'pedido-diario.column-widths.v1';
  arrastrandoTabla = false;
  private arrastre: { pointerId: number; inicioX: number; scrollInicial: number } | null = null;

  enCargaActualizarPedidos: boolean = false;
  enCarga: boolean = false;
  seleccionados = new Set<number>();
  lotePedidos: LotePedido[] = [];
  lotePedido!: LotePedido;
  page = 1;
  pageSize = 20;

  constructor(private borradoresService: BorradoresService, private lotePedidoService: LotePedidoService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.cargarAnchosColumnas();
    this.showPedidosDiarios();
    this.paginatedData();
  }

  get anchoTabla(): number {
    return this.anchoSeleccion + this.columnas.reduce((total, columna) => total + columna.ancho, 0);
  }

  get posicionNombre(): number {
    return this.anchoSeleccion + this.columnas[0].ancho;
  }

  cambiarAnchoColumna(columna: ColumnaPedido & { ancho: number }, ancho: number): void {
    if (Number.isFinite(ancho)) {
      columna.ancho = Math.round(Math.max(columna.minimo, Math.min(columna.maximo, ancho)));
    }
  }

  private cargarAnchosColumnas(): void {
    try {
      const guardados: unknown = JSON.parse(localStorage.getItem(this.claveAnchos) ?? 'null');
      if (!guardados || typeof guardados !== 'object' || Array.isArray(guardados)) return;

      for (const columna of this.columnas) {
        const ancho = (guardados as Record<string, unknown>)[columna.id];
        if (typeof ancho === 'number') this.cambiarAnchoColumna(columna, ancho);
      }
    } catch {
      // Los valores iniciales siguen disponibles si el navegador bloquea el almacenamiento.
    }
  }

  guardarAnchosColumnas(): void {
    try {
      const anchos = Object.fromEntries(this.columnas.map(columna => [columna.id, columna.ancho]));
      localStorage.setItem(this.claveAnchos, JSON.stringify(anchos));
    } catch {
      // El ajuste de columnas también funciona sin persistencia.
    }
  }

  restablecerColumnas(): void {
    for (const columna of this.columnas) columna.ancho = columna.anchoInicial;
    try {
      localStorage.removeItem(this.claveAnchos);
    } catch {
      // Restablecer la vista no depende del acceso al almacenamiento.
    }
  }

  iniciarDesplazamiento(event: PointerEvent): void {
    if (event.pointerType !== 'mouse' || event.button !== 0 || !event.isPrimary || this.arrastre) return;
    const elemento = event.target;
    if (!(elemento instanceof Element) || elemento.closest(
      'input, textarea, select, button, a, label, [contenteditable], .column-resize-handle, .select-text'
    )) return;

    this.arrastre = {
      pointerId: event.pointerId,
      inicioX: event.clientX,
      scrollInicial: this.tableContainer.nativeElement.scrollLeft,
    };
  }

  desplazarTabla(event: PointerEvent): void {
    if (!this.arrastre || event.pointerId !== this.arrastre.pointerId) return;
    if (event.buttons === 0) {
      this.finalizarDesplazamiento(event);
      return;
    }

    const distancia = event.clientX - this.arrastre.inicioX;
    const contenedor = this.tableContainer.nativeElement;
    if (!this.arrastrandoTabla && Math.abs(distancia) > 5) {
      contenedor.setPointerCapture(event.pointerId);
      this.arrastrandoTabla = true;
    }
    if (this.arrastrandoTabla) {
      event.preventDefault();
      contenedor.scrollLeft = this.arrastre.scrollInicial - distancia * 1.5;
    }
  }

  salirDelContenedor(event: PointerEvent): void {
    if (!this.arrastrandoTabla) this.finalizarDesplazamiento(event);
  }

  finalizarDesplazamiento(event?: PointerEvent): void {
    if (event && event.pointerId !== this.arrastre?.pointerId) return;
    const pointerId = this.arrastre?.pointerId;
    this.arrastre = null;
    this.arrastrandoTabla = false;
    const contenedor = this.tableContainer?.nativeElement;
    if (pointerId !== undefined && contenedor?.hasPointerCapture(pointerId)) {
      contenedor.releasePointerCapture(pointerId);
    }
  }

  ngOnDestroy(): void {
    this.finalizarDesplazamiento();
  }

  actualizarPedidos() {
    this.enCargaActualizarPedidos = true;
    this.borradoresService.registrarPedidos().subscribe({
      next: () => {
        this.showPedidosDiarios();
        this.enCargaActualizarPedidos = false;
      },
      error: (err) => {
        console.error('Error sincronizando: ', err);
        this.enCargaActualizarPedidos = false;
      }
    })
  }

  postLotePedido() {
    this.enCarga = true;
    const comentarios = this.lotePedidos.map(p => ({
      docEntry: p.docEntry,
      codCliente: p.codCliente,
      comentario: p.comentario || ''
    }))
    this.borradoresService.generarLote(this.pedidosSeleccionados).subscribe({
      next: () => {
        this.toastr.success('Lote generado correctamente', 'Éxito');
        this.showPedidosDiarios();
        this.seleccionados.clear()
        this.enCarga = false;
      },
      error: (err) => {
        this.toastr.error('Error al generar lote', 'Error');
        this.enCarga = false;
      }
    })
  }

  showPedidosDiarios() {
    return this.borradoresService.getPedidosDiarios().subscribe(
      data => this.lotePedidos = data
    )
  }

  guardarComentario(pd: LotePedido) {
    this.borradoresService.agregarComentario(pd.idLotePedidos, pd).subscribe({
      next: () => {
        console.log(`Comentario guardado para ${pd.codCliente}`);
        this.toastr.success('Comentario guardado', 'Pedido');
      },
      error: (err) => {
        console.error(err)
      }
    })
  }

  agregarComentario(idBorrador: number) {
    this.borradoresService.agregarComentario(idBorrador, this.lotePedido).subscribe(
      () => {
        console.log(this.lotePedido.comentario);
        console.log('Comentario registrado')
      }
    )
  }

  get lineaCreditoUtilizada() {
    return 0
  }

  get todosSeleccionados(): boolean {
    const datos = this.paginatedData();
    return datos.length > 0 && datos.every(pd => this.seleccionados.has(pd.idLotePedidos));
  }

  get algunosSeleccionados(): boolean {
    const datos = this.paginatedData();
    return datos.some(pd => this.seleccionados.has(pd.idLotePedidos)) && !this.todosSeleccionados;
  }

  toggleTodos() {
    if (this.todosSeleccionados) {
      this.paginatedData().forEach(pd => this.seleccionados.delete(pd.idLotePedidos));
    } else {
      this.paginatedData().forEach(pd => this.seleccionados.add(pd.idLotePedidos));
    }
  }

  toggleUno(id: number) {
    if (this.seleccionados.has(id)) {
      this.seleccionados.delete(id);
    } else {
      this.seleccionados.add(id);
    }
  }

  get pedidosSeleccionados(): LotePedido[] {
    return this.lotePedidos.filter(pd => this.seleccionados.has(pd.idLotePedidos));
  }

  formatoHora(updateTS: number | null): string {
    if (updateTS == null || isNaN(updateTS) || updateTS < 0) return '--:--';

    const horas = Math.floor(updateTS / 10000);
    const minutos = Math.floor((updateTS % 10000) / 100);

    if (horas > 23 || minutos > 59) return '--:--';   // descarta valores basura

    return `${horas.toString().padStart(2, '0')}:${minutos.toString().padStart(2, '0')}`;
  }

  paginatedData() {
    const start = (this.page - 1) * this.pageSize;
    const end = start + this.pageSize;
    return this.lotePedidos.slice(start, end);
  }

  totalPages() {
    return Math.ceil(this.lotePedidos.length / this.pageSize);
  }

  nextPage() {
    if (this.page < this.totalPages()) this.page++;
  }

  prevPage() {
    if (this.page > 1) this.page--;
  }
}
