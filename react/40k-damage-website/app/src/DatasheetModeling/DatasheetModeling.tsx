export interface Unit {
    unitName: string;
    pointCost: number;
    abilities: AbilityData[]
    listOfModels: Model[];
    statModifiers: StatModifiersData
    id: string
}
export type ServerUnit = Omit<Unit, 'id'>;

export interface Model {
    name: string;
    toughness: number;
    armorSave: number;
    invulnerableSave: number;
    wounds: number;

    active: boolean;
    weapons: Weapon[]
    abilities: AbilityData[]
}
export interface Weapon
{
    name: String;
    amountOfAttacks: DiceAmount;

    damageAmount: DiceAmount;
    // Does not take to account N/A yet
    ballisticSkill: number;
    ap: number;
    strength:number;
    isMelee:boolean;
    active:boolean;
    abilities: AbilityData[]
}

export interface DiceAmount {

    baseAmount: number;

    numberOfD6: number;

    numberOfD3: number;
}

export interface Army {
    name: string;
    units: Unit[];
    abilities: any[]
    statModifiers: any
}

export class GamePiece{

    abilities: any[] = []
    statModifiers: any
}

export function gamePieceFromArmy(army: Army) : GamePiece
{
    let retValue: GamePiece = new GamePiece();
    
    retValue.abilities = army.abilities;
    retValue.statModifiers = army.statModifiers;

    return retValue;
}

export class Conditions{
    rapidFireRange: boolean = false;
    meleeCombat: boolean = false;
    rangedCombat: boolean = true;
    mediumBlast: boolean = false;
    fullBlast: boolean = false;
    tacticalDoctrine: boolean = false;
    devastatorDoctrine: boolean = false;
    assaultDoctrine: boolean = false;
    plusOneToWound: boolean = false;
    dakkaHalfRange: boolean = false;
}

// PLACEHOLDER — must match core.Enums.StatModifier ordinal order EXACTLY.
// The Java side stores modifiers in a private int[] indexed by ordinal(),
// so if this enum's order doesn't match, values get written to the wrong stat.
// Replace this with the real enum once StatModifier.java is shared.
export enum StatModifier {
  HitRoll = 0,
  WoundRoll = 1,
  BallisticSkill = 2,
  WeaponSkill = 3,
  Strength = 4,
  AP = 5,
  Damage = 6,
  Attacks = 7,
  Toughness = 8,
  ArmorSave = 9,
  InvulnerableSave = 10,
}
 
// Human-readable labels for the UI — keyed by enum name, not ordinal,
// so this stays correct even if you reorder the Java enum later.
export const STAT_MODIFIER_LABELS: Record<string, string> = {
  HitRoll: "Hit Roll",
  WoundRoll: "Wound Roll",
  BallisticSkill: "Ballistic Skill",
  WeaponSkill: "Weapon Skill",
  Strength: "Strength",
  AP: "AP",
  Damage: "Damage",
  Attacks: "Attacks",
  Toughness: "Toughness",
  ArmorSave: "Armor Save",
  InvulnerableSave: "Invulnerable Save",
};
 
// Matches the private int[] statModifiers field on StatModifiers.java.
// Assumes it serializes to JSON as { statModifiers: number[] } — verify
// against an actual network payload once you test this.
export interface StatModifiersData {
  statModifiers: number[];
}
 
// Matches Ability.java. `implemented` requires the isImplemented() getter
// suggested in chat — without it this field won't be present in the JSON.
// `kind` matches the AbilityKind tag AbilityElementAdapter uses to pick a
// class on deserialize; abilities added via the "custom" free-text flow
// (no backing Java class) should use a kind that isn't in the server
// registry, e.g. "CUSTOM", so they round-trip as UnimplementedAbility.
// Any extra param fields (e.g. `keyword`, `woundThreshold` for Anti-Keyword)
// are flattened directly onto the object — Gson writes fields flat, not
// nested under a "params" key, so the wire shape needs to match that.
export interface AbilityData {
  kind: string;
  name: string;
  description: string;
  active: boolean;
  implemented: boolean;
  hasBoosted: boolean;
  isBoosted: boolean;
  [paramField: string]: unknown;
}

// Builds a brand-new, empty Unit for the "add unit" flow in the editor.
// `id` is a fresh client-side id (mirrors what assignUnitIds does on upload).
// statModifiers is sized to match the number of StatModifier enum entries —
// keep in sync if you add/remove entries from that enum.
export function createBlankUnit(): Unit {
  const modifierCount = Object.keys(StatModifier).filter(k => isNaN(Number(k))).length;
  return {
    id: crypto.randomUUID(),
    unitName: "New Unit",
    pointCost: 0,
    abilities: [],
    listOfModels: [],
    statModifiers: { statModifiers: new Array(modifierCount).fill(0) },
  };
}

export interface AbilityCatalogEntry {
  kind: string;
  displayName: string;
  description: string;
  params: { fieldName: string; type: 'INT' | 'KEYWORD' | 'BOOLEAN' | 'DICE_AMOUNT'; uiLabel: string }[];
  hasBoosted: boolean;
}