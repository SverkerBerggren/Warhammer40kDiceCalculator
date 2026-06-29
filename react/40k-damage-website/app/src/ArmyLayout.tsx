"use client";
import { log } from "console";
import { useState } from "react";
import { useEffect} from "react";
import { resourceLimits } from "worker_threads";
import type { Army, Unit } from "@/app/src/DatasheetModeling/DatasheetModeling";

type ArmyLayoutProps = {
    army: Army;
};


export default function ArmyLayout({ army }: ArmyLayoutProps) {
    return (
    <div>
      <ul>
        {army.units.map((unit, i) => (
          <li className="py-1" key={i}>
            <button className="bg-blue-500 hover:bg-blue-700 text-white font-bold px-4 rounded" style={{textAlign: "center"  }}>{unit.unitName}</button>
          </li>
        ))}
      </ul>
    </div>      
  );
}