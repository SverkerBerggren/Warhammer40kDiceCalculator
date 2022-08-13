package com.example.warhammer40kdicecalculator;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class RollingLogic {


    public void TestOmFungerar()
    {
        Log.d(("hej"),"hejhej");
    }

    public void newCalculateDamage(Unit attacker, Unit defender)
    {
        int amountOfWoundsTotal;
        int amountOfModelsKilled;
        int currentModelDamage;
        ArrayList<Integer> resultWoundsDealt = new ArrayList<Integer>();
        ArrayList<Integer> resultModelsSlain = new ArrayList<Integer>();

        Unit OriginalUnit = defender.copy();

  //      for(int i = 0; i < defender.listOfModels.size();i++)
  //      {
  //
  //      }

        for(int z = 0; z < 10000; z++)
        {


            amountOfWoundsTotal = 0;
            amountOfModelsKilled = 0;
            currentModelDamage = 0;
            int currentDefendingModelInteger = 0;
            Model currentDefendingModel = defender.listOfModels.get(0);
            for(int i = 0; i < attacker.listOfModels.size(); i++)
            {

                Model currentAttackingModel = attacker.listOfModels.get(i);

                for(int f = 0; f < currentAttackingModel.listOfRangedWeapons.size(); f++)
                {

                    RangedWeapon currentWeapon = currentAttackingModel.listOfRangedWeapons.get(f);
                    int requiredBallisticSkil= currentAttackingModel.ballisticSkill;
                    int damage = currentWeapon.damage;
                    int ap = currentWeapon.ap;
                    int strength = currentWeapon.strength;

                    MetricsOfAttacking currentMetricsOfAttacking = new MetricsOfAttacking(0, ap,damage, 0,0);
                    int amountOfAttacks = 0;

                    amountOfAttacks += currentWeapon.amountOfAttacks.rawNumberOfAttacks;

                    for( int p = 0; p < currentWeapon.amountOfAttacks.numberOfD3; p++)
                    {
                        Log.d(("Testar loopar: "),"Fungerar vapen loopen loopen");
                        Integer diceResult = ThreadLocalRandom.current().nextInt(1, 3 +1 );

                        for(int c = 0; c < currentAttackingModel.listOfAbilites.size(); c++)
                        {
                            currentAttackingModel.listOfAbilites.get(c).rollNumberOfShots(diceResult,currentMetricsOfAttacking);
                        }
                        amountOfAttacks += diceResult;
                    }
                    for( int p = 0; p < currentWeapon.amountOfAttacks.numberOfD6; p++)
                    {


                        Integer diceResult = ThreadLocalRandom.current().nextInt(1, 6 +1 );
                        for(int c = 0; c < currentAttackingModel.listOfAbilites.size(); c++)
                        {
                            currentAttackingModel.listOfAbilites.get(c).rollNumberOfShots(diceResult,currentMetricsOfAttacking);
                        }
                        amountOfAttacks += diceResult;
                    }




                    for(int p = 0; p < amountOfAttacks; p++)
                    {

                       // Log.d(("Testar loopar: "),"Fungerar amount of attacks loopen loopen " + p);
                        Integer hitRoll = ThreadLocalRandom.current().nextInt(1, 6 +1 );

                        for(int k = 0; k < currentAttackingModel.listOfAbilites.size(); k++)
                        {
                            currentAttackingModel.listOfAbilites.get(k).hitRollAbility(hitRoll,currentMetricsOfAttacking);
                        }

                        if(hitRoll >= requiredBallisticSkil)
                        {
                            currentMetricsOfAttacking.hits += 1;
                        }

                        for(int j = 0; j < currentMetricsOfAttacking.hits; j++)
                        {
                            int woundRoll = ThreadLocalRandom.current().nextInt(1, 6 +1 );
                            for(int m = 0; m < currentAttackingModel.listOfAbilites.size(); m++ )
                            {
                                currentAttackingModel.listOfAbilites.get(m).woundRollAbility(woundRoll,currentMetricsOfAttacking);
                            }
                            for(int m = 0; m < currentAttackingModel.listOfAbilites.size(); m++ )
                            {
                                currentAttackingModel.listOfAbilites.get(m).woundRollAbility(woundRoll,currentMetricsOfAttacking);
                            }

                            int requiredResult = -1;
                            if(strength == currentDefendingModel.toughness)
                            {
                                requiredResult = 4;
                            }
                            else if(strength >= currentDefendingModel.toughness *2)
                            {
                                requiredResult = 2;
                            }
                            else if(strength > currentDefendingModel.toughness)
                            {
                                requiredResult = 3;
                            }
                            if(strength  <= currentDefendingModel.toughness /2 )
                            {
                                requiredResult = 6;
                            }
                            else if(strength < currentDefendingModel.toughness)
                            {
                                requiredResult = 5;
                            }
                            if(woundRoll >= requiredResult)
                            {
                                currentMetricsOfAttacking.wounds += 1;
                            }
                        }







                        int requiredSaveRoll = currentDefendingModel.armorSave + currentMetricsOfAttacking.ap;


                        for(int e = 0; e < currentMetricsOfAttacking.wounds; e++)
                        {   int defenderWoundRoll = ThreadLocalRandom.current().nextInt(1, 6 +1 );
                            for(int w = 0; w < currentDefendingModel.listOfAbilites.size(); w++)
                            {
                                currentDefendingModel.listOfAbilites.get(w).saveRollAbility(defenderWoundRoll,currentMetricsOfAttacking);
                            }
                            if(defenderWoundRoll < requiredSaveRoll )
                            {
                                amountOfWoundsTotal += currentMetricsOfAttacking.damage;

                                currentDefendingModel.wounds -= currentMetricsOfAttacking.damage;

                                if(currentDefendingModel.wounds <= 0)
                                {
                                    z = z;
                                    amountOfModelsKilled +=1;

                                    if(!(currentDefendingModelInteger == defender.listOfModels.size() -1) )
                                    {   currentDefendingModelInteger +=1;
                                        currentDefendingModel = defender.listOfModels.get(currentDefendingModelInteger );
                                    }
                                }

                                //        if(currentModelDamage >= defender.numberOfWounds)
                                //        {
                                //            amountOfModelsKilled +=1;

                                //            currentModelDamage = 0;
                                //        }
                            }
                        }

                    }




                  //  Log.d("Resultat test:", "Hits: " + currentMetricsOfAttacking.hits + " Wounds" + currentMetricsOfAttacking.wounds);
                }




            }

            resultWoundsDealt.add(amountOfWoundsTotal);
            resultModelsSlain.add(amountOfModelsKilled);
        }

        float average = 0;
        float sum = 0;
        for(int result : resultWoundsDealt)
        {
            sum +=result;
        }
        float averageModelsKilled = 0;
        float anotherSum = 0;

        for(int result : resultModelsSlain)
        {
            anotherSum += result;
        }

        average = sum/10000;

        averageModelsKilled = anotherSum/10000;

        Log.d("Result", "Average amount of wounds: " + average);
        Log.d("Result", "Average amount of killed models: " + averageModelsKilled);
    }

    public void CalculateDamage(DataSheet attacker, DataSheet defender)
    {   //Random random = new Random();

        boolean printRequireHitdResult = true;
        boolean printRequiredWoundResult = true;
        boolean printRequiredSaveResult = true;

        int amountOfWoundsTotal;
        int amountOfModelsKilled;
        int currentModelDamage;
        ArrayList<Integer> resultWoundsDealt = new ArrayList<Integer>();
        ArrayList<Integer> resultModelsSlain = new ArrayList<Integer>();



        for(int z = 0; z < 10000; z++)
        {   amountOfWoundsTotal = 0;
            amountOfModelsKilled = 0;
            currentModelDamage = 0;
            for (int i = 0; i < attacker.NumberOfAttacks; i++)
            {


                int resultHitRoll;

                resultHitRoll = ThreadLocalRandom.current().nextInt(1, 6 +1 );
                if(attacker.hammerOfEmperor && resultHitRoll == 6)
                {
                    int resultSave = ThreadLocalRandom.current().nextInt(1, 6 +1 );

                    if(resultSave <= defender.armorSave + attacker.ap)
                    {
                        amountOfWoundsTotal +=1;
                        continue;
                    }
                }

                if(printRequireHitdResult)
                {
                    Log.d("Tester", "Vilken hitroll kravdes " + attacker.ballisticSkill);
                    printRequireHitdResult = false;
                }
                if(resultHitRoll >= attacker.ballisticSkill)
                {   int resultWoundRoll = ThreadLocalRandom.current().nextInt(1, 6 +1 );

                    int requiredResult = -1;
                    if(attacker.Strength == defender.toughness)
                    {
                        requiredResult = 4;
                    }
                    else if(attacker.Strength >= defender.toughness *2)
                    {
                        requiredResult = 2;
                    }
                    else if(attacker.Strength > defender.toughness)
                    {
                        requiredResult = 3;
                    }
                    if(attacker.Strength  <= defender.toughness /2 )
                    {
                        requiredResult = 6;
                    }
                    else if(attacker.Strength < defender.toughness)
                    {
                        requiredResult = 5;
                    }
                    if(printRequiredWoundResult)
                    {
                        Log.d("required Result", "vad ar required result wound" + requiredResult );
                        printRequiredWoundResult = false;
                    }


                    if(resultWoundRoll >= requiredResult)
                    {
                        int defenderWoundRoll = ThreadLocalRandom.current().nextInt(1, 6 +1 );

                        int requiredRoll = defender.armorSave + attacker.ap;

                        if(printRequiredSaveResult)
                        {
                            Log.d("tester: ", "Vad behover saveas" + requiredRoll);
                            printRequiredSaveResult = false;
                        }

                        if(defenderWoundRoll < requiredRoll)
                        {
                            amountOfWoundsTotal += attacker.damage;
                            currentModelDamage += attacker.damage;

                            if(currentModelDamage >= defender.numberOfWounds)
                            {
                                amountOfModelsKilled +=1;

                                currentModelDamage = 0;
                            }
                        }

                    }
                }
              //  Log.d("Mangden damage", "hur manga wounds " + amountOfWoundsTotal );

            }
            resultWoundsDealt.add(amountOfWoundsTotal);
            resultModelsSlain.add(amountOfModelsKilled);
        }

        Log.d("inget", "" +resultWoundsDealt.size());
        float average = 0;
        float sum = 0;
        for(int result : resultWoundsDealt)
        {
            sum +=result;
        }
        float averageModelsKilled = 0;
        float anotherSum = 0;

        for(int result : resultModelsSlain)
        {
            anotherSum += result;
        }

        average = sum/10000;

        averageModelsKilled = anotherSum/10000;

        Log.d("Result", "Average amount of wounds: " + average);
        Log.d("Result", "Average amount of killed models: " + averageModelsKilled);

    }



}
