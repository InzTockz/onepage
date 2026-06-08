export class ComentarioPedidoRequest {
    docEntry: number;
    codCliente: string;
    comentario: string;

    constructor(docEntry: number, codCliente: string, comentario: string) {
        this.docEntry = docEntry;
        this.codCliente = codCliente;
        this.comentario = comentario;
    }
}
