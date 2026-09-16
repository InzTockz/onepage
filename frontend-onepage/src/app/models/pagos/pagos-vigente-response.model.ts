export class PagosVigenteResponse {
    idPagoVigente: number;
    nroUnico: string;
    nroFactura: string;
    aceptante: string;
    fechaIngreso: string;
    fechaVencimiento: string;
    moneda: string;
    importe: number;
    estadoOriginal: string;
    estado: string;
    archivoOrigen: string;
    fechaRegistro: string;
    idBanco: number;
    estadoRegistro: boolean;

    constructor(idPagoVigente: number, nroUnico: string, nroFactura: string, aceptante: string, fechaIngreso: string,
        fechaVencimiento: string, moneda: string, importe: number, estadoOriginal: string, estado: string, archivoOrigen: string,
        fechaRegistro: string, idBanco: number, estadoRegistro: boolean) {
        this.idPagoVigente = idPagoVigente;
        this.nroUnico = nroUnico;
        this.nroFactura = nroFactura;
        this.aceptante = aceptante;
        this.fechaIngreso = fechaIngreso;
        this.fechaVencimiento = fechaVencimiento;
        this.moneda = moneda;
        this.importe = importe;
        this.estadoOriginal = estadoOriginal;
        this.estado = estado;
        this.archivoOrigen = archivoOrigen;
        this.fechaRegistro = fechaRegistro;
        this.idBanco = idBanco;
        this.estadoRegistro = estadoRegistro
    }
}
