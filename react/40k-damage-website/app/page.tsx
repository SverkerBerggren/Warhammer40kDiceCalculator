"use client";


import Image from "next/image";
import TestGrej from "@/app/src/test";
import ArmyUploader from "@/app/src/ArmyUpploader";
import ArmySelector from "@/app/src/ArmySelector"
import { useEffect} from "react";
import { useState} from "react";
import ArmyLayout from "./src/ArmyLayout";

import { Army, Unit,GamePiece,gamePieceFromArmy, Conditions, ServerUnit, createBlankUnit} from "@/app/src/DatasheetModeling/DatasheetModeling";
import { NodeNextRequest } from "next/dist/server/base-http/node";
import { log } from "console";
import DistributionChart from "./src/DistributionChart";
import UnitEditorModal from "./src/UnitEditorModal";
import { useMemo } from "react";
import { toServerUnit } from "./src/utils/armyUtils";
interface RollResult{
    woundsDealt: number[];
    modelsSlain: number[];
    averageAmountOfWounds: number[];
    averageAmountOfModelsSlain: number[];
}


class CalculateDamageRequest {
    attackerList: ServerUnit[];
    defendingUnit: ServerUnit;
    attackingArmy: GamePiece;
    defendingArmy: GamePiece;
    conditions: Conditions;
    constructor (attackerList: ServerUnit[],defendingUnit: ServerUnit,attackingArmy:GamePiece,defendingArmy:GamePiece,conditions:Conditions)
    {
        this.attackerList = attackerList;
        this.defendingUnit = defendingUnit;
        this.attackingArmy = attackingArmy;
        this.defendingArmy = defendingArmy;
        this.conditions = conditions;
    }
}

export type EditingUnit = {
    unit: Unit;
    index: number;
    attacker: boolean;
    // True when this came from "+ Add unit" rather than editing an existing
    // entry — lets UnitEditorModal show "Add unit" instead of "Save changes"
    // and lets onSave know to append rather than replace in place.
    isNew?: boolean;
};


export default function Home() {
  const [armies,setArmies] = useState<Army[] >([]);
    useEffect(() => {
        const result = Object.keys(localStorage)
            .filter(key => key.startsWith("army:"))
            .map(key => JSON.parse(localStorage.getItem(key)!));
            setArmies(result)
        }, []);
        
  const [attackerArmy,setAttackerArmy] = useState<Army >();
  const [defenderArmy,setDefenderArmy] = useState<Army >();
  const [selectedAttackerIds, setSelectedAttackerIds] = useState<string[]>([]);
  const [selectedDefenderId, setSelectedDefenderId] = useState<string | null>(null);
  const [damageResult, setDamageResult] = useState<RollResult>();
  const [error, setError] = useState<string | null>(null);
  const [editingUnit, setEditingUnit] = useState<EditingUnit | null>(null);

  const attackingUnits = useMemo(() => {
    if (!attackerArmy) return [];
    return attackerArmy.units.filter(u => selectedAttackerIds.includes(u.id));
  }, [attackerArmy, selectedAttackerIds]);
  const defendingUnit = useMemo(() => {
    if (!defenderArmy || selectedDefenderId == null) return null;
    return defenderArmy.units.find(u => u.id === selectedDefenderId) ?? null;
  }, [defenderArmy, selectedDefenderId]);

  
  // Exports the current working unit as a .json fixture. Drops the client-only
  // `id` field so the shape matches what the server / test fixtures expect.
  function exportAttackingUnitAsJson( ) {
    const serverShapedUnits = attackingUnits.map(({ id, ...rest }) => rest);
    const blob = new Blob([JSON.stringify(serverShapedUnits, null, 2)], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    const safeName = (serverShapedUnits[0].unitName || "unit").trim().replace(/\s+/g, "_");
    a.href = url;
    a.download = `${safeName}.json`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  }

  function fetchCalculateDamageRequest(attackingUnits: Unit[],defendingUnit: Unit,attackingArmy: Army,defendingArmy: Army,conditions:Conditions){
    let attackingGamePiece = gamePieceFromArmy(attackingArmy);
    let defendingGamePiece = gamePieceFromArmy(defendingArmy);
    let attackingServerUnits: ServerUnit[] = attackingUnits.map(toServerUnit);
    let defendingServerUnit: ServerUnit = toServerUnit(defendingUnit);

    let calculateDamageRequest = new CalculateDamageRequest(attackingServerUnits,defendingServerUnit,attackingGamePiece,defendingGamePiece,conditions)
    let requestBody = JSON.stringify(calculateDamageRequest);

    fetch("http://localhost:7070/api/calculate-damage", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: requestBody,
    })
    .then((res) => {
        if (!res.ok) {
            return res.text().then(body => {
                console.log("Error response body:", body);
                throw new Error(`${res.status}: ${body}`);
            });
        }
        return res.json();
    })
    .then((data: RollResult) => {
        console.log("Success:", data);
        setDamageResult(data);
    })
    .catch((err) => {
        console.log("Caught error:", err.message);
        setError(err.message);
    });
}

  function toggleUnit(unit: Unit) {
     setSelectedAttackerIds(current => {
         const exists = current.includes(unit.id);
         if (exists) {
             return current.filter(u => u !== unit.id);
         }
         return [...current, unit.id];
     });
  }
  function toggleDefendingUnit(unit: Unit) {
     setSelectedDefenderId(current => {
        if(unit.id == current)
        {
            return null;
        }
        return unit.id;
     });
  }

  // Opens the editor pre-loaded with a blank unit for the given side. Reuses
  // the same UnitEditorModal + onSave path as editing an existing unit —
  // index is set to the current end of the list so onSave's array-index
  // assignment appends rather than overwrites.
  function openAddUnit(attacker: boolean) {
    const army = attacker ? attackerArmy : defenderArmy;
    if (!army) return;
    setEditingUnit({
      unit: createBlankUnit(),
      index: army.units.length,
      attacker,
      isNew: true,
    });
  }

  function SaveArmy(army: Army)
  {
      const storageKey = `army:${army.name}`;
      localStorage.setItem(storageKey, JSON.stringify(army));
  }


  return (
      <div className="flex flex-col flex-1 items-center justify-center bg-sky-50 font-sans dark:bg-black">
        <h1 className="py-8 bg-sky-50">Unit crunch moggaren</h1>
        <TestGrej></TestGrej>
        {editingUnit && (
            <UnitEditorModal
              editingUnit={editingUnit}
              isOpen={!!editingUnit}
              onClose={() => setEditingUnit(null)}
              onSave={(updated,index,attacker) => {
                // replace the unit in its army's unit list + persist to localStorage

                const armyToUpdate = attacker ? attackerArmy : defenderArmy;

                if (!armyToUpdate) {
                    return;
                }
                console.log("efter grejen")
                const newUnits = [...armyToUpdate.units];
                newUnits[index] = updated;


                attacker ? setAttackerArmy({...armyToUpdate,units:newUnits}) :setDefenderArmy({...armyToUpdate,units:newUnits}) 

                setEditingUnit(null);
              }}
         />
        )}
        <main className="flex flex-1 w-full bg-white dark:bg-black">          

                <div className="w-[20%] flex flex-col  items-center">
                    {/** <AttackerPanel /> */}
                    <h1>Attacker</h1>
                    <div className="grid grid-cols-3 items-center w-full">
                        <div></div>
                    
                        {attackerArmy ? (
                            <h1 className="justify-self-center">{attackerArmy.name}</h1>
                        ) : (
                            <h1 className="justify-self-center">Choose attacking army</h1>
                        )}
                        <div className="justify-self-end">
                            <ArmySelector
                                armies={armies}
                                setArmy={setAttackerArmy}
                                setLocalStorageArmies={setArmies}
                            />
                        </div>
                    </div>
                    {attackerArmy && (
                        <ArmyLayout army={attackerArmy} toggleUnit={toggleUnit} attacker={true} setEditingUnit={setEditingUnit} ></ArmyLayout>
                    )}
                    {attackerArmy && (
                        <button
                          onClick={() => openAddUnit(true)}
                          className="mt-2 text-sm bg-green-100 hover:bg-green-200 text-green-800 px-3 py-1.5 rounded"
                        >
                          + Add unit
                        </button>
                    )}
                    {attackerArmy && (
                        <button
                          onClick={() => SaveArmy(attackerArmy)}
                          className="mt-2 text-sm bg-blue-100 hover:bg-blue-200 text-blue-800 px-3 py-1.5 rounded"
                        >
                          Save and persist army
                        </button>
                    )}
                </div>

                <div className="w-[20%] flex flex-col  items-center">
                    {/** <Defender panel /> */}
                    <h1>Defender</h1>
                    <div className="grid grid-cols-3 items-center w-full">
                        <div></div>
                    
                        {defenderArmy ? (
                            <h1 className="justify-self-center">{defenderArmy.name}</h1>
                        ) : (
                            <h1 className="justify-self-center">Choose defending army</h1>
                        )}
                        <div className="justify-self-end">
                            <ArmySelector
                                armies={armies}
                                setArmy={setDefenderArmy}
                                setLocalStorageArmies={setArmies}
                            />
                        </div>
                    </div>
                    {defenderArmy && (
                        <ArmyLayout army={defenderArmy} toggleUnit={toggleDefendingUnit} attacker={false} setEditingUnit={setEditingUnit} ></ArmyLayout>
                    )}
                    {defenderArmy && (
                        <button
                          onClick={() => openAddUnit(false)}
                          className="mt-2 text-sm bg-green-100 hover:bg-green-200 text-green-800 px-3 py-1.5 rounded"
                        >
                          + Add unit
                        </button>
                    )}
                    {defenderArmy && (
                        <button
                          onClick={() => SaveArmy(defenderArmy)}
                          className="mt-2 text-sm bg-blue-100 hover:bg-blue-200 text-blue-800 px-3 py-1.5 rounded"
                        >
                          Save and persist army
                        </button>
                    )}
                </div>

                <div className="flex w-[60%] ">
                    {/** <resultPanel /> */}
                    <div className="w-[30%] flex flex-col items-center">
                        <h1>Selected units</h1>
                        <div className="flex h-[40%] flex-col">
                            <div className="flex flex-col flex h-[70%]">
                                <h1>Attacking units</h1>
                                <ul>
                                {attackingUnits &&( attackingUnits.map((unit, i) => (
                                  <li className="py-1" key={i}>
                                    <button className="bg-blue-500 hover:bg-blue-700 text-white font-bold px-4 rounded" 
                                    style={{textAlign: "center"  }} onClick={()=> toggleUnit(unit)}>{unit.unitName}</button>
                                  </li>
                                )))}
                                </ul>
                                <button
                                onClick={() => exportAttackingUnitAsJson()}
                                    className="mt-2 text-sm bg-blue-100 hover:bg-blue-200 text-blue-800 px-3 py-1.5 rounded"
                                    >
                                    Export attacking units as json
                                </button>
                            </div>
                            <div className="flex flex-col h-[30%]">
                                <h1>Defending unit</h1>
                                { defendingUnit && (<button className="bg-blue-500 hover:bg-blue-700 text-white font-bold px-4 rounded" 
                                    style={{textAlign: "center"  }} onClick={()=> toggleDefendingUnit(defendingUnit)}>{defendingUnit.unitName}</button>)
                                }
                            </div>
                        </div>
                    </div>
                    <div className="w-[70%] flex flex-col items-center">
                        <h1>Result</h1>
                        { defendingUnit && attackerArmy && defenderArmy&& (<button  className="bg-blue-500 hover:bg-blue-700 text-white font-bold px-4 rounded" 
                                    style={{textAlign: "center"  }} onClick={()=> fetchCalculateDamageRequest(attackingUnits,defendingUnit,
                                        attackerArmy,defenderArmy,new Conditions())
                                    } > Calculate damage </button>)
                        }
                        {damageResult && (
                            <div className="w-full flex flex-col gap-8 px-4">
                                <div className="flex justify-around text-sm text-gray-600 dark:text-gray-300">
                                    <span>Avg. wounds: {damageResult.averageAmountOfWounds}</span>
                                    <span>Avg. models slain: {damageResult.averageAmountOfModelsSlain}</span>
                                </div>
                                <DistributionChart samples={damageResult.woundsDealt} title="Wounds Dealt Distribution" color="#3b82f6" />
                                <DistributionChart samples={damageResult.modelsSlain} title="Models Slain Distribution" color="#ef4444" />
                            </div>
                        )}
                    </div>
                </div>
        </main>
     </div>
  );
}