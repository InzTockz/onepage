export class PedidosDiarios {
    docEntry: number;
    cardCode: string;
    cardName: string;
    pymntGroup: string;
    docTotalFC: number;
    creditLine: number;
    docDate: Date;
    facturasVencidas: number;
    montoVencido: number;
    cuentaTotal: number;
    montoPorVencer: number;
    fechaVencida: Date

    constructor(docEntry: number, cardCode: string, cardName: string, pymntGroup: string, docTotalFC: number, creditLine: number,
        docDate: Date, facturasVencidas: number, montoVencido: number, cuentaTotal: number, montoPorVencer: number, fechaVencida: Date
    ) {
        this.docEntry = docEntry;
        this.cardCode = cardCode;
        this.cardName = cardName;
        this.pymntGroup = pymntGroup;
        this.docTotalFC = docTotalFC;
        this.creditLine = creditLine;
        this.docDate = docDate;
        this.facturasVencidas = facturasVencidas;
        this.montoVencido = montoVencido;
        this.cuentaTotal = cuentaTotal;
        this.montoPorVencer = montoPorVencer;
        this.fechaVencida = fechaVencida;
    }
}
