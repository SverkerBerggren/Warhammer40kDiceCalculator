"use client";
import { log } from "console";
import { useState } from "react";
import { useEffect} from "react";
import ArmySelectButon from "@/app/src/ArmySelectButton";
import ArmyUploader from "./ArmyUpploader";
type ArmySelectorProps = {
    armies: any[];
    setArmy: (army: any) => void;
    setLocalStorageArmies: any;
}


export default function ArmySelector({ armies, setArmy,setLocalStorageArmies }: ArmySelectorProps) {
    return (
    <details >
        <summary>Select army</summary>
        <div className="max-h-50 overflow-auto">
            <ul className="">
                <ArmyUploader setLocalStorageArmies={setLocalStorageArmies}></ArmyUploader>
                    {armies.map((army, i) => (
                        <ArmySelectButon key={i} armyName={army.name} setArmy={setArmy}></ArmySelectButon>
                    ))}
            </ul>
        </div>
    </details>
  );
}