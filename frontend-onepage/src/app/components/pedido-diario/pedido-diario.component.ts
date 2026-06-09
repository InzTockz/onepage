import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { PedidosDiarios } from '../../models/pedidos-diarios.model';
import { BorradoresService } from '../../services/borradores.service';
import { CommonModule } from '@angular/common';
import { LotePedidoService } from '../../services/lote-pedido.service';
import { LotePedido } from '../../models/borrador/lote-pedido.model';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-pedido-diario',
  imports: [CommonModule, FormsModule],
  templateUrl: './pedido-diario.component.html',
  styleUrl: './pedido-diario.component.css',
})
export class PedidoDiarioComponent implements OnInit, AfterViewInit, OnDestroy {

  /*LOGICA PARA LA TABLA*/
  @ViewChild('tableContainer') tableContainer!: ElementRef;
  /* */

  enCargaActualizarPedidos: boolean = false;
  enCarga: boolean = false;
  lotePedidos: LotePedido[] = [];
  lotePedido!: LotePedido;
  page = 1;
  pageSize = 10;

  constructor(private borradoresService: BorradoresService, private lotePedidoService: LotePedidoService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.showPedidosDiarios();
    this.paginatedData();
  }

  ngAfterViewInit(): void {
    const el = this.tableContainer.nativeElement;

    let isDown = false;
    let startX = 0;
    let scrollLeft = 0;

    el.onmousedown = (e: MouseEvent) => {
      isDown = true;
      startX = e.pageX - el.offsetLeft;
      scrollLeft = el.scrollLeft;
    };

    el.onmouseleave = () => {
      isDown = false;
    };

    el.onmouseup = () => {
      isDown = false;
    };

    el.onmousemove = (e: MouseEvent) => {
      if (!isDown) return;
      e.preventDefault();
      const x = e.pageX - el.offsetLeft;
      const walk = (x - startX) * 1.5;
      el.scrollLeft = scrollLeft - walk;
    };
  }

  ngOnDestroy(): void {
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
      docEntry: p.docentry,
      codCliente: p.codCliente,
      comentario: p.comentario || ''
    }))
    this.borradoresService.generarLote().subscribe({
      next: () => {
        console.log('Lote generado con exito')
        this.showPedidosDiarios();
        this.enCarga = false;
      },
      error: (err) => {
        console.error('Error generando lote: ', err);
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
