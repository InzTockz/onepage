export class Factura {
    ruc: string;
    nombre: string;
    documento: number;
    comprobante: string;
    emision: string;
    vencimiento: string;
    moneda: string;
    importe: number;
    saldo: number;
    vendedor: string;
    fechaRegistro: string;
    periodo: number;
    lc: number;

    constructor(ruc: string, nombre: string, documento: number, comprobante: string, emision: string, vencimiento: string,
        moneda: string, importe: number, saldo: number, vendedor: string, fechaRegistro: string, periodo: number, lc: number
    ) {
        this.ruc = ruc;
        this.nombre = nombre;
        this.documento = documento;
        this.comprobante = comprobante;
        this.emision = emision;
        this.vencimiento = vencimiento;
        this.moneda = moneda;
        this.importe = importe;
        this.saldo = saldo;
        this.vendedor = vendedor;
        this.fechaRegistro = fechaRegistro;
        this.periodo = periodo;
        this.lc = lc;
    }
}
