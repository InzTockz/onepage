export class Cliente {
    cardCode: string;
    cardName: string;
    email: string;
    licTradNum: string;
    creditLine: number;
    groupNum: number;
    frozenFor: string;
    listNum: number;
    slpCode: number;

    constructor(cardCode: string, cardName: string, email: string, licTradNum: string, creditLine: number, groupNum: number,
        frozenFor: string, listNum: number, slpCode: number) {
        this.cardCode = cardCode;
        this.cardName = cardName;
        this.email = email;
        this.licTradNum = licTradNum;
        this.creditLine = creditLine;
        this.groupNum = groupNum;
        this.frozenFor = frozenFor;
        this.listNum = listNum;
        this.slpCode = slpCode;
    }
}
