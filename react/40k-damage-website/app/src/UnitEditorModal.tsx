"use client";
import { useEffect, useMemo, useState } from "react";
import { Unit, Model, Weapon, DiceAmount } from "@/app/src/DatasheetModeling/DatasheetModeling";
import {
  StatModifier,
  STAT_MODIFIER_LABELS,
  AbilityData,
  AbilityCatalogEntry,
} from "@/app/src/DatasheetModeling/DatasheetModeling";

import type { EditingUnit } from "@/app/page";

interface UnitEditorModalProps {
  editingUnit: EditingUnit;
  isOpen: boolean;
  onClose: () => void;
  onSave: (updatedUnit: Unit, index: Number, attacker: boolean) => void;
}

type EditorTab = "components" | "modifiers" | "abilities";

// Numeric enums in TS produce both name->value and value->name entries in
// Object.entries, so filter down to just the forward (name->number) pairs.
const STAT_MODIFIER_ENTRIES = Object.entries(StatModifier).filter(
  ([, value]) => typeof value === "number"
) as [string, number][];

function useAbilityCatalog() {
  const [catalog, setCatalog] = useState<AbilityCatalogEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch("http://localhost:7070/api/implemented-abilities")
      .then(res => {
        if (!res.ok) throw new Error(`${res.status}`);
        return res.json();
      })
      .then((data: AbilityCatalogEntry[]) => setCatalog(data))
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  return { catalog, loading, error };
}

// --- draft shapes used by the add/edit forms ---

type ModelDraft = Pick<Model, "name" | "toughness" | "armorSave" | "invulnerableSave" | "wounds">;

function blankModelDraft(): ModelDraft {
  return { name: "", toughness: 4, armorSave: 3, invulnerableSave: 7, wounds: 1 };
}

function blankDiceAmount(): DiceAmount {
  return { baseAmount: 1, numberOfD6: 0, numberOfD3: 0 };
}

// Two models are "the same row" iff every field matches, including name —
// that's deliberate: it's what makes Copy's rename immediately un-aggregate
// the new instance instead of it silently merging back into the group.
function modelGroupKey(model: Model): string {
  return JSON.stringify(model);
}

type ModelGroup = {
  key: string;
  model: Model; // representative — every member is structurally identical to this
  indices: number[]; // positions in listOfModels belonging to this group, in order
};

// Groups listOfModels by structural equality. Order-preserving: a group's
// position in the result reflects the first time its shape was seen.
function groupModels(models: Model[]): ModelGroup[] {
  const groups = new Map<string, ModelGroup>();
  const order: string[] = [];
  models.forEach((model, index) => {
    const key = modelGroupKey(model);
    const existing = groups.get(key);
    if (existing) {
      existing.indices.push(index);
    } else {
      groups.set(key, { key, model, indices: [index] });
      order.push(key);
    }
  });
  return order.map(key => groups.get(key)!);
}

// Appends " (copy)", " (copy 2)", ... until the name no longer collides with
// an existing model — guarantees a fresh copy never accidentally re-merges
// into the group it was copied from.
function uniqueCopyName(baseName: string, existingModels: Model[]): string {
  const existingNames = new Set(existingModels.map(m => m.name));
  let candidate = `${baseName} (copy)`;
  let n = 2;
  while (existingNames.has(candidate)) {
    candidate = `${baseName} (copy ${n})`;
    n++;
  }
  return candidate;
}

type WeaponDraft = {
  name: string;
  isMelee: boolean;
  ballisticSkill: number;
  strength: number;
  ap: number;
  amountOfAttacks: DiceAmount;
  damageAmount: DiceAmount;
};

function blankWeaponDraft(): WeaponDraft {
  return {
    name: "",
    isMelee: false,
    ballisticSkill: 4,
    strength: 4,
    ap: 0,
    amountOfAttacks: blankDiceAmount(),
    damageAmount: blankDiceAmount(),
  };
}

// Which abilities array a given AbilityManager instance is reading/writing.
// "model"/"weapon" scopes take the *group's* indices (every listOfModels
// position this aggregated card represents) rather than one index, so an
// ability change applies identically to every model in the group — keeping
// the group internally consistent instead of silently forking on the next
// render.
type AbilityPath =
  | { scope: "unit" }
  | { scope: "model"; modelIndices: number[] }
  | { scope: "weapon"; modelIndices: number[]; weaponIndex: number };

// Where the model/weapon forms currently point: adding a brand-new entry,
// or editing an existing group in place (preserves active/weapons/abilities
// and is applied to every index in the group).
type ModelFormMode = { type: "add" } | { type: "edit"; modelIndices: number[] };
type WeaponFormMode =
  | { modelIndices: number[]; type: "add" }
  | { modelIndices: number[]; type: "edit"; weaponIndex: number };

export default function UnitEditorModal({ editingUnit, isOpen, onClose, onSave }: UnitEditorModalProps) {
  const [workingUnit, setWorkingUnit] = useState<Unit>(() => structuredClone(editingUnit.unit));
  const [activeTab, setActiveTab] = useState<EditorTab>("components");
  const { catalog, loading: catalogLoading, error: catalogError } = useAbilityCatalog();

  const [modelFormMode, setModelFormMode] = useState<ModelFormMode | null>(null);
  const [modelDraft, setModelDraft] = useState<ModelDraft>(blankModelDraft());

  const [weaponFormMode, setWeaponFormMode] = useState<WeaponFormMode | null>(null);
  const [weaponDraft, setWeaponDraft] = useState<WeaponDraft>(blankWeaponDraft());

  // tracks which models/weapons currently have their abilities panel expanded
  const [expandedModelAbilities, setExpandedModelAbilities] = useState<Set<number>>(new Set());
  const [expandedWeaponAbilities, setExpandedWeaponAbilities] = useState<Set<string>>(new Set());

  // Identical models (same stats, weapons, abilities, active state, name)
  // collapse into one card with a count — see groupModels above.
  const modelGroups = useMemo(() => groupModels(workingUnit.listOfModels), [workingUnit.listOfModels]);

  if (!isOpen) return null;

  const unitAbilities = (workingUnit.abilities as unknown as AbilityData[]) ?? [];

  // --- generic mutation helpers ---

  function mutateUnit(mutator: (draft: Unit) => void) {
    setWorkingUnit(current => {
      const updated = structuredClone(current);
      mutator(updated);
      return updated;
    });
  }

  function abilitiesAt(draft: Unit, path: AbilityPath): AbilityData[] {
    if (path.scope === "unit") return ((draft.abilities as unknown as AbilityData[]) ??= []);
    if (path.scope === "model") return (draft.listOfModels[path.modelIndices[0]].abilities ??= []);
    return (draft.listOfModels[path.modelIndices[0]].weapons[path.weaponIndex].abilities ??= []);
  }

  function updateAbilitiesAt(path: AbilityPath, updater: (abilities: AbilityData[]) => AbilityData[]) {
    mutateUnit(draft => {
      const current = abilitiesAt(draft, path);
      console.log(current)
      const next = updater(current);
      if (path.scope === "unit") {
        draft.abilities = next as unknown as AbilityData[];
      } else if (path.scope === "model") {
        // Write to every group member, each with its own cloned array —
        // keeps the group structurally identical (so it stays one card)
        // without the members literally sharing an array reference.
        for (const idx of path.modelIndices) draft.listOfModels[idx].abilities = structuredClone(next);
      } else {
        for (const idx of path.modelIndices) draft.listOfModels[idx].weapons[path.weaponIndex].abilities = structuredClone(next);
      }
    });
  }

  // Builds the onToggle/onRemove/onAddCatalog/onAddCustom handlers for a given
  // abilities array (unit-level, one model, or one weapon).
  function abilityHandlersFor(path: AbilityPath) {
    return {
      onToggle: (i: number) =>
        updateAbilitiesAt(path, abilities => abilities.map((a, idx) => (idx === i ? { ...a, active: !a.active } : a))),
      // Only present when the backing Java class extends DualModeAbility —
      // see the `'boosted' in ability` guard in the render below. Mirrors
      // FlipBoosted() on the Java side; Gson round-trips `boosted` as a
      // plain field regardless of which class hierarchy defines it.
      onToggleBoost: (i: number) =>
      {      
        updateAbilitiesAt(path, abilities => abilities.map((a, idx) => (idx === i ? { ...a, isBoosted: !a.isBoosted } : a)))
      },
      onRemove: (i: number) => updateAbilitiesAt(path, abilities => abilities.filter((_, idx) => idx !== i)),
      // paramValues holds whatever the entry's ParamSpecs asked for (e.g.
      // { keyword: "Infantry", woundThreshold: 4 } for Anti-Keyword). They're
      // spread flat onto the AbilityData so the shape matches what Gson
      // writes/reads on the Java side (no nested "params" object). Entries
      // with params can be added more than once with different values
      // (Anti-Infantry 2+ and Anti-Monster 4+ are both valid on one weapon);
      // parameterless entries are singleton-ish, so we still dedupe those.
      onAddCatalog: (entry: AbilityCatalogEntry, paramValues: Record<string, string | number | boolean | DiceAmount> = {}) =>
        updateAbilitiesAt(path, abilities =>
          entry.params.length === 0 && abilities.some(a => a.kind === entry.kind)
            ? abilities
            : [
                ...abilities,
                {
                  kind: entry.kind,
                  name: entry.displayName,
                  description: entry.description,
                  active: true,
                  implemented: true,
                  hasBoosted: entry.hasBoosted,
                  isBoosted: false,
                  ...paramValues,
                },
              ]
        ),
      onAddCustom: (name: string, description: string) =>
        updateAbilitiesAt(path, abilities => [
          ...abilities,
          { kind: "CUSTOM", name, description, active: true, implemented: false,hasBoosted: false,isBoosted:false},
        ]),
    };
  }

  // --- model/component handlers ---

  function toggleModelGroup(modelIndices: number[]) {
    mutateUnit(draft => {
      const newActive = !draft.listOfModels[modelIndices[0]].active;
      for (const idx of modelIndices) draft.listOfModels[idx].active = newActive;
    });
  }

  function toggleWeapon(modelIndices: number[], weaponIndex: number) {
    mutateUnit(draft => {
      const newActive = !draft.listOfModels[modelIndices[0]].weapons[weaponIndex].active;
      for (const idx of modelIndices) draft.listOfModels[idx].weapons[weaponIndex].active = newActive;
    });
  }

  function removeModelGroup(modelIndices: number[]) {
    mutateUnit(draft => {
      const toRemove = new Set(modelIndices);
      draft.listOfModels = draft.listOfModels.filter((_, i) => !toRemove.has(i));
    });
  }

  function removeWeapon(modelIndices: number[], weaponIndex: number) {
    mutateUnit(draft => {
      for (const idx of modelIndices) draft.listOfModels[idx].weapons.splice(weaponIndex, 1);
    });
  }

  // Resizes a group to exactly newCount members by cloning the representative
  // model (growing) or dropping members from the end of the group (shrinking).
  // newCount <= 0 removes the group entirely — useful for "what happens if
  // this whole unit of models dies".
  function setModelCount(modelIndices: number[], newCount: number) {
    mutateUnit(draft => {
      if (newCount <= 0) {
        const toRemove = new Set(modelIndices);
        draft.listOfModels = draft.listOfModels.filter((_, i) => !toRemove.has(i));
        return;
      }
      const currentCount = modelIndices.length;
      if (newCount < currentCount) {
        const removeCount = currentCount - newCount;
        const toRemove = new Set(modelIndices.slice(modelIndices.length - removeCount));
        draft.listOfModels = draft.listOfModels.filter((_, i) => !toRemove.has(i));
      } else if (newCount > currentCount) {
        const template = draft.listOfModels[modelIndices[0]];
        const clones = Array.from({ length: newCount - currentCount }, () => structuredClone(template));
        draft.listOfModels.push(...clones);
      }
    });
  }

  function incrementModelCount(modelIndices: number[]) {
    setModelCount(modelIndices, modelIndices.length + 1);
  }

  // Decrementing a group of 1 removes it entirely rather than leaving a
  // zero-count group around.
  function decrementModelCount(modelIndices: number[]) {
    if (modelIndices.length <= 1) removeModelGroup(modelIndices);
    else setModelCount(modelIndices, modelIndices.length - 1);
  }

  // Duplicates the group's representative model as a brand-new, deliberately
  // renamed single instance — the rename is what keeps it from immediately
  // re-merging into the group it came from, letting the user then diverge it
  // (e.g. swap its weapon) without touching the rest of the squad.
  function copyModelAsNew(modelIndices: number[]) {
    mutateUnit(draft => {
      const template = draft.listOfModels[modelIndices[0]];
      const clone = structuredClone(template);
      clone.name = uniqueCopyName(clone.name, draft.listOfModels);
      draft.listOfModels.push(clone);
    });
  }

  function openAddModelForm() {
    setModelDraft(blankModelDraft());
    setModelFormMode({ type: "add" });
  }

  function openEditModelForm(modelIndices: number[]) {
    const m = workingUnit.listOfModels[modelIndices[0]];
    setModelDraft({
      name: m.name,
      toughness: m.toughness,
      armorSave: m.armorSave,
      invulnerableSave: m.invulnerableSave,
      wounds: m.wounds,
    });
    setModelFormMode({ type: "edit", modelIndices });
  }

  function cancelModelForm() {
    setModelFormMode(null);
    setModelDraft(blankModelDraft());
  }

  function submitModelForm() {
    if (!modelFormMode || !modelDraft.name.trim()) return;
    const name = modelDraft.name.trim();
    mutateUnit(draft => {
      if (modelFormMode.type === "edit") {
        for (const idx of modelFormMode.modelIndices) {
          const existing = draft.listOfModels[idx];
          draft.listOfModels[idx] = { ...existing, ...modelDraft, name };
        }
      } else {
        draft.listOfModels.push({ ...modelDraft, name, active: true, weapons: [], abilities: [] });
      }
    });
    cancelModelForm();
  }

  function openAddWeaponForm(modelIndices: number[]) {
    setWeaponDraft(blankWeaponDraft());
    setWeaponFormMode({ modelIndices, type: "add" });
  }

  function openEditWeaponForm(modelIndices: number[], weaponIndex: number) {
    const w = workingUnit.listOfModels[modelIndices[0]].weapons[weaponIndex];
    setWeaponDraft({
      name: String(w.name),
      isMelee: w.isMelee,
      ballisticSkill: w.ballisticSkill,
      strength: w.strength,
      ap: w.ap,
      amountOfAttacks: { ...w.amountOfAttacks },
      damageAmount: { ...w.damageAmount },
    });
    setWeaponFormMode({ modelIndices, type: "edit", weaponIndex });
  }

  function cancelWeaponForm() {
    setWeaponFormMode(null);
    setWeaponDraft(blankWeaponDraft());
  }

  function submitWeaponForm() {
    if (!weaponFormMode || !weaponDraft.name.trim()) return;
    const name = weaponDraft.name.trim();
    const { modelIndices } = weaponFormMode;
    mutateUnit(draft => {
      for (const idx of modelIndices) {
        const model = draft.listOfModels[idx];
        if (weaponFormMode.type === "edit") {
          const existing = model.weapons[weaponFormMode.weaponIndex];
          model.weapons[weaponFormMode.weaponIndex] = { ...existing, ...weaponDraft, name };
        } else {
          model.weapons.push({ ...weaponDraft, name, active: true, abilities: [] });
        }
      }
    });
    cancelWeaponForm();
  }

  function toggleExpandedModelAbilities(modelIndex: number) {
    setExpandedModelAbilities(current => {
      const next = new Set(current);
      next.has(modelIndex) ? next.delete(modelIndex) : next.add(modelIndex);
      return next;
    });
  }

  function toggleExpandedWeaponAbilities(key: string) {
    setExpandedWeaponAbilities(current => {
      const next = new Set(current);
      next.has(key) ? next.delete(key) : next.add(key);
      return next;
    });
  }

  // --- modifiers ---

  function updateModifier(modifierIndex: number, delta: number) {
    mutateUnit(draft => {
      const arr = (draft.statModifiers as any).statModifiers as number[];
      arr[modifierIndex] = (arr[modifierIndex] ?? 0) + delta;
    });
  }

  function setModifier(modifierIndex: number, value: number) {
    mutateUnit(draft => {
      const arr = (draft.statModifiers as any).statModifiers as number[];
      arr[modifierIndex] = value;
    });
  }

  function handleSave() {
    onSave(workingUnit, editingUnit.index, editingUnit.attacker);
    onClose();
  }

  // Exports the current working unit as a .json fixture. Drops the client-only
  // `id` field so the shape matches what the server / test fixtures expect.
  function exportUnitAsJson() {
    const { id, ...serverShapedUnit } = workingUnit;
    const blob = new Blob([JSON.stringify(serverShapedUnit, null, 2)], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    const safeName = (workingUnit.unitName || "unit").trim().replace(/\s+/g, "_");
    a.href = url;
    a.download = `${safeName}.json`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  }

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50" onClick={onClose}>
      <div
        className="bg-white dark:bg-gray-900 rounded-lg shadow-xl w-[680px] max-h-[80vh] flex flex-col"
        onClick={e => e.stopPropagation()}
      >
        <div className="flex justify-between items-center px-6 py-4 border-b border-gray-200 dark:border-gray-700 gap-3">
          <div className="flex items-center gap-3 flex-1">
            <input
              type="text"
              value={workingUnit.unitName}
              onChange={e => mutateUnit(draft => { draft.unitName = e.target.value; })}
              placeholder="Unit name"
              className="text-lg font-semibold bg-transparent border-b border-transparent focus:border-gray-300 outline-none flex-1 min-w-0"
            />
            <label className="flex items-center gap-1 text-xs text-gray-500 shrink-0">
              Points
              <input
                type="number"
                value={workingUnit.pointCost}
                onChange={e => mutateUnit(draft => { draft.pointCost = Number(e.target.value); })}
                className="w-16 border border-gray-300 dark:border-gray-600 rounded px-1 py-0.5 text-sm bg-white dark:bg-gray-800"
              />
            </label>
          </div>
          <button onClick={onClose} className="text-gray-500 hover:text-gray-800 dark:hover:text-gray-200 shrink-0">
            ✕
          </button>
        </div>

        <div className="flex border-b border-gray-200 dark:border-gray-700">
          {(["components", "modifiers", "abilities"] as EditorTab[]).map(tab => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`flex-1 py-2 capitalize text-sm font-medium ${
                activeTab === tab
                  ? "border-b-2 border-blue-500 text-blue-600"
                  : "text-gray-500 hover:text-gray-700 dark:hover:text-gray-300"
              }`}
            >
              {tab}
            </button>
          ))}
        </div>

        <div className="flex-1 overflow-y-auto px-6 py-4">
          {activeTab === "components" && (
            <div className="flex flex-col gap-4">
              {modelGroups.map(group => {
                const { model, indices } = group;
                const representativeIndex = indices[0];
                const modelAbilities = model.abilities ?? [];
                const abilitiesExpanded = expandedModelAbilities.has(representativeIndex);
                return (
                  <div key={representativeIndex} className="border border-gray-200 dark:border-gray-700 rounded p-3">
                    <div className="flex items-center justify-between gap-2">
                      <label className="flex items-center gap-2 font-medium min-w-0">
                        <input type="checkbox" checked={model.active} onChange={() => toggleModelGroup(indices)} />
                        <span className="truncate">{model.name || `Model ${representativeIndex + 1}`}</span>
                        <span className="text-xs font-normal text-gray-400 whitespace-nowrap">
                          T{model.toughness} Sv{model.armorSave}+ Inv{model.invulnerableSave}+ W{model.wounds}
                        </span>
                      </label>
                      <div className="flex items-center gap-3 shrink-0">
                        {/* Count stepper — grows/shrinks the group by cloning or
                            dropping instances. This is the "N models died" control:
                            it changes how many identical models exist, separate
                            from active (which toggles the whole group on/off). */}
                        <div className="flex items-center gap-1 text-xs" title="Number of identical models">
                          <button
                            onClick={() => decrementModelCount(indices)}
                            className="w-5 h-5 bg-gray-200 dark:bg-gray-700 rounded"
                          >
                            −
                          </button>
                          <input
                            type="number"
                            min={0}
                            value={indices.length}
                            onChange={e => setModelCount(indices, Number(e.target.value))}
                            className="w-10 text-center border border-gray-300 dark:border-gray-600 rounded text-xs bg-white dark:bg-gray-800"
                          />
                          <button
                            onClick={() => incrementModelCount(indices)}
                            className="w-5 h-5 bg-gray-200 dark:bg-gray-700 rounded"
                          >
                            +
                          </button>
                        </div>
                        <button
                          onClick={() => copyModelAsNew(indices)}
                          title="Add one new model, same stats, as its own (renamed) entry — not aggregated with this group"
                          className="text-gray-500 hover:text-gray-700 text-xs"
                        >
                          Copy
                        </button>
                        <button
                          onClick={() => openEditModelForm(indices)}
                          className="text-blue-500 hover:text-blue-700 text-xs"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => removeModelGroup(indices)}
                          className="text-red-500 hover:text-red-700 text-xs"
                        >
                          Remove
                        </button>
                      </div>
                    </div>

                    {modelFormMode?.type === "edit" && modelFormMode.modelIndices[0] === representativeIndex && (
                      <ModelForm draft={modelDraft} onChange={setModelDraft} onSubmit={submitModelForm} onCancel={cancelModelForm} submitLabel="Save model" />
                    )}

                    <div className="pl-6 mt-2 flex flex-col gap-1">
                      {model.weapons.map((weapon, weaponIndex) => {
                        const weaponKey = `${representativeIndex}-${weaponIndex}`;
                        const weaponAbilities = weapon.abilities ?? [];
                        const weaponAbilitiesExpanded = expandedWeaponAbilities.has(weaponKey);
                        const isEditingThisWeapon =
                          weaponFormMode?.type === "edit" &&
                          weaponFormMode.modelIndices[0] === representativeIndex &&
                          weaponFormMode.weaponIndex === weaponIndex;
                        return (
                          <div key={weaponIndex} className="text-sm">
                            <div className="flex items-center justify-between gap-2">
                              <label className="flex items-center gap-2 min-w-0">
                                <input
                                  type="checkbox"
                                  checked={weapon.active}
                                  disabled={!model.active}
                                  onChange={() => toggleWeapon(indices, weaponIndex)}
                                />
                                <span className={!model.active ? "text-gray-400 truncate" : "truncate"}>
                                  {String(weapon.name)}
                                  <span className="text-xs text-gray-400 ml-1">
                                    ({weapon.isMelee ? "melee" : "ranged"}, S{weapon.strength} AP{weapon.ap})
                                  </span>
                                </span>
                              </label>
                              <div className="flex items-center gap-2 shrink-0 text-xs">
                                <button
                                  onClick={() => toggleExpandedWeaponAbilities(weaponKey)}
                                  className="text-gray-500 hover:text-gray-700"
                                >
                                  Abilities{weaponAbilities.length > 0 ? ` (${weaponAbilities.length})` : ""}
                                </button>
                                <button
                                  onClick={() => openEditWeaponForm(indices, weaponIndex)}
                                  className="text-blue-500 hover:text-blue-700"
                                >
                                  Edit
                                </button>
                                <button
                                  onClick={() => removeWeapon(indices, weaponIndex)}
                                  className="text-red-500 hover:text-red-700"
                                >
                                  Remove
                                </button>
                              </div>
                            </div>

                            {isEditingThisWeapon && (
                              <WeaponForm draft={weaponDraft} onChange={setWeaponDraft} onSubmit={submitWeaponForm} onCancel={cancelWeaponForm} submitLabel="Save weapon" />
                            )}

                            {weaponAbilitiesExpanded && (
                              <div className="pl-6 mt-1 mb-2 border-l border-gray-200 dark:border-gray-700 pl-3">
                                <AbilityManager
                                  abilities={weaponAbilities}
                                  catalog={catalog}
                                  catalogLoading={catalogLoading}
                                  catalogError={catalogError}
                                  compact
                                  {...abilityHandlersFor({ scope: "weapon", modelIndices: indices, weaponIndex })}
                                />
                              </div>
                            )}
                          </div>
                        );
                      })}

                      {weaponFormMode?.type === "add" && weaponFormMode.modelIndices[0] === representativeIndex ? (
                        <WeaponForm draft={weaponDraft} onChange={setWeaponDraft} onSubmit={submitWeaponForm} onCancel={cancelWeaponForm} submitLabel="Add weapon" />
                      ) : (
                        <button
                          onClick={() => openAddWeaponForm(indices)}
                          className="self-start mt-1 text-xs bg-green-100 hover:bg-green-200 text-green-800 px-2 py-1 rounded"
                        >
                          + Add weapon
                        </button>
                      )}
                    </div>

                    <div className="mt-2">
                      <button
                        onClick={() => toggleExpandedModelAbilities(representativeIndex)}
                        className="text-xs text-gray-500 hover:text-gray-700"
                      >
                        Model abilities{modelAbilities.length > 0 ? ` (${modelAbilities.length})` : ""}
                      </button>
                      {abilitiesExpanded && (
                        <div className="mt-2 border-t border-gray-100 dark:border-gray-800 pt-2">
                          <AbilityManager
                            abilities={modelAbilities}
                            catalog={catalog}
                            catalogLoading={catalogLoading}
                            catalogError={catalogError}
                            {...abilityHandlersFor({ scope: "model", modelIndices: indices })}
                          />
                        </div>
                      )}
                    </div>
                  </div>
                );
              })}

              {modelFormMode?.type === "add" ? (
                <ModelForm draft={modelDraft} onChange={setModelDraft} onSubmit={submitModelForm} onCancel={cancelModelForm} submitLabel="Add model" />
              ) : (
                <button
                  onClick={openAddModelForm}
                  className="self-start text-sm bg-green-100 hover:bg-green-200 text-green-800 px-3 py-1.5 rounded"
                >
                  + Add model
                </button>
              )}
            </div>
          )}

          {activeTab === "modifiers" && (
            <div className="grid grid-cols-2 gap-4">
              {STAT_MODIFIER_ENTRIES.map(([name, ordinal]) => (
                <ModifierInput
                  key={name}
                  label={STAT_MODIFIER_LABELS[name] ?? name}
                  value={(workingUnit.statModifiers as any).statModifiers?.[ordinal] ?? 0}
                  onIncrement={() => updateModifier(ordinal, 1)}
                  onDecrement={() => updateModifier(ordinal, -1)}
                  onSet={v => setModifier(ordinal, v)}
                />
              ))}
            </div>
          )}

          {activeTab === "abilities" && (
            <div>
              <h3 className="text-sm font-medium mb-2">This unit's abilities</h3>
              <AbilityManager
                abilities={unitAbilities}
                catalog={catalog}
                catalogLoading={catalogLoading}
                catalogError={catalogError}
                {...abilityHandlersFor({ scope: "unit" })}
              />
            </div>
          )}
        </div>

        <div className="flex justify-between items-center gap-2 px-6 py-4 border-t border-gray-200 dark:border-gray-700">
          <button
            onClick={exportUnitAsJson}
            title="Download this unit as a .json fixture (client-only id field is stripped)"
            className="text-sm px-4 py-1 rounded border border-gray-300 dark:border-gray-600 hover:bg-gray-100 dark:hover:bg-gray-800"
          >
            Export as JSON
          </button>
          <div className="flex gap-2">
            <button onClick={onClose} className="px-4 py-1 text-sm rounded border border-gray-300 dark:border-gray-600">
              Cancel
            </button>
            <button onClick={handleSave} className="bg-blue-500 hover:bg-blue-700 text-white text-sm px-4 py-1 rounded">
              {editingUnit.isNew ? "Add unit" : "Save changes"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

function ModifierInput({
  label,
  value,
  onIncrement,
  onDecrement,
  onSet,
}: {
  label: string;
  value: number;
  onIncrement: () => void;
  onDecrement: () => void;
  onSet: (v: number) => void;
}) {
  return (
    <div className="flex flex-col gap-1">
      <label className="text-xs text-gray-500">{label}</label>
      <div className="flex items-center gap-1">
        <button onClick={onDecrement} className="w-6 h-6 bg-gray-200 dark:bg-gray-700 rounded text-sm">−</button>
        <input
          type="number"
          value={value}
          onChange={e => onSet(Number(e.target.value))}
          className="w-14 text-center border border-gray-300 dark:border-gray-600 rounded text-sm bg-white dark:bg-gray-800"
        />
        <button onClick={onIncrement} className="w-6 h-6 bg-gray-200 dark:bg-gray-700 rounded text-sm">+</button>
      </div>
    </div>
  );
}

type ParamValue = string | number | boolean | DiceAmount;

// Small inline form shown after picking a catalog entry that needs setup
// (Anti-Keyword's keyword + threshold, Sustained Hits' D3 amount, etc). Only
// rendered for entries with a non-empty params list — everything else is
// added straight from the search result. Field types come from ParamSpec's
// ParamType ('INT' | 'KEYWORD' | 'BOOLEAN' | 'DICE_AMOUNT'); KEYWORD is a
// free-text input for now since the frontend doesn't have the Keyword enum
// values — swap for a <select> once the backend exposes them. DICE_AMOUNT
// reuses the same baseAmount/D6/D3 fields WeaponForm already uses for
// attacks/damage, since it's the identical DiceAmount shape on the wire.
function CatalogParamForm({
  entry,
  onSubmit,
  onCancel,
}: {
  entry: AbilityCatalogEntry;
  onSubmit: (values: Record<string, ParamValue>) => void;
  onCancel: () => void;
}) {
  const [values, setValues] = useState<Record<string, ParamValue>>(() =>
    Object.fromEntries(
      entry.params.map(p => [
        p.fieldName,
        p.type === "BOOLEAN" ? false : p.type === "INT" ? 0 : p.type === "DICE_AMOUNT" ? blankDiceAmount() : "",
      ])
    )
  );

  return (
    <div className="border border-dashed border-green-300 dark:border-green-700 rounded p-2 flex flex-col gap-2 bg-green-50 dark:bg-green-950/30">
      <p className="text-xs font-medium">{entry.displayName}</p>
      <div className="flex gap-3 flex-wrap">
        {entry.params.map(param => (
          param.type === "DICE_AMOUNT" ? (
            <DiceAmountFields
              key={param.fieldName}
              label={param.uiLabel}
              value={values[param.fieldName] as DiceAmount}
              onChange={d => setValues(v => ({ ...v, [param.fieldName]: d }))}
            />
          ) : (
            <label key={param.fieldName} className="flex flex-col gap-0.5 text-[11px] text-gray-500">
              {param.uiLabel}
              {param.type === "BOOLEAN" ? (
                <input
                  type="checkbox"
                  checked={Boolean(values[param.fieldName])}
                  onChange={e => setValues(v => ({ ...v, [param.fieldName]: e.target.checked }))}
                  className="mt-1"
                />
              ) : param.type === "INT" ? (
                <input
                  type="number"
                  value={Number(values[param.fieldName])}
                  onChange={e => setValues(v => ({ ...v, [param.fieldName]: Number(e.target.value) }))}
                  className="w-16 border border-gray-300 dark:border-gray-600 rounded px-1 py-0.5 text-sm bg-white dark:bg-gray-800"
                />
              ) : (
                <input
                  type="text"
                  value={String(values[param.fieldName])}
                  onChange={e => setValues(v => ({ ...v, [param.fieldName]: e.target.value }))}
                  className="w-24 border border-gray-300 dark:border-gray-600 rounded px-1 py-0.5 text-sm bg-white dark:bg-gray-800"
                />
              )}
            </label>
          )
        ))}
      </div>
      <div className="flex gap-2">
        <button onClick={() => onSubmit(values)} className="bg-green-500 hover:bg-green-700 text-white text-xs px-3 py-1 rounded">
          Add
        </button>
        <button onClick={onCancel} className="text-xs px-3 py-1 rounded border border-gray-300 dark:border-gray-600">
          Cancel
        </button>
      </div>
    </div>
  );
}

// Generic ability list + add-from-catalog + add-custom, reused for unit-level,
// model-level, and weapon-level abilities (e.g. Lethal Hits on a weapon).
function AbilityManager({
  abilities,
  catalog,
  catalogLoading,
  catalogError,
  onToggle,
  onToggleBoost,
  onRemove,
  onAddCatalog,
  onAddCustom,
  compact = false,
}: {
  abilities: AbilityData[];
  catalog: AbilityCatalogEntry[];
  catalogLoading: boolean;
  catalogError: string | null;
  onToggle: (index: number) => void;
  onToggleBoost: (index: number) => void;
  onRemove: (index: number) => void;
  onAddCatalog: (entry: AbilityCatalogEntry, paramValues?: Record<string, string | number | boolean | DiceAmount>) => void;
  onAddCustom: (name: string, description: string) => void;
  compact?: boolean;
}) {
  const [customName, setCustomName] = useState("");
  const [customDescription, setCustomDescription] = useState("");
  const [catalogSearch, setCatalogSearch] = useState("");
  // Set when the user picks a catalog entry that needs params (e.g.
  // Anti-Keyword) — holds off adding it until the small form is submitted.
  const [pendingEntry, setPendingEntry] = useState<AbilityCatalogEntry | null>(null);

  // Parameterless entries (Torrent, Lethal Hits) are effectively singleton
  // per abilities-array, so hide them once added. Parameterized entries
  // (Anti-Infantry 2+ vs Anti-Monster 4+) are legitimately addable more than
  // once with different values, so they always stay searchable.
  const addableCatalogEntries = catalog.filter(entry => {
    if (entry.params.length === 0 && abilities.some(a => a.kind === entry.kind)) return false;
    if (!catalogSearch.trim()) return true;
    return entry.displayName.toLowerCase().includes(catalogSearch.trim().toLowerCase());
  });

  function submitCustom() {
    if (!customName.trim()) return;
    onAddCustom(customName.trim(), customDescription.trim());
    setCustomName("");
    setCustomDescription("");
  }

  return (
    <div className="flex flex-col gap-2">
      {abilities.length === 0 && <p className="text-xs text-gray-400">No abilities.</p>}
      {abilities.length > 0 && (
        <ul className="flex flex-col gap-1.5">
          {abilities.map((ability, i) => (
            <li
              key={i}
              className="flex items-start justify-between bg-gray-100 dark:bg-gray-800 rounded px-2 py-1.5"
            >
              <div className="flex gap-2 items-start min-w-0">
                <input type="checkbox" checked={ability.active} onChange={() => onToggle(i)} className="mt-1" />
                <div className="min-w-0">
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-medium">{ability.name}</span>
                    {ability.implemented ? (
                      <span className="text-[9px] uppercase tracking-wide bg-green-100 text-green-700 px-1 py-0.5 rounded shrink-0">
                        Simulated
                      </span>
                    ) : (
                      <span className="text-[9px] uppercase tracking-wide bg-gray-200 text-gray-500 px-1 py-0.5 rounded shrink-0">
                        No logic
                      </span>
                    )}
                    {  ability.hasBoosted && (
                      <button
                        onClick={() => onToggleBoost(i)}
                        title="Toggle this ability's boosted mode (DualModeAbility on the server)"
                        className={`text-[9px] uppercase tracking-wide px-1 py-0.5 rounded shrink-0 ${
                          ability.isBoosted
                            ? "bg-amber-200 text-amber-800 hover:bg-amber-300"
                            : "bg-gray-200 text-gray-500 hover:bg-gray-300"
                        }`}
                      >
                        {ability.isBoosted ? "Boosted" : "Boost off"}
                      </button>
                    )}
                  </div>
                  {ability.description && !compact && (
                    <p className="text-[11px] text-gray-500 dark:text-gray-400 mt-0.5">{ability.description}</p>
                  )}
                </div>
              </div>
              <button onClick={() => onRemove(i)} className="text-red-500 hover:text-red-700 text-[11px] shrink-0 ml-2">
                Remove
              </button>
            </li>
          ))}
        </ul>
      )}

      {catalogLoading && <p className="text-xs text-gray-400">Loading catalog…</p>}
      {catalogError && <p className="text-xs text-red-500">Couldn't load ability catalog: {catalogError}</p>}
      {!catalogLoading && !catalogError && (
        <div className="flex flex-col gap-1.5">
          <input
            type="text"
            value={catalogSearch}
            onChange={e => {
              setCatalogSearch(e.target.value);
              setPendingEntry(null);
            }}
            placeholder="Search abilities to add…"
            className="border border-gray-300 dark:border-gray-600 rounded px-2 py-1 text-xs bg-white dark:bg-gray-800"
          />
          {catalogSearch.trim() && !pendingEntry && (
            <ul className="flex flex-col gap-0.5 max-h-40 overflow-y-auto border border-gray-200 dark:border-gray-700 rounded">
              {addableCatalogEntries.length === 0 && (
                <li className="text-[11px] text-gray-400 px-2 py-1">No matches.</li>
              )}
              {addableCatalogEntries.map(entry => (
                <li key={entry.kind}>
                  <button
                    onClick={() =>
                      entry.params.length === 0
                        ? (onAddCatalog(entry), setCatalogSearch(""))
                        : setPendingEntry(entry)
                    }
                    title={entry.description}
                    className="w-full text-left text-[11px] hover:bg-green-100 dark:hover:bg-green-900/40 px-2 py-1"
                  >
                    + {entry.displayName}
                    {entry.params.length > 0 && <span className="text-gray-400"> (needs setup)</span>}
                  </button>
                </li>
              ))}
            </ul>
          )}
          {pendingEntry && (
            <CatalogParamForm
              entry={pendingEntry}
              onSubmit={values => {
                onAddCatalog(pendingEntry, values);
                setPendingEntry(null);
                setCatalogSearch("");
              }}
              onCancel={() => setPendingEntry(null)}
            />
          )}
        </div>
      )}

      <div className="flex gap-1.5 items-start">
        <div className="flex-1 flex flex-col gap-1">
          <input
            type="text"
            value={customName}
            onChange={e => setCustomName(e.target.value)}
            placeholder="Custom ability name"
            className="border border-gray-300 dark:border-gray-600 rounded px-2 py-1 text-xs bg-white dark:bg-gray-800"
          />
          {!compact && (
            <textarea
              value={customDescription}
              onChange={e => setCustomDescription(e.target.value)}
              placeholder="Description (optional)"
              rows={2}
              className="border border-gray-300 dark:border-gray-600 rounded px-2 py-1 text-xs bg-white dark:bg-gray-800"
            />
          )}
        </div>
        <button onClick={submitCustom} className="bg-gray-500 hover:bg-gray-700 text-white text-xs px-2 py-1 rounded shrink-0">
          Add
        </button>
      </div>
    </div>
  );
}

function ModelForm({
  draft,
  onChange,
  onSubmit,
  onCancel,
  submitLabel,
}: {
  draft: ModelDraft;
  onChange: (d: ModelDraft) => void;
  onSubmit: () => void;
  onCancel: () => void;
  submitLabel: string;
}) {
  const numberField = (key: keyof ModelDraft, label: string) => (
    <label className="flex flex-col gap-0.5 text-xs text-gray-500">
      {label}
      <input
        type="number"
        value={draft[key] as number}
        onChange={e => onChange({ ...draft, [key]: Number(e.target.value) })}
        className="w-16 border border-gray-300 dark:border-gray-600 rounded px-1 py-0.5 text-sm bg-white dark:bg-gray-800"
      />
    </label>
  );

  return (
    <div className="border border-dashed border-gray-300 dark:border-gray-600 rounded p-3 mt-2 flex flex-col gap-2">
      <input
        type="text"
        value={draft.name}
        onChange={e => onChange({ ...draft, name: e.target.value })}
        placeholder="Model name"
        className="border border-gray-300 dark:border-gray-600 rounded px-2 py-1 text-sm bg-white dark:bg-gray-800"
      />
      <div className="flex gap-3 flex-wrap">
        {numberField("toughness", "Toughness")}
        {numberField("armorSave", "Armor Sv+")}
        {numberField("invulnerableSave", "Invuln Sv+")}
        {numberField("wounds", "Wounds")}
      </div>
      <div className="flex gap-2">
        <button onClick={onSubmit} className="bg-green-500 hover:bg-green-700 text-white text-xs px-3 py-1 rounded">
          {submitLabel}
        </button>
        <button onClick={onCancel} className="text-xs px-3 py-1 rounded border border-gray-300 dark:border-gray-600">
          Cancel
        </button>
      </div>
    </div>
  );
}

function DiceAmountFields({
  label,
  value,
  onChange,
}: {
  label: string;
  value: DiceAmount;
  onChange: (d: DiceAmount) => void;
}) {
  return (
    <div className="flex flex-col gap-0.5 text-xs text-gray-500">
      {label}
      <div className="flex gap-1 items-center">
        <input
          type="number"
          value={value.baseAmount}
          onChange={e => onChange({ ...value, baseAmount: Number(e.target.value) })}
          title="Flat base amount"
          className="w-12 border border-gray-300 dark:border-gray-600 rounded px-1 py-0.5 text-sm bg-white dark:bg-gray-800"
        />
        <span>+</span>
        <input
          type="number"
          value={value.numberOfD6}
          onChange={e => onChange({ ...value, numberOfD6: Number(e.target.value) })}
          title="Number of D6 to roll"
          className="w-12 border border-gray-300 dark:border-gray-600 rounded px-1 py-0.5 text-sm bg-white dark:bg-gray-800"
        />
        <span>D6 +</span>
        <input
          type="number"
          value={value.numberOfD3}
          onChange={e => onChange({ ...value, numberOfD3: Number(e.target.value) })}
          title="Number of D3 to roll"
          className="w-12 border border-gray-300 dark:border-gray-600 rounded px-1 py-0.5 text-sm bg-white dark:bg-gray-800"
        />
        <span>D3</span>
      </div>
    </div>
  );
}

function WeaponForm({
  draft,
  onChange,
  onSubmit,
  onCancel,
  submitLabel,
}: {
  draft: WeaponDraft;
  onChange: (d: WeaponDraft) => void;
  onSubmit: () => void;
  onCancel: () => void;
  submitLabel: string;
}) {
  return (
    <div className="border border-dashed border-gray-300 dark:border-gray-600 rounded p-2 mt-1 flex flex-col gap-2">
      <div className="flex gap-2 items-center">
        <input
          type="text"
          value={draft.name}
          onChange={e => onChange({ ...draft, name: e.target.value })}
          placeholder="Weapon name"
          className="flex-1 border border-gray-300 dark:border-gray-600 rounded px-2 py-1 text-sm bg-white dark:bg-gray-800"
        />
        <label className="flex items-center gap-1 text-xs text-gray-500">
          <input
            type="checkbox"
            checked={draft.isMelee}
            onChange={e => onChange({ ...draft, isMelee: e.target.checked })}
          />
          Melee
        </label>
      </div>

      <div className="flex gap-3 flex-wrap">
        <label className="flex flex-col gap-0.5 text-xs text-gray-500">
          {draft.isMelee ? "Weapon Skill" : "Ballistic Skill"}
          <input
            type="number"
            value={draft.ballisticSkill}
            onChange={e => onChange({ ...draft, ballisticSkill: Number(e.target.value) })}
            className="w-16 border border-gray-300 dark:border-gray-600 rounded px-1 py-0.5 text-sm bg-white dark:bg-gray-800"
          />
        </label>
        <label className="flex flex-col gap-0.5 text-xs text-gray-500">
          Strength
          <input
            type="number"
            value={draft.strength}
            onChange={e => onChange({ ...draft, strength: Number(e.target.value) })}
            className="w-16 border border-gray-300 dark:border-gray-600 rounded px-1 py-0.5 text-sm bg-white dark:bg-gray-800"
          />
        </label>
        <label className="flex flex-col gap-0.5 text-xs text-gray-500">
          AP
          <input
            type="number"
            value={draft.ap}
            onChange={e => onChange({ ...draft, ap: Number(e.target.value) })}
            className="w-16 border border-gray-300 dark:border-gray-600 rounded px-1 py-0.5 text-sm bg-white dark:bg-gray-800"
          />
        </label>
      </div>

      <div className="flex gap-4 flex-wrap">
        <DiceAmountFields
          label="Attacks"
          value={draft.amountOfAttacks}
          onChange={d => onChange({ ...draft, amountOfAttacks: d })}
        />
        <DiceAmountFields
          label="Damage"
          value={draft.damageAmount}
          onChange={d => onChange({ ...draft, damageAmount: d })}
        />
      </div>

      <div className="flex gap-2">
        <button onClick={onSubmit} className="bg-green-500 hover:bg-green-700 text-white text-xs px-3 py-1 rounded">
          {submitLabel}
        </button>
        <button onClick={onCancel} className="text-xs px-3 py-1 rounded border border-gray-300 dark:border-gray-600">
          Cancel
        </button>
      </div>
    </div>
  );
}