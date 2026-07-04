import { Army, Unit,ServerUnit,GamePiece,gamePieceFromArmy, Conditions} from "@/app/src/DatasheetModeling/DatasheetModeling";


export function assignUnitIds(rawArmy: Omit<Army, 'units'> & { units: Omit<Unit, 'id'>[] }): Army {
  return {
    ...rawArmy,
    units: rawArmy.units.map(unit => ({
      ...unit,
      id: crypto.randomUUID(),
    })),
  };
}
export function toServerUnit(unit: Unit): ServerUnit {
  const { id, ...serverUnit } = unit;
  return serverUnit;
}