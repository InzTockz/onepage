export class Vendedor {
    slpCode: number;
    slpName: string;
    listNum: number;
    active: string;

    constructor(slpCode: number, slpName: string, listNum: number, active: string) {
        this.slpCode = slpCode;
        this.slpName = slpName;
        this.listNum = listNum;
        this.active = active;
    }
}
