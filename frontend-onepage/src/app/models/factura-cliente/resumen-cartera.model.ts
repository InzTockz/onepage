export class ResumenCartera {
    periodo: number;
    noVencido: number;
    vencido0a30: number;
    vencido31a45: number;
    vencido46a60: number;
    vencido61a90: number;
    vencido91a180: number;
    vencido180aMas: number;

    constructor(periodo: number, noVencido: number, vencido0a30: number, vencido31a45: number,
        vencido46a60: number, vencido61a90: number, vencido91a180: number, vencido180aMas: number,) {
        this.periodo = periodo;
        this.noVencido = noVencido;
        this.vencido0a30 = vencido0a30;
        this.vencido31a45 = vencido31a45;
        this.vencido46a60 = vencido46a60;
        this.vencido61a90 = vencido61a90;
        this.vencido91a180 = vencido91a180;
        this.vencido180aMas = vencido180aMas;
    }
}
