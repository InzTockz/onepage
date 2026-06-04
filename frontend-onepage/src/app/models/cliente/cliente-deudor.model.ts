export class ClienteDeudor {
    ruc: string;
    nombre: string;
    slpCode: number;

    constructor(ruc: string, nombre: string, slpCode: number) {
        this.ruc = ruc;
        this.nombre = nombre;
        this.slpCode = slpCode;
    }
}
