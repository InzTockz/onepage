export class LotePedido {
    idLotePedido: number;
    docentry: number;
    codCliente: string;
    nombre: string;
    montoTotalPedido: number;
    condicionPago: string;
    lineaCredito: number;
    montoTotalDeuda: number;
    montoPorCobrar: number;
    montoVencido: number;
    lineaCreditoUtilizada: number;
    mora: number;
    nroFacturasVencidas: number;
    fechaFacturaVencidaMasAntigua: string;
    fechaUltimaFacturaPagada: string;

    fechaCreacionPedido: string;
    fechaRegistro: string;
    fechaLoteGenerado: string;
    estadoBorrador: string;
    facturasVencidas: string;
    docTime: number;
    comentario: string;

    constructor(
        idLotePedido: number,
        docentry: number,
        codCliente: string,
        nombre: string,
        montoTotalPedido: number,
        condicionPago: string,
        lineaCredito: number,
        montoTotalDeuda: number,
        montoPorCobrar: number,
        montoVencido: number,
        lineaCreditoUtilizada: number,
        mora: number,
        nroFacturasVencidas: number,
        fechaFacturaVencidaMasAntigua: string,
        fechaUltimaFacturaPagada: string,
        fechaCreacionPedido: string,
        fechaRegistro: string,
        fechaLoteGenerado: string,
        estadoBorrador: string,
        facturasVencidas: string,
        docTime: number,
        comentario: string
    ) {
        this.idLotePedido = idLotePedido;
        this.docentry = docentry;
        this.codCliente = codCliente;
        this.nombre = nombre;
        this.montoTotalPedido = montoTotalPedido;
        this.condicionPago = condicionPago;
        this.lineaCredito = lineaCredito;
        this.montoTotalDeuda = montoTotalDeuda;
        this.montoPorCobrar = montoPorCobrar;
        this.montoVencido = montoVencido;
        this.lineaCreditoUtilizada = lineaCreditoUtilizada;
        this.mora = mora;
        this.nroFacturasVencidas = nroFacturasVencidas;
        this.fechaFacturaVencidaMasAntigua = fechaFacturaVencidaMasAntigua;
        this.fechaUltimaFacturaPagada = fechaUltimaFacturaPagada;
        this.fechaCreacionPedido = fechaCreacionPedido;
        this.fechaRegistro = fechaRegistro;
        this.fechaLoteGenerado = fechaLoteGenerado;
        this.estadoBorrador = estadoBorrador;
        this.facturasVencidas = facturasVencidas;
        this.docTime = docTime;
        this.comentario = comentario;
    }
}
