"use client";


import Image from "next/image";
import TestGrej from "@/app/src/test";
import ArmyUploader from "@/app/src/ArmyUpploader";
import ArmySelector from "@/app/src/ArmySelector"
import { useEffect} from "react";
import { useState} from "react";
import ArmyLayout from "./src/ArmyLayout";

import type { Army, Unit } from "@/app/src/DatasheetModeling/DatasheetModeling";



export default function Home() {
  const [armies,setArmies] = useState<any[] >([]);
    useEffect(() => {
        const result = Object.keys(localStorage)
            .filter(key => key.startsWith("army:"))
            .map(key => JSON.parse(localStorage.getItem(key)!));
            setArmies(result)
        }, []);
        
  const [attackerArmy,setAttackerArmy] = useState<Army >();
  const [defenderArmy,setDefenderArmy] = useState<Army >();

  return (
      <div className="flex flex-col flex-1 items-center justify-center bg-sky-50 font-sans dark:bg-black">
        <h1 className="py-8 bg-sky-50">Unit crunch moggaren</h1>
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
                        <ArmyLayout army={attackerArmy} ></ArmyLayout>
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
                        <ArmyLayout army={defenderArmy} ></ArmyLayout>
                    )}
                </div>

                <div className="flex w-[60%] justify-center">
                    {/** <resultPanel /> */}
                    <div className="flex flex-row">
                        <h1>Selected units</h1>
                    </div>
                    <div> 
                        <h1>Result</h1>
                    </div>
                </div>
        </main>
     </div>
  );
}