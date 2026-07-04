"use client";
import { useState } from "react";
import { json } from "stream/consumers";
import { useRef } from "react";
import { Army, Unit,GamePiece,gamePieceFromArmy, Conditions} from "@/app/src/DatasheetModeling/DatasheetModeling";
import {assignUnitIds} from "@/app/src/utils/armyUtils"
import { Dispatch, SetStateAction } from "react";
type ArmySelectorProps = {
    setLocalStorageArmies: Dispatch<SetStateAction<Army[]>>;
}
export default function ArmyUploader( {setLocalStorageArmies }: ArmySelectorProps) {
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  async function handleFileUpload(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;

    setLoading(true);
    setError(null);
    
    const text = await file.text();

    const requestBody = {"name": file.name, "list": text}

    try {
      const response = await fetch("http://localhost:7070/api/parse-army", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestBody),
      });

      const data = await response.json();

      if (!response.ok) {
        setError(data.error ?? "Server error");
        return;
      }
    const army: Army = assignUnitIds(data); // <-- ID assignment happens exactly once, here

    localStorage.setItem(`army:${army.name}`, JSON.stringify(army));
    const result:Army[] = Object.keys(localStorage)
          .filter(key => key.startsWith("army:"))
          .map(key => JSON.parse(localStorage.getItem(key)!));

    setLocalStorageArmies(result);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }
  
  return (
    <div >
      <input type="file" accept=".txt"
       onChange={handleFileUpload} className="hidden"
      ref={fileInputRef} />
      <button onClick={() => fileInputRef.current?.click()} className="bg-green-500 hover:bg-blue-700 text-white font-bold px-4 py-2 rounded">
          Upload new army
      </button>
      {loading && <p>Parsing army...</p>}
      {error && <p className="text-red-500">Error: {error}</p>}
    </div>
  );
}