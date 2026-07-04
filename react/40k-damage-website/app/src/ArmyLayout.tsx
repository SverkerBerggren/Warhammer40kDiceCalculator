"use client";
import { log } from "console";
import { useState } from "react";
import { useEffect} from "react";
import { resourceLimits } from "worker_threads";
import type { Army, Unit } from "@/app/src/DatasheetModeling/DatasheetModeling";
import { detectConflictingPaths } from "next/dist/build/utils";

import type { EditingUnit } from "../page";


type ArmyLayoutProps = {
    army: Army;
    attacker: boolean
    toggleUnit: (unit: Unit) =>void;
    setEditingUnit: (editingUnit: EditingUnit) =>void;
};


export default function ArmyLayout({ army, attacker, toggleUnit, setEditingUnit}: ArmyLayoutProps) {

    
    return (
    <div>
      <ul>
        {army.units.map((unit, index) => (
          <li className="py-1" key={index}>
            <div className="flex flex-row gap-x-2">              
              <button className="bg-blue-500 hover:bg-blue-700 text-white font-bold px-4 rounded" 
              style={{textAlign: "center"  }} onClick={ 
                  ( )=>{toggleUnit(unit)}  
                } >{unit.unitName}</button>
              <button className="bg-green-300 hover:bg-green-700 px-2 rounded" onClick={ 
                  ( )=>
                  {
                    setEditingUnit({unit,index,attacker})
                  }  
                } >Edit</button>
            </div>
          </li>
        ))}
      </ul>
    </div>      
  );
}