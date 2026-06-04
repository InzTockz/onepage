import { Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LotePedidoService } from '../../services/lote-pedido.service';
import { LotePedido } from '../../models/borrador/lote-pedido.model';
import html2canvas from 'html2canvas-pro';

@Component({
  selector: 'app-lote-pedidos',
  imports: [CommonModule],
  templateUrl: './lote-pedidos.component.html',
  styleUrl: './lote-pedidos.component.css',
})
export class LotePedidosComponent implements OnInit {


  fechaHoy: string = new Date().toLocaleDateString('es-PE');
  lotePedidos: LotePedido[] = [];
  enCargaEnvio: boolean = false;
  @ViewChild('gridCards') gridCards!: ElementRef;
  @ViewChildren('cardItem') cardItems!: QueryList<ElementRef>;

  constructor(private lotePedidosService: LotePedidoService) { }

  ngOnInit(): void {
    this.showPedidosDiarios();
  }

  showPedidosDiarios() {
    return this.lotePedidosService.getLotePedidos().subscribe(
      data => this.lotePedidos = data
    )
  };

  // generarEnvioPedidos() {

  //   //Si no hay pedidos, no hace nada
  //   if (this.lotePedidos.length === 0) return;

  //   this.enCargaEnvio = true;

  //   //PASO 1: Fotografiar los cards antes de que desparezcan
  //   html2canvas(this.gridCards.nativeElement, {
  //     backgroundColor: '#ffffff',
  //     scale: 2
  //   }).then(canvas => {

  //     //PASO 2: Descagar la imagen
  //     const imagen = canvas.toDataURL('image/jpeg', 0.95);
  //     const link = document.createElement('a');
  //     link.download = `pedidos-${this.fechaHoy}.jpg`;
  //     link.href = imagen;
  //     link.click();

  //     //PASO 3: Llamar a la API para cambiar estado
  //     this.lotePedidosService.getGenerarEnvio().subscribe({
  //       next: () => {
  //         console.log("Enviado generado y descargado");
  //         //PASO 4: Recargar la lista (ahora vacia)
  //         this.showPedidosDiarios();
  //         this.enCargaEnvio = false;
  //       },
  //       error: (err) => {
  //         console.error("Error al enviar: ", err);
  //         this.enCargaEnvio = false;
  //       }
  //     })
  //   })
  // }

  generarEnvioPedidos() {

    //Si no hay pedidos, no hace nada
    if (this.lotePedidos.length === 0) return;

    this.enCargaEnvio = true;

    //Descarga cada card como imagen individual
    this.descargarTodosInvidual().then(() => {

      //Despues de descargar todas, llame a la API
      this.lotePedidosService.getGenerarEnvio().subscribe({
        next: () => {
          console.log("Envio generado y descargado");
          this.showPedidosDiarios();
          this.enCargaEnvio = false;
        },
        error: (err) => {
          console.log("Error al enviar: ", err);
          this.enCargaEnvio = false;
        }
      })
    })
  }

  async descargarTodosInvidual() {
    const cards = this.cardItems.toArray();

    for (let i = 0; i < cards.length; i++) {
      const card = cards[i];
      const pedido = this.lotePedidos[i];

      //Fotografia el card
      const canvas = await html2canvas(card.nativeElement, {
        backgroundColor: '#ffffff',
        scale: 2
      });

      //Descargar la imagen
      const imagen = canvas.toDataURL('image/jpeg', 0.95);
      const link = document.createElement('a');
      link.download = `${pedido.codCliente}-${pedido.nombre}.jpg`;
      link.href = imagen;
      link.click();

      //Espera 300ms entra descargas para que el navegador no se bloquee
      await new Promise(resolve => setTimeout(resolve, 300));
    }
  }
}
