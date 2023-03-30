package com.example.warhammer40kdicecalculator;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RollingLogic {


    public void TestOmFungerar()
    {
        Log.d(("hej"),"hejhej");
    }

    public RollResult newCalculateDamage(ArrayList<Unit> attackerList, Unit defender)
    {
        int amountOfWoundsTotal;
        int amountOfModelsKilled;
        int currentModelDamage;
        ArrayList<Integer> resultWoundsDealt = new ArrayList<Integer>();
        ArrayList<Integer> resultModelsSlain = new ArrayList<Integer>();

        Unit attacker = attackerList.get(0);

        Unit OriginalUnit = defender.copy();
        double averageAmountOfAttacks = 0;
  //      for(int i = 0; i < defender.listOfModels.size();i++)
  //      {
  //
  //      }

        for(int z = 0; z < 10000; z++)
        {
            OriginalUnit = defender.copy();
          //  averageAmountOfAttacks = 0;
            amountOfWoundsTotal = 0;
            amountOfModelsKilled = 0;
            currentModelDamage = 0;
            int currentDefendingModelInteger = 0;

            Model currentDefendingModel = OriginalUnit.listOfModels.get(0);
          //  amountOfWoundsTotal = currentDefendingModel.wounds;
            for(int i = 0; i < attacker.listOfModels.size(); i++)
            {

                Model currentAttackingModel = attacker.listOfModels.get(i);

                for(int f = 0; f < currentAttackingModel.listOfRangedWeapons.size(); f++)
                {

                    RangedWeapon currentWeapon = currentAttackingModel.listOfRangedWeapons.get(f);
                    int requiredBallisticSkil= currentAttackingModel.ballisticSkill;
                    int damage = currentWeapon.damageAmount.rawDamageAmount;
                    int ap = currentWeapon.ap;
                    int strength = currentWeapon.strength;

                    MetricsOfAttacking currentMetricsOfAttacking = new MetricsOfAttacking(0, ap,damage, 0,0);
                    double amountOfAttacks = 0;

                    amountOfAttacks += currentWeapon.amountOfAttacks.rawNumberOfAttacks;
                    List<DiceResult> amountOffAttacksRollD3 = new ArrayList<>();
                    for( int p = 0; p < currentWeapon.amountOfAttacks.numberOfD3; p++)
                    {

                        //  Log.d(("Testar loopar: "),"Fungerar vapen loopen loopen");


                        DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 3 + 1));
                        diceResult.isD3Roll = true;
                        amountOffAttacksRollD3.add(diceResult);
                    }
                    if(currentWeapon.amountOfAttacks.numberOfD3 != 0)
                    {
                        for(int l = 0; l < currentAttackingModel.listOfAbilites.size(); l++)
                        {
                            currentAttackingModel.listOfAbilites.get(l).rollNumberOfShots(amountOffAttacksRollD3,currentMetricsOfAttacking);
                        }
                    }

                    for(int l = 0; l < amountOffAttacksRollD3.size(); l++)
                    {
                        amountOfAttacks += amountOffAttacksRollD3.get(l).result;
                    }
                    List<DiceResult> amountOffAttacksRollD6 = new ArrayList<>();
                    for( int p = 0; p < currentWeapon.amountOfAttacks.numberOfD6; p++)
                    {

                        //  Log.d(("Testar loopar: "),"Fungerar vapen loopen loopen");

                        DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 +1 ));
                        diceResult.isD6Roll= true;
                        amountOffAttacksRollD6.add(diceResult);
                    }
                    if(currentWeapon.amountOfAttacks.numberOfD6 != 0)
                    {
                        for (int l = 0; l < currentAttackingModel.listOfAbilites.size(); l++) {
                            currentAttackingModel.listOfAbilites.get(l).rollNumberOfShots(amountOffAttacksRollD6, currentMetricsOfAttacking);
                        }
                    }
                    for(int l = 0; l < amountOffAttacksRollD6.size(); l++)
                    {
                        amountOfAttacks += amountOffAttacksRollD6.get(l).result;
                    }
                    averageAmountOfAttacks += amountOfAttacks;


                    for(int p = 0; p < amountOfAttacks; p++)
                    {

                       // Log.d(("Testar loopar: "),"Fungerar amount of attacks loopen loopen " + p);
                        DiceResult hitRoll = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 +1 ));

                        for(int k = 0; k < attacker.listOfAbilitys.size(); k++)
                        {
                            attacker.listOfAbilitys.get(k).hitRollAbility(hitRoll,currentMetricsOfAttacking);
                        }

                        if(hitRoll.result >= requiredBallisticSkil)
                        {
                            currentMetricsOfAttacking.hits += 1;
                        }

                        for(int j = 0; j < currentMetricsOfAttacking.hits; j++)
                        {
                            DiceResult woundRoll = new DiceResult( ThreadLocalRandom.current().nextInt(1, 6 +1 ));
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
                            if(woundRoll.result >= requiredResult)
                            {
                                currentMetricsOfAttacking.wounds += 1;
                            }
                        }







                        int requiredSaveRoll = currentDefendingModel.armorSave - currentMetricsOfAttacking.ap;


                        for(int e = 0; e < currentMetricsOfAttacking.wounds; e++)
                        {   DiceResult defenderWoundRoll = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 +1 ));
                            for(int w = 0; w < currentDefendingModel.listOfAbilites.size(); w++)
                            {
                                currentDefendingModel.listOfAbilites.get(w).saveRollAbility(defenderWoundRoll,currentMetricsOfAttacking);
                            }
                            if(defenderWoundRoll.result < requiredSaveRoll )
                            {
                                //amountOfWoundsTotal += currentWeapon.damageAmount.rawDamageAmount;
                                int damageToBeTaken = currentWeapon.damageAmount.rawDamageAmount;
                                for( int o = 0; o < currentWeapon.damageAmount.d3DamageAmount; o++)
                                {
                                    //  Log.d(("Testar loopar: "),"Fungerar vapen loopen loopen");
                                    DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 +1 ));

                                    for(int c = 0; c < currentAttackingModel.listOfAbilites.size(); c++)
                                    {
                                       currentAttackingModel.listOfAbilites.get(c).woundRollAbility(diceResult,currentMetricsOfAttacking);
                                    }
                                    if(diceResult.result ==1 || diceResult.result == 2)
                                    {
                                        damageToBeTaken += 1;
                                    }
                                    if(diceResult.result ==3 || diceResult.result == 4)
                                    {
                                        damageToBeTaken += 2;
                                    }
                                    if(diceResult.result ==5 || diceResult.result == 6)
                                    {
                                        damageToBeTaken += 3;
                                    }
                                }
                                for( int o = 0; o < currentWeapon.damageAmount.d6DamageAmount; o++)
                                {


                                    DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 +1 ));
                                    for(int c = 0; c < currentAttackingModel.listOfAbilites.size(); c++)
                                    {
                                       currentAttackingModel.listOfAbilites.get(c).woundRollAbility(diceResult,currentMetricsOfAttacking);
                                    }
                                    damageToBeTaken += diceResult.result;
                                }
                                amountOfWoundsTotal += damageToBeTaken;

                                currentModelDamage +=damageToBeTaken;



                                if(currentDefendingModel.wounds <= currentModelDamage)
                                {
                                    z = z;
                                    amountOfModelsKilled +=1;

                                    if(!(currentDefendingModelInteger == defender.listOfModels.size() -1) )
                                    {   currentDefendingModelInteger +=1;
                                        currentDefendingModel = defender.listOfModels.get(currentDefendingModelInteger );
                                    }
                                    currentModelDamage = 0;
                                }

                                //        if(currentModelDamage >= defender.numberOfWounds)
                                //        {
                                //            amountOfModelsKilled +=1;

                                //            currentModelDamage = 0;
                                //        }
                            }
                        }
                        currentMetricsOfAttacking.wounds = 0;
                        currentMetricsOfAttacking.hits = 0;
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
        Log.d("Result", "Average amount of attacks: " + averageAmountOfAttacks/10000);
        Log.d("Result", "Average amount of wounds: " + average);
        Log.d("Result", "Average amount of killed models: " + averageModelsKilled);
        RollResult returnResult = new RollResult();
        returnResult.modelsSlain = resultModelsSlain;
        returnResult.woundsDealt = resultWoundsDealt;

        return returnResult;


    }





}
