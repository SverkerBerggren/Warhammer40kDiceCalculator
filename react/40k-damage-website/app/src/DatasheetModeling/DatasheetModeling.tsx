export interface Unit {
    unitName: string;
    pointCost: number;
    listOfModels: any[];
}

export interface Army {
    name: string;
    units: Unit[];
}

