export class LotePedido {
    idLotePedido: number;
    codCliente: string;
    nombre: string;
    fechaCreacion: string;
    fechaRecorte: string;
    montoTotal: number;
    lineaCredito: number;
    condicionPago: string;
    montoPorCobrar: number;
    montoVencido: number;
    lineaCreditoUtilizada: number;
    mora: number
    nroFacturasVencidas: number;
    fechaFacturaVencidaMasAntigua: string;
    fechaUltimaFacturaPagada: string;
    estado: boolean;
    facturasVencidas: string;

    constructor(idLotePedido: number, codCliente: string, nombre: string, fechaCreacion: string, fechaRecorte: string,
        montoTotal: number, lineaCredito: number, condicionPago: string, montoPorCobrar: number, montoVencido: number,
        lineaCreditoUtilizada: number, mora: number, nroFacturasVencidas: number, fechaFacturaVencidaMasAntigua: string,
        fechaUltimaFacturaPagada: string, estado: boolean, facturasVencidas: string
    ) {
        this.idLotePedido = idLotePedido;
        this.codCliente = codCliente;
        this.nombre = nombre;
        this.fechaCreacion = fechaCreacion;
        this.fechaRecorte = fechaRecorte;
        this.montoTotal = montoTotal;
        this.lineaCredito = lineaCredito;
        this.condicionPago = condicionPago;
        this.montoPorCobrar = montoPorCobrar;
        this.montoVencido = montoVencido;
        this.lineaCreditoUtilizada = lineaCreditoUtilizada;
        this.mora = mora;
        this.nroFacturasVencidas = nroFacturasVencidas;
        this.fechaFacturaVencidaMasAntigua = fechaFacturaVencidaMasAntigua;
        this.fechaUltimaFacturaPagada = fechaUltimaFacturaPagada;
        this.estado = estado;
        this.facturasVencidas = facturasVencidas;
    }
}
