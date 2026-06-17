export class PagosResponse {
  idPago: number;
  idBanco: number;
  nroTransaccion: string;
  nroFactura: string;
  aceptante: string;
  fechaOperacion: string;
  fechaVencimiento: string;
  fechaIngresoBanco: string;
  moneda: string;
  importeIngreso: number;
  importe: number;
  interes: number;
  comision: number;
  gastos: number;
  estado: string;
  estadoOriginal: string;
  estadoOperativo: string;
  archivoOrigen: string;
  fechaCarga: string;

  constructor(
    idPago: number,
    idBanco: number,
    nroTransaccion: string,
    nroFactura: string,
    aceptante: string,
    fechaOperacion: string,
    fechaVencimiento: string,
    fechaIngresoBanco: string,
    moneda: string,
    importeIngreso: number,
    importe: number,
    interes: number,
    comision: number,
    gastos: number,
    estado: string,
    estadoOriginal: string,
    estadoOperativo: string,
    archivoOrigen: string,
    fechaCarga: string,
  ) {
    this.idPago = idPago;
    this.idBanco = idBanco;
    this.nroTransaccion = nroTransaccion;
    this.nroFactura = nroFactura;
    this.aceptante = aceptante;
    this.fechaOperacion = fechaOperacion;
    this.fechaVencimiento = fechaVencimiento;
    this.fechaIngresoBanco = fechaIngresoBanco;
    this.moneda = moneda;
    this.importeIngreso = importeIngreso;
    this.importe = importe;
    this.interes = interes;
    this.comision = comision;
    this.gastos = gastos;
    this.estado = estado;
    this.estadoOriginal = estadoOriginal;
    this.estadoOperativo = estadoOperativo;
    this.archivoOrigen = archivoOrigen;
    this.fechaCarga = fechaCarga;
  }
}
