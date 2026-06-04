import { Component, CUSTOM_ELEMENTS_SCHEMA, OnInit, signal } from '@angular/core';
import { ClienteService } from '../../services/cliente.service';
import { Cliente } from '../../models/cliente/cliente.model';
import { VendedorService } from '../../services/vendedor.service';
import { Vendedor } from '../../models/vendedor/vendedor.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FacturaClienteService } from '../../services/factura-cliente.service';
import { FacturasPorCobrar } from '../../models/factura-cliente/facturas-por-cobrar.model';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { NgCircleProgressModule } from 'ng-circle-progress';
import { LucideWalletMinimal, LucideUsersRound } from "@lucide/angular";
import { FacturaService } from '../../services/factura.service';
import { ResumenCartera } from '../../models/factura-cliente/resumen-cartera.model';
import { NgxEchartsDirective, provideEchartsCore } from 'ngx-echarts';
import * as echarts from 'echarts/core';
import { BarChart, LineChart } from 'echarts/charts';
import { GridComponent, TooltipComponent, LegendComponent, DatasetComponent } from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';
import type { EChartsCoreOption } from 'echarts/core';
import { ResumenCarteraSap } from '../../models/factura-cliente/resumen-cartera-sap.model';
import { ClienteDeudor } from '../../models/cliente/cliente-deudor.model';
echarts.use([BarChart, LineChart, GridComponent, CanvasRenderer, TooltipComponent, LegendComponent, DatasetComponent]);

@Component({
  selector: 'app-home',
  imports: [CommonModule, FormsModule, NgxChartsModule, LucideWalletMinimal, NgxChartsModule, NgCircleProgressModule, LucideUsersRound, NgxEchartsDirective],
  templateUrl: './home.component.html',
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  styleUrl: './home.component.css',
  providers: [provideEchartsCore({ echarts })]
})
export class HomeComponent implements OnInit {

  /* Componentes del HTML */
  selectConsultor: boolean = false;
  selectClientes: boolean = false;
  checkVistaGeneral: boolean = false;
  activeTab = signal(0);

  /* Arreglos */
  meses = [
    { id: 0, descripcion: 'Seleccionar mes' },
    { id: 1, descripcion: 'Enero' },
    { id: 2, descripcion: 'Febrero' },
    { id: 3, descripcion: 'Marzo' },
    { id: 4, descripcion: 'Abril' },
    { id: 5, descripcion: 'Mayo' },
    { id: 6, descripcion: 'Junio' },
    { id: 7, descripcion: 'Julio' },
    { id: 8, descripcion: 'Agosto' },
    { id: 9, descripcion: 'Setiembre' },
    { id: 10, descripcion: 'Octubre' },
    { id: 11, descripcion: 'Noviembre' },
    { id: 12, descripcion: 'Diciembre' },];
  tabs = ['Tablero', 'EECC'];
  graficaLineal: any[] = [];
  barraAcumuladoHorizontal: EChartsCoreOption = {};
  lineaMonosidad: EChartsCoreOption = {};
  barrasHorizontal: EChartsCoreOption = {};
  rangoSeleccionado: string = 'todos';
  worldPopulation: EChartsCoreOption = {};
  worldPopulationMerge: EChartsCoreOption = {};
  rangosWP = [
    { key: 'todos', label: 'Todos' },
    { key: 'noVencido', label: 'No vencido' },
    { key: 'vencido0a30', label: 'Entre 0 y 30' },
    { key: 'vencido31a45', label: 'Entre 31 y 45' },
    { key: 'vencido46a60', label: 'Entre 46 y 60' },
    { key: 'vencido61a90', label: 'Entre 61 y 90' },
    { key: 'vencido91a180', label: 'Entre 91 y 180' },
    { key: 'vencido180aMas', label: 'Mayor a 180' },
  ];
  todasCategorias = [
    { key: 'vencido180aMas', label: 'Mayor a 180' },
    { key: 'vencido91a180', label: 'Entre 91 y 180' },
    { key: 'vencido61a90', label: 'Entre 61 y 90' },
    { key: 'vencido46a60', label: 'Entre 46 y 60' },
    { key: 'vencido31a45', label: 'Entre 31 y 45' },
    { key: 'vencido0a30', label: 'Entre 0 y 30' },
    { key: 'noVencido', label: 'No vencido' },
  ];
  topDiezChart: EChartsCoreOption = {};

  /* Modelo */
  consultorSeleccionada: string = "-1";
  clienteSeleccionado: string = "-1";
  mesHistoricoSeleccionado: number = 0;
  clientes: Cliente[] = [];
  clientesDeudores: ClienteDeudor[] = [];
  vendedores: Vendedor[] = [];
  facturasPorCobrar: FacturasPorCobrar[] = [];
  facturasPorCobrarTopDiez: { name: string, value: number }[] = [];
  resumenCartera: ResumenCartera[] = [];
  resumenHistorico: ResumenCartera[] = [];
  mesActual: number = new Date().getMonth() + 1;
  // mesActual: number = 6;
  clientesExpandidos = new Set<string>();



  constructor(private clienteService: ClienteService, private vendedorService: VendedorService,
    private facturaClienteService: FacturaClienteService, private facturaService: FacturaService
  ) { }

  ngOnInit(): void {
    // this.getClientes();
    this.getVendedor();
    this.getFacturasPorCobrarTopDiez();
    this.cargarDataCompleta();
    this.getClientesDeudores();
  }

  /**VISTA GENERAL DE LOS REPORTES */

  cargarDataCompleta() {
    this.facturaService.getResumenCartera().subscribe(
      historico => {
        this.facturaClienteService.getResumenCartera().subscribe(
          sap => {
            const sapConvertido = this.convertirSapResumen(sap[0]);

            this.resumenCartera = [...historico, sapConvertido];

            this.graficaDeBarrasApiladas();
            this.graficaDeLineas();
            this.graficaWorldPopulation();
          }
        )
      }
    )
  }

  convertirSapResumen(sap: ResumenCarteraSap): ResumenCartera {
    return new ResumenCartera(
      new Date().getMonth() + 1,
      sap.noVencido,
      sap.vencido0a30,
      sap.vencido31a45,
      sap.vencido46a60,
      sap.vencido61a90,
      sap.vencido91a180,
      sap.vencido180aMas
    )
  }

  getResumenCarteraPorMes(periodo: number) {
    this.facturaService.getResumenCarteraPorPeriodo(periodo).subscribe(
      data => {
        this.resumenCartera = data
        this.graficaDeBarrasApiladas()
        this.graficaDeLineas()
        this.graficaWorldPopulation()
        // this.tasaMorosidadPorcentaje
        // this.montoTotalHistoricoVencido
        // this.montoTotalHistorico
      }
    )
  }

  changeMesCartera() {

    this.mesHistoricoSeleccionado = Number(this.mesHistoricoSeleccionado)

    if (this.mesHistoricoSeleccionado == 0 || this.mesHistoricoSeleccionado == this.mesActual) {
      this.cargarDataCompleta();
    } else if (this.mesHistoricoSeleccionado < this.mesActual) {
      this.getResumenCarteraPorMes(this.mesHistoricoSeleccionado);
    } else {
      this.resumenCartera = [];
      this.graficaDeBarrasApiladas()
      this.graficaDeLineas()
      this.graficaWorldPopulation()
    }
  }

  graficaDeBarrasApiladas() {
    const meses = ['', 'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Set', 'Oct', 'Nov', 'Dic'];

    const categorias = this.resumenCartera.map(rc => meses[rc.periodo]);

    const series = [
      { nombre: 'No vencido', color: '#3b82f6', key: 'noVencido' },
      { nombre: '0 - 30', color: '#22c55e', key: 'vencido0a30' },
      { nombre: '31 - 45', color: '#f59e0b', key: 'vencido31a45' },
      { nombre: '46 - 60', color: '#f97316', key: 'vencido46a60' },
      { nombre: '61 - 90', color: '#ef4444', key: 'vencido61a90' },
      { nombre: '91 - 180', color: '#a855f7', key: 'vencido91a180' },
      { nombre: '180 a mas', color: '#7c3aed', key: 'vencido180aMas' },
    ];

    this.barraAcumuladoHorizontal = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        formatter: (params: any) => {
          let result = `<b>${params[0].name}</b><br/>`;
          params.forEach((p: any) => {
            result += `${p.marker} ${p.seriesName}: ${Math.round((p.value / 1000)).toLocaleString('es')}<br/>`;
          });
          return result;
        }
      },
      legend: {
        bottom: 0,
        textStyle: { fontSize: 11 }
      },
      grid: {
        left: '3%',
        right: '8%',
        bottom: '22%',
        containLabel: true
      },
      xAxis: {
        type: 'value',
        axisLabel: {
          formatter: (val: number) => `$ ${(val / 1000).toFixed(0)}K`
        },
        show: false
      },
      yAxis: {
        type: 'category',
        inverse: true,
        data: categorias
      },
      series: series.map(s => ({
        name: s.nombre,
        type: 'bar',
        stack: 'total',
        itemStyle: { color: s.color },
        data: this.resumenCartera.map((rc: any) => rc[s.key])
      }))
    };
  }

  graficaDeLineas() {
    const meses = ['', 'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Set', 'Oct', 'Nov', 'Dic'];

    const categorias = this.resumenCartera.map(rc => meses[rc.periodo]);

    const porcentajes = this.resumenCartera.map(rc => {
      const total = rc.noVencido + rc.vencido0a30 + rc.vencido31a45 +
        rc.vencido46a60 + rc.vencido61a90 + rc.vencido91a180 +
        rc.vencido180aMas;

      if (rc.noVencido === 0) return 0;

      return Math.round((((total - rc.noVencido) / total) * 100));
    });

    this.lineaMonosidad = {
      tooltip: {
        trigger: 'axis',
        formatter: (params: any) => {
          const p = params[0];
          return `<b>${p.name}</b><br/>${p.marker} ${p.seriesName}: ${p.value}%`;
        }
      },
      grid: {
        left: '3%',
        right: '5%',
        bottom: '5%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: categorias,
        boundaryGap: false
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          formatter: (val: number) => `${val}%`
        }
      },
      series: [
        {
          name: 'Morosidad',
          type: 'line',
          smooth: true,
          data: porcentajes,
          itemStyle: { color: '#e91e8c' },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0, y: 0, x2: 0, y2: 1,
              colorStops: [
                { offset: 0, color: 'rgba(233, 30, 140, 0.3)' },
                { offset: 1, color: 'rgba(233, 30, 140, 0.02)' }
              ]
            }
          }
        }
      ]
    };
  }

  graficaWorldPopulation() {
    const meses = ['', 'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Set', 'Oct', 'Nov', 'Dic'];
    const categorias = this.resumenCartera.map(rc => meses[rc.periodo]);

    const series = [
      { nombre: 'No vencido', color: '#3b82f6', key: 'noVencido' },
      { nombre: '0 - 30', color: '#22c55e', key: 'vencido0a30' },
      { nombre: '31 - 45', color: '#f59e0b', key: 'vencido31a45' },
      { nombre: '46 - 60', color: '#f97316', key: 'vencido46a60' },
      { nombre: '61 - 90', color: '#ef4444', key: 'vencido61a90' },
      { nombre: '91 - 180', color: '#a855f7', key: 'vencido91a180' },
      { nombre: '180+', color: '#7c3aed', key: 'vencido180aMas' },
    ];

    this.worldPopulation = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        formatter: (params: any) => {
          let result = `<b>${params[0].name}</b><br/>`;
          params.forEach((p: any) => {
            if (p.value === 0) return;
            const valor = p.value.toLocaleString('es', { minimumFractionDigits: 2 });
            result += `${p.marker} ${p.seriesName}: $ ${valor}<br/>`;
          });
          return result;
        }
      },
      legend: {
        type: 'scroll',
        orient: 'vertical',
        right: 0,
        top: 'middle',
        textStyle: { fontSize: 11 }
      },
      grid: { left: '3%', right: '120px', bottom: '5%', containLabel: true },
      xAxis: {
        type: 'value',
        splitNumber: 3,
        axisLabel: {
          formatter: (val: number) =>
            `$ ${val.toLocaleString('es', { minimumFractionDigits: 0 })}`
        }
      },
      yAxis: {
        type: 'category',
        inverse: true,
        data: categorias
      },
      series: series.map(s => ({
        name: s.nombre,
        type: 'bar',
        itemStyle: { color: s.color },
        data: this.resumenCartera.map((rc: any) => rc[s.key])
      }))
    };
  }

  filtrarRangoWP(key: string) {
    this.rangoSeleccionado = key;

    const categoriasVisibles = key === 'todos'
      ? this.todasCategorias
      : this.todasCategorias.filter(c => c.key === key);

    this.worldPopulationMerge = {
      yAxis: {
        type: 'category',
        inverse: false,
        data: categoriasVisibles.map(c => c.label)
      },
      series: this.resumenCartera.map(rc => ({
        data: categoriasVisibles.map(c => (rc as any)[c.key])
      }))
    };
  }

  get tasaMorosidadPorcentaje(): number {
    const periodo = this.mesHistoricoSeleccionado == 0 ? this.mesActual : this.mesHistoricoSeleccionado;

    const datos = this.resumenCartera.filter(rc => rc.periodo == periodo);
    const totalVencido = datos.reduce((total, rc) => {
      return total + rc.vencido0a30 + rc.vencido31a45 + rc.vencido46a60 + rc.vencido61a90 + rc.vencido91a180 +
        rc.vencido180aMas
    }, 0);

    const totalGeneral = datos.reduce((total, rc) => {
      return total + rc.noVencido + rc.vencido0a30 + rc.vencido31a45 + rc.vencido46a60 + rc.vencido61a90 + rc.vencido91a180 +
        rc.vencido180aMas
    }, 0)

    if (totalGeneral === 0) return 0;

    return Math.round((totalVencido / totalGeneral) * 100)
  }

  get montoTotalHistoricoVencido(): number {
    const periodo = this.mesHistoricoSeleccionado == 0 ? this.mesActual : this.mesHistoricoSeleccionado;

    const totalVencido = this.resumenCartera.filter(rc => rc.periodo == periodo).reduce((total, rc) => {
      return total + rc.vencido0a30 + rc.vencido31a45 + rc.vencido46a60 + rc.vencido61a90 + rc.vencido91a180 +
        rc.vencido180aMas
    }, 0)
    return totalVencido / 1000000;
  }

  get montoTotalHistorico(): number {

    const periodo = this.mesHistoricoSeleccionado == 0 ? this.mesActual : this.mesHistoricoSeleccionado

    const totalHistorico = this.resumenCartera.filter(rc => rc.periodo == periodo).reduce((total, rc) => {
      return total + rc.vencido0a30 + rc.vencido31a45 + rc.vencido46a60 + rc.vencido61a90 + rc.vencido91a180 +
        rc.vencido180aMas + rc.noVencido
    }, 0)
    return totalHistorico / 1000000;
  }

  get mesesDisponibles() {
    return this.meses.filter(m => m.id <= this.mesActual || m.id == 0)
  }

  get mesesTabla() {

    const hastaElMes = this.mesHistoricoSeleccionado === 0
      ? this.mesActual
      : this.mesHistoricoSeleccionado;

    return this.meses.filter(m => m.id >= 1 && m.id <= hastaElMes);
  }

  getValorPorPeriodo(periodo: number, campo: string) {
    const resumen = this.resumenCartera.find(rc => rc.periodo === periodo);
    if (!resumen) return 0;
    return (resumen as any)[campo] || 0;
  }

  graficaTopDiez() {
    const nombres = this.facturasPorCobrarTopDiez.map(f => f.name);
    const valores = this.facturasPorCobrarTopDiez.map(f => f.value);

    this.topDiezChart = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        formatter: (params: any) => {
          const p = params[0];
          const valor = p.value.toLocaleString('es', { minimumFractionDigits: 2 });
          return `<b>${p.name}</b><br/>${p.marker} Saldo: $ ${valor}`;
        }
      },
      grid: { left: '3%', right: '8%', bottom: '3%', top: '3%', containLabel: true },
      xAxis: {
        type: 'value',
        axisLabel: {
          formatter: (val: number) =>
            `$ ${(val / 1000).toFixed(0)}K`
        }
      },
      yAxis: {
        type: 'category',
        inverse: true,
        data: nombres,
        axisLabel: {
          fontSize: 10,
          width: 150,
          overflow: 'truncate'
        }
      },
      series: [{
        type: 'bar',
        data: valores,
        itemStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: '#1e40af' },
              { offset: 1, color: '#3b82f6' }
            ]
          },
          borderRadius: [0, 4, 4, 0]
        }
      }]
    };
  }

  //FACTURAS POR COBRAR: TOP DE 10 PRIMEROS CON MONTOS ALTOS
  getFacturasPorCobrarTopDiez() {
    this.facturaClienteService.getFacturasPorCobrarTopDiez().subscribe(
      data => {
        this.facturasPorCobrarTopDiez = [];
        for (var f of data) {
          this.facturasPorCobrarTopDiez.push({ name: f.nombre, value: f.saldo });
        }
        this.graficaTopDiez();
      }
    )
  }

  /** ========================================================================================== */

  /**DETALLE DE REPORTES POR COBRAR POR USUARIO Y/O VENDEDOR */

  //FACTURAS POR COBRAR: POR CLIENTE
  getFacturasPorCobrarCliente(ruc: string) {
    return this.facturaClienteService.getFacturasPorCobrarCliente(ruc).subscribe(
      data => {
        this.facturasPorCobrar = data;
      }
    )
  }

  //FACTURAS POR COBRAR: SUMA DE TODOS LOS MONTOS VENCIDOS
  getRecibosVencidosTotal(): number {
    return this.facturasPorCobrar.filter(
      f => {
        const dias = this.calDiasVencimiento(f.vencimiento);
        return dias >= 0;
      }).reduce((total, f) => total + f.saldo, 0)
  }

  //FACTURAS POR COBRAR: SUMA DE TODOS LOS MONTOS VENCIDOS POR RANGO DE DIAS
  getRecibosVencidosPorMonto(firts: number, last: number): number {
    return this.facturasPorCobrar.filter(
      f => {
        const dias = this.calDiasVencimiento(f.vencimiento);
        return dias >= firts && dias <= last;
      }).reduce((total, f) => total + f.saldo, 0)
  }

  //FACTURAS POR COBRAR: SUMA DE TODO LS MONTOS NO VENCIDOS
  getRecibosNoVencidosPorMonto(): number {
    return this.facturasPorCobrar.filter(
      f => {
        const dias = this.calDiasVencimiento(f.vencimiento);
        return dias < 0;
      }
    ).reduce((total, f) => total + f.saldo, 0)
  }

  //FACTURAS POR COBRAR: SUMA DE TODOS LOS MONTOS DE IMPORTE
  get totalImporte(): number {
    return this.facturasPorCobrar.reduce(
      (acumulador, factura) => acumulador + factura.importe, 0
    );
  }

  //FACTURAS POR COBRAR: SUMA DE TODOS LOS MONTOS DE SALDO
  get totalSaldo(): number {
    return this.facturasPorCobrar.reduce(
      (acumulador, factura) => acumulador + factura.saldo, 0
    );
  }

  calDiasVencimiento(fechaVencimiento: string): number {
    // Creamos la fecha CORRECTAMENTE
    const vencimiento = new Date(fechaVencimiento);
    const hoy = new Date();

    const diferencia = hoy.getTime() - vencimiento.getTime();
    const dias = Math.ceil(diferencia / (1000 * 60 * 60 * 24));

    return dias;
  }

  getPorcentajeVencidoCalc(): number {
    if (this.totalSaldo === 0) return 0;

    const vencido = this.getRecibosVencidosTotal();
    const porcentaje = (vencido / this.totalSaldo) * 100;

    return Math.round(porcentaje);
  }

  getFacturasPorCobrarVendedor(slpCode: number) {
    return this.facturaClienteService.getFacturasPorCobrarVendedor(slpCode).subscribe(
      data => this.facturasPorCobrar = data
    )
  }

  /** Seccion de clientes */
  getClientes() {
    return this.clienteService.getClientes().subscribe(
      data => this.clientes = data
    )
  }

  getClientesDeudores() {
    this.clienteService.getDeudores().subscribe(
      data => this.clientesDeudores = data
    )
  }

  getClientesPorVendedor(idVendedor: number) {
    return this.clienteService.getClientesPorVendedor(idVendedor).subscribe(
      data => this.clientes = data
    )
  }

  getClientesDeudoresPorVendedor(idVendedor: number) {
    this.clienteService.getDeudoresPorVendedor(idVendedor).subscribe(
      data => this.clientesDeudores = data
    )
  }

  /** Seccion de Vendedor */
  getVendedor() {
    return this.vendedorService.getVendedor().subscribe(
      data => this.vendedores = data
    )
  }

  changeConsultor(value: string) {
    var convertNumber = Number(value);
    if (value != "-1") {
      // this.getClientesPorVendedor(convertNumber);
      this.getClientesDeudoresPorVendedor(convertNumber);
      this.getFacturasPorCobrarVendedor(convertNumber);
      this.clienteSeleccionado = '-1';

    } else {
      // this.getClientes()
      this.getClientesDeudores()
      this.facturasPorCobrar = [];
    }
  }

  changeCliente(ruc: string) {
    if (ruc != "-1") {
      this.getFacturasPorCobrarCliente(ruc);
    } else {
      this.facturasPorCobrar = [];
    }
  }

  get facturasPorCobrarAgrupadas(): { nombre: string, totalImporte: number, totalSaldo: number, facturas: FacturasPorCobrar[] }[] {
    const grupos = new Map<string, FacturasPorCobrar[]>();

    this.facturasPorCobrar.forEach(f => {
      if (!grupos.has(f.nombre)) grupos.set(f.nombre, []);
      grupos.get(f.nombre)!.push(f);
    });

    return Array.from(grupos.entries()).map(([nombre, facturas]) => ({
      nombre,
      totalImporte: facturas.reduce((acc, f) => acc + f.importe, 0),
      totalSaldo: facturas.reduce((acc, f) => acc + f.saldo, 0),
      facturas
    }));
  }

  toggleCliente(nombre: string) {
    if (this.clientesExpandidos.has(nombre)) {
      this.clientesExpandidos.delete(nombre);
    } else {
      this.clientesExpandidos.add(nombre);
    }
  }

  /** Metodo de los componentes en HTML */
  changeStatusSelect() {
    this.checkVistaGeneral = !this.checkVistaGeneral
    this.consultorSeleccionada = '-1';
    this.clienteSeleccionado = '-1';
  }


}
