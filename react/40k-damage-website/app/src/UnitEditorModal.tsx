"use client";
import { useEffect, useState } from "react";
import { Unit } from "@/app/src/DatasheetModeling/DatasheetModeling";
import {
  StatModifier,
  STAT_MODIFIER_LABELS,
  AbilityData,
  AbilityCatalogEntry,
} from "@/app/src/DatasheetModeling/DatasheetModeling";

import type {EditingUnit} from"@/app/page"

interface UnitEditorModalProps {
  editingUnit: EditingUnit;
  isOpen: boolean;
  onClose: () => void;
  onSave: (updatedUnit: Unit,index: Number,attacker:boolean) => void;
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
    fetch("http://localhost:7070/api/abilities")
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
 
export default function UnitEditorModal({ editingUnit, isOpen, onClose, onSave }: UnitEditorModalProps) {
  const [workingUnit, setWorkingUnit] = useState<Unit>(() => structuredClone(editingUnit.unit));
  const [activeTab, setActiveTab] = useState<EditorTab>("components");
  const [customAbilityName, setCustomAbilityName] = useState("");
  const [customAbilityDescription, setCustomAbilityDescription] = useState("");
  const { catalog, loading: catalogLoading, error: catalogError } = useAbilityCatalog();
 
  if (!isOpen) return null;
 
  const unitAbilities = workingUnit.abilities as unknown as AbilityData[];
 
  function toggleModel(modelIndex: number) {
    setWorkingUnit(current => {
      const updated = structuredClone(current);
      updated.listOfModels[modelIndex].active = !updated.listOfModels[modelIndex].active;
      return updated;
    });
  }
 
  function toggleWeapon(modelIndex: number, weaponIndex: number) {
    setWorkingUnit(current => {
      const updated = structuredClone(current);
      updated.listOfModels[modelIndex].weapons[weaponIndex].active =
        !updated.listOfModels[modelIndex].weapons[weaponIndex].active;
      return updated;
    });
  }
 
  function toggleAbility(abilityIndex: number) {
    setWorkingUnit(current => {
      const updated = structuredClone(current);
      (updated.abilities as unknown as AbilityData[])[abilityIndex].active =
        !(updated.abilities as unknown as AbilityData[])[abilityIndex].active;
      return updated;
    });
  }
 
  function removeAbility(abilityIndex: number) {
    setWorkingUnit(current => {
      const updated = structuredClone(current);
      updated.abilities.splice(abilityIndex, 1);
      return updated;
    });
  }
 
  // Adds an ability picked from the server-provided implemented catalog.
  function addCatalogAbility(entry: AbilityCatalogEntry) {
    const alreadyPresent = unitAbilities.some(a => a.name === entry.name);
    if (alreadyPresent) return;
    setWorkingUnit(current => {
      const updated = structuredClone(current);
      (updated.abilities as unknown as AbilityData[]).push({
        name: entry.name,
        description: entry.description,
        active: true,
        implemented: true,
      });
      return updated;
    });
  }
 
  // Adds a free-text ability with no simulation logic — purely for record-keeping
  // (e.g. datasheet abilities you haven't built logic for yet).
  function addCustomAbility() {
    if (!customAbilityName.trim()) return;
    setWorkingUnit(current => {
      const updated = structuredClone(current);
      (updated.abilities as unknown as AbilityData[]).push({
        name: customAbilityName.trim(),
        description: customAbilityDescription.trim(),
        active: true,
        implemented: false,
      });
      return updated;
    });
    setCustomAbilityName("");
    setCustomAbilityDescription("");
  }
 
  function updateModifier(modifierIndex: number, delta: number) {
    setWorkingUnit(current => {
      const updated = structuredClone(current);
      const arr = (updated.statModifiers as any).statModifiers as number[];
      arr[modifierIndex] = (arr[modifierIndex] ?? 0) + delta;
      return updated;
    });
  }
 
  function setModifier(modifierIndex: number, value: number) {
    setWorkingUnit(current => {
      const updated = structuredClone(current);
      const arr = (updated.statModifiers as any).statModifiers as number[];
      arr[modifierIndex] = value;
      return updated;
    });
  }
 
  function handleSave() {
    onSave(workingUnit,editingUnit.index,editingUnit.attacker);
    onClose();
  }
 
  const addableCatalogEntries = catalog.filter(
    entry => !unitAbilities.some(a => a.name === entry.name)
  );
 
  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50" onClick={onClose}>
      <div
        className="bg-white dark:bg-gray-900 rounded-lg shadow-xl w-[640px] max-h-[80vh] flex flex-col"
        onClick={e => e.stopPropagation()}
      >
        <div className="flex justify-between items-center px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          <h2 className="text-lg font-semibold">{workingUnit.unitName}</h2>
          <button onClick={onClose} className="text-gray-500 hover:text-gray-800 dark:hover:text-gray-200">
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
              {workingUnit.listOfModels.map((model, modelIndex) => (
                <div key={modelIndex} className="border border-gray-200 dark:border-gray-700 rounded p-3">
                  <label className="flex items-center gap-2 font-medium">
                    <input type="checkbox" checked={model.active} onChange={() => toggleModel(modelIndex)} />
                    {model.name || `Model ${modelIndex + 1}`}
                  </label>
                  <div className="pl-6 mt-2 flex flex-col gap-1">
                    {model.weapons.map((weapon, weaponIndex) => (
                      <label key={weaponIndex} className="flex items-center gap-2 text-sm">
                        <input
                          type="checkbox"
                          checked={weapon.active}
                          disabled={!model.active}
                          onChange={() => toggleWeapon(modelIndex, weaponIndex)}
                        />
                        <span className={!model.active ? "text-gray-400" : ""}>{weapon.name}</span>
                      </label>
                    ))}
                  </div>
                </div>
              ))}
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
            <div className="flex flex-col gap-5">
              <div>
                <h3 className="text-sm font-medium mb-2">This unit's abilities</h3>
                {unitAbilities.length === 0 && (
                  <p className="text-sm text-gray-400">No abilities on this unit.</p>
                )}
                <ul className="flex flex-col gap-2">
                  {unitAbilities.map((ability, i) => (
                    <li
                      key={i}
                      className="flex items-start justify-between bg-gray-100 dark:bg-gray-800 rounded px-3 py-2"
                    >
                      <div className="flex gap-2 items-start">
                        <input
                          type="checkbox"
                          checked={ability.active}
                          onChange={() => toggleAbility(i)}
                          className="mt-1"
                        />
                        <div>
                          <div className="flex items-center gap-2">
                            <span className="text-sm font-medium">{ability.name}</span>
                            {ability.implemented ? (
                              <span className="text-[10px] uppercase tracking-wide bg-green-100 text-green-700 px-1.5 py-0.5 rounded">
                                Simulated
                              </span>
                            ) : (
                              <span className="text-[10px] uppercase tracking-wide bg-gray-200 text-gray-500 px-1.5 py-0.5 rounded">
                                No logic
                              </span>
                            )}
                          </div>
                          {ability.description && (
                            <p className="text-xs text-gray-500 dark:text-gray-400 mt-0.5">
                              {ability.description}
                            </p>
                          )}
                        </div>
                      </div>
                      <button
                        onClick={() => removeAbility(i)}
                        className="text-red-500 hover:text-red-700 text-xs shrink-0 ml-2"
                      >
                        Remove
                      </button>
                    </li>
                  ))}
                </ul>
              </div>
 
              <div>
                <h3 className="text-sm font-medium mb-2">Add simulated ability</h3>
                {catalogLoading && <p className="text-xs text-gray-400">Loading catalog…</p>}
                {catalogError && (
                  <p className="text-xs text-red-500">Couldn't load ability catalog: {catalogError}</p>
                )}
                {!catalogLoading && !catalogError && (
                  <div className="flex flex-wrap gap-2">
                    {addableCatalogEntries.length === 0 && (
                      <p className="text-xs text-gray-400">All catalog abilities are already on this unit.</p>
                    )}
                    {addableCatalogEntries.map(entry => (
                      <button
                        key={entry.name}
                        onClick={() => addCatalogAbility(entry)}
                        title={entry.description}
                        className="text-xs bg-green-100 hover:bg-green-200 text-green-800 px-2 py-1 rounded"
                      >
                        + {entry.name}
                      </button>
                    ))}
                  </div>
                )}
              </div>
 
              <div>
                <h3 className="text-sm font-medium mb-2">Add unsimulated ability</h3>
                <p className="text-xs text-gray-400 mb-2">
                  For tracking datasheet abilities that don't yet affect the simulation.
                </p>
                <div className="flex flex-col gap-2">
                  <input
                    type="text"
                    value={customAbilityName}
                    onChange={e => setCustomAbilityName(e.target.value)}
                    placeholder="Ability name"
                    className="border border-gray-300 dark:border-gray-600 rounded px-2 py-1 text-sm bg-white dark:bg-gray-800"
                  />
                  <textarea
                    value={customAbilityDescription}
                    onChange={e => setCustomAbilityDescription(e.target.value)}
                    placeholder="Description (optional)"
                    rows={2}
                    className="border border-gray-300 dark:border-gray-600 rounded px-2 py-1 text-sm bg-white dark:bg-gray-800"
                  />
                  <button
                    onClick={addCustomAbility}
                    className="self-start bg-gray-500 hover:bg-gray-700 text-white text-sm px-3 py-1 rounded"
                  >
                    Add
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
 
        <div className="flex justify-end gap-2 px-6 py-4 border-t border-gray-200 dark:border-gray-700">
          <button onClick={onClose} className="px-4 py-1 text-sm rounded border border-gray-300 dark:border-gray-600">
            Cancel
          </button>
          <button onClick={handleSave} className="bg-blue-500 hover:bg-blue-700 text-white text-sm px-4 py-1 rounded">
            Save changes
          </button>
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