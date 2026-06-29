"use client";
import { log } from "console";
import { useState } from "react";
import { useEffect} from "react";
import { resourceLimits } from "worker_threads";

type ArmyButtonProps = {
    armyName: String;
    setArmy: any
};

export default function ArmySelectButton({ armyName,setArmy }: ArmyButtonProps) {
    let army: JSON = JSON.parse( localStorage.getItem("army:" +armyName)!);

    return (
    <div className="justify-center" >
        <button onClick={ ()=> setArmy(army)} className="bg-blue-500 hover:bg-blue-700 text-white font-bold px-4 py-2 rounded" style={{textAlign: "center"  }}>
            {armyName}
        </button>
    </div>
  );
}