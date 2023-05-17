package com.egenapp.warhammer40kdicecalculator;

import android.util.Log;

import com.egenapp.warhammer40kdicecalculator.Abilities.Ability;
import com.egenapp.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.egenapp.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.egenapp.warhammer40kdicecalculator.DatasheetModeling.RangedWeapon;
import com.egenapp.warhammer40kdicecalculator.DatasheetModeling.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RollingLogic {


    public void TestOmFungerar() {
        Log.d(("hej"), "hejhej");
    }

    public RollResult newCalculateDamage(ArrayList<Unit> attackerList, Unit defender, Army attackingArmy, Army defendingArmy) {
        int amountOfWoundsTotal;
        int amountOfModelsKilled;
        int currentModelDamage;
        ArrayList<Integer> resultWoundsDealt = new ArrayList<Integer>();
        ArrayList<Integer> resultModelsSlain = new ArrayList<Integer>();




        //Army armyDefenderRules = null;
        Unit OriginalUnit = defender.copy();
        double averageAmountOfAttacks = 0;

        for (int z = 0; z < 10000; z++)
        {   Unit attacker;
            OriginalUnit = defender.copy();
            //  averageAmountOfAttacks = 0;
            amountOfWoundsTotal = 0;
            amountOfModelsKilled = 0;
            currentModelDamage = 0;
            int currentDefendingModelInteger = 0;
            Model currentDefendingModel = OriginalUnit.listOfModels.get(0);
            //  amountOfWoundsTotal = currentDefendingModel.wounds;

            for(int q = 0; q < attackerList.size(); q++)
            {
                attacker = attackerList.get(q);
                for (int i = 0; i < attacker.listOfModels.size(); i++)
                {
                    Model currentAttackingModel = attacker.listOfModels.get(i);
                    for (int f = 0; f < currentAttackingModel.listOfRangedWeapons.size(); f++) {
                        RangedWeapon currentWeapon = currentAttackingModel.listOfRangedWeapons.get(f);
                        int requiredBallisticSkill = currentAttackingModel.ballisticSkill;
                        int damage = currentWeapon.damageAmount.rawDamageAmount;
                        int ap = currentWeapon.ap;
                        int strength = currentWeapon.strength;

                        requiredBallisticSkill += CalculateModifierHitRoll(attackingArmy,attacker,defendingArmy,defender) * -1;

                        MetricsOfAttacking currentMetricsOfAttacking = new MetricsOfAttacking(0, ap, damage, 0, 0);
                        int amountOfAttacks = AmountOfAttacks(currentMetricsOfAttacking, attacker, currentWeapon, defender, currentAttackingModel);

                        averageAmountOfAttacks +=amountOfAttacks;

                        for (int p = 0; p < amountOfAttacks; p++) {

                            // Log.d(("Testar loopar: "),"Fungerar amount of attacks loopen loopen " + p);
                            DiceResult hitRoll = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));

                            HitRoll(currentMetricsOfAttacking, attackingArmy,attacker,currentWeapon,defendingArmy, defender, currentAttackingModel, hitRoll,requiredBallisticSkill);

                            for (int j = 0; j < currentMetricsOfAttacking.extraHits; j++) {
                                DiceResult woundRoll = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));

                                WoundRoll(currentMetricsOfAttacking, attacker, defender, currentAttackingModel, currentDefendingModel, woundRoll, currentWeapon);
                            }
                            int requiredSaveRoll = currentDefendingModel.armorSave - currentMetricsOfAttacking.ap;


                            for (int e = 0; e < currentMetricsOfAttacking.wounds; e++)
                            {
                                DiceResult defenderSaveRoll = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));
                                int damageToBeTaken = SaveRoll(currentMetricsOfAttacking,attacker,defender,currentAttackingModel,currentDefendingModel,currentWeapon,defenderSaveRoll,requiredSaveRoll);
                                amountOfWoundsTotal += damageToBeTaken;

                                currentModelDamage += damageToBeTaken;


                                if (currentDefendingModel.wounds <= currentModelDamage)
                                {

                                    amountOfModelsKilled += 1;

                                    if (!(currentDefendingModelInteger == defender.listOfModels.size() - 1))
                                    {
                                        currentDefendingModelInteger += 1;
                                        currentDefendingModel = defender.listOfModels.get(currentDefendingModelInteger);
                                    }
                                    currentModelDamage = 0;
                                }
                            }
                            currentMetricsOfAttacking.wounds = 0;
                            currentMetricsOfAttacking.extraHits = 0;
                        }
                    }
                }
            }
            resultWoundsDealt.add(amountOfWoundsTotal);
            resultModelsSlain.add(amountOfModelsKilled);
        }


        float average = 0;
        float sum = 0;
        for (int result : resultWoundsDealt) {
            sum += result;
        }
        float averageModelsKilled = 0;
        float anotherSum = 0;

        for (int result : resultModelsSlain) {
            anotherSum += result;
        }
        average = sum / 10000;
        averageModelsKilled = anotherSum / 10000;
        Log.d("Result", "Average amount of attacks: " + averageAmountOfAttacks / 10000);
        Log.d("Result", "Average amount of wounds: " + average);
        Log.d("Result", "Average amount of killed models: " + averageModelsKilled);
        RollResult returnResult = new RollResult();
        returnResult.modelsSlain = resultModelsSlain;
        returnResult.woundsDealt = resultWoundsDealt;
        returnResult.averageAmountOfWounds = average;
        return returnResult;
    }


    private int AmountOfAttacks(MetricsOfAttacking metrics, Unit currentAttackingUnit, RangedWeapon currentWeapon, Unit defendingUnit, Model currentAttackingModel) {
        int amountOfAttacks = 0;

        amountOfAttacks += currentWeapon.amountOfAttacks.rawNumberOfAttacks;
        List<DiceResult> amountOffAttacksRollD3 = new ArrayList<>();
        for (int p = 0; p < currentWeapon.amountOfAttacks.numberOfD3; p++) {
            DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 3 + 1));
            diceResult.isD3Roll = true;
            amountOffAttacksRollD3.add(diceResult);
        }
        if (currentWeapon.amountOfAttacks.numberOfD3 != 0) {
            for (int l = 0; l < currentAttackingModel.listOfAbilites.size(); l++) {
                currentAttackingModel.listOfAbilites.get(l).rollNumberOfShots(amountOffAttacksRollD3, metrics);
            }
        }

        for (int l = 0; l < amountOffAttacksRollD3.size(); l++) {
            amountOfAttacks += amountOffAttacksRollD3.get(l).result;
        }
        List<DiceResult> amountOffAttacksRollD6 = new ArrayList<>();
        for (int p = 0; p < currentWeapon.amountOfAttacks.numberOfD6; p++) {

            //  Log.d(("Testar loopar: "),"Fungerar vapen loopen loopen");

            DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));
            diceResult.isD6Roll = true;
            amountOffAttacksRollD6.add(diceResult);
        }
        if (currentWeapon.amountOfAttacks.numberOfD6 != 0) {
            for (int l = 0; l < currentAttackingModel.listOfAbilites.size(); l++) {
                currentAttackingModel.listOfAbilites.get(l).rollNumberOfShots(amountOffAttacksRollD6, metrics);
            }
        }
        for (int l = 0; l < amountOffAttacksRollD6.size(); l++) {
            amountOfAttacks += amountOffAttacksRollD6.get(l).result;
        }

        return amountOfAttacks;
    }

    private MetricsOfAttacking HitRoll(MetricsOfAttacking metrics,Army attackingArmy, Unit currentAttackingUnit,RangedWeapon attackingWeapon,Army defendingArmy, Unit defendingUnit, Model currentAttackingModel, DiceResult hitRoll,
                                       int requiredHit) {


        AbilitiesHitRoll(metrics,hitRoll,attackingArmy,defendingArmy,currentAttackingUnit,defendingUnit,attackingWeapon);


        if (hitRoll.result >= requiredHit) {
            metrics.extraHits += 1;
        }
        return metrics;
    }

    private int WoundRoll(MetricsOfAttacking metrics, Unit currentAttackingUnit, Unit defendingUnit, Model currentAttackingModel, Model defendingModel, DiceResult woundRoll, RangedWeapon weapon) {

        int amountOfWoundsDealt = 0;
        for (int m = 0; m < currentAttackingModel.listOfAbilites.size(); m++) {
            currentAttackingModel.listOfAbilites.get(m).woundRollAbility(woundRoll, metrics);
        }
        for (int m = 0; m < currentAttackingModel.listOfAbilites.size(); m++) {
            currentAttackingModel.listOfAbilites.get(m).woundRollAbility(woundRoll, metrics);
        }

        int requiredResult = -1;
        if (weapon.strength == defendingModel.toughness) {
            requiredResult = 4;
        } else if (weapon.strength >= defendingModel.toughness * 2) {
            requiredResult = 2;
        } else if (weapon.strength > defendingModel.toughness) {
            requiredResult = 3;
        }
        if (weapon.strength <= defendingModel.toughness / 2) {
            requiredResult = 6;
        } else if (weapon.strength < defendingModel.toughness) {
            requiredResult = 5;
        }
        if (woundRoll.result >= requiredResult) {
            metrics.wounds += 1;
            amountOfWoundsDealt+=1;
        }
        return amountOfWoundsDealt;
    }


    private int SaveRoll(MetricsOfAttacking metrics, Unit currentAttackingUnit, Unit defendingUnit, Model currentAttackingModel, Model currentDefendingModel, RangedWeapon weapon, DiceResult saveRoll, int requiredSaveRoll)
    {

        int damageToBeTaken = 0;
        for (int w = 0; w < currentDefendingModel.listOfAbilites.size(); w++) {
            currentDefendingModel.listOfAbilites.get(w).saveRollAbility(saveRoll, metrics);
        }
        if (saveRoll.result < requiredSaveRoll) {
            //amountOfWoundsTotal += currentWeapon.damageAmount.rawDamageAmount;
            damageToBeTaken = weapon.damageAmount.rawDamageAmount;
            for (int o = 0; o < weapon.damageAmount.d3DamageAmount; o++) {
                //  Log.d(("Testar loopar: "),"Fungerar vapen loopen loopen");
                DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));

                for (int c = 0; c < currentAttackingModel.listOfAbilites.size(); c++) {
                    currentAttackingModel.listOfAbilites.get(c).woundRollAbility(diceResult, metrics);
                }
                if (diceResult.result == 1 || diceResult.result == 2) {
                    damageToBeTaken += 1;
                }
                if (diceResult.result == 3 || diceResult.result == 4) {
                    damageToBeTaken += 2;
                }
                if (diceResult.result == 5 || diceResult.result == 6) {
                    damageToBeTaken += 3;
                }
            }
            for (int o = 0; o < weapon.damageAmount.d6DamageAmount; o++) {
                DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));
                for (int c = 0; c < currentAttackingModel.listOfAbilites.size(); c++) {
                    currentAttackingModel.listOfAbilites.get(c).woundRollAbility(diceResult, metrics);
                }
                damageToBeTaken += diceResult.result;
            }
        }
        return damageToBeTaken;
    }

    private void AmountOfAttacks()
    {

    }

    private void AbilitiesHitRoll(MetricsOfAttacking metrics, DiceResult diceResult,  Army attackingArmy, Army defendingArmy, Unit attackingUnit, Unit defendingUnit, RangedWeapon attackingWeapon)
    {
        for(Ability ability : attackingArmy.abilities)
        {
            ability.hitRollAbility(diceResult,metrics);
        }
        for(Ability ability : attackingUnit.listOfAbilitys)
        {
            ability.hitRollAbility(diceResult,metrics);
        }
        for(Ability ability : attackingWeapon.weaponRules)
        {
            ability.hitRollAbility(diceResult,metrics);
        }
        for(Ability ability : defendingArmy.abilities)
        {
            ability.hitRollAbility(diceResult,metrics);
        }
        for(Ability ability : defendingUnit.listOfAbilitys)
        {
            ability.hitRollAbility(diceResult,metrics);
        }

    }



    private int CalculateModifierHitRoll(Army attackerArmy, Unit attackingUnit, Army defendingArmy, Unit defendingUnit)
    {
        int modifierToReturn = 0;


        modifierToReturn += attackerArmy.ballisticSkillModifier;
        modifierToReturn += attackingUnit.ballisticSkillModifier;

        //abilities???


        if(modifierToReturn < -1)
        {
            modifierToReturn = -1;
        }
        if(modifierToReturn > 1)
        {
            modifierToReturn = 1;
        }

        return  modifierToReturn;

    }





    private int AbilitiesHitRollModifier(Army attackerArmy, Unit attackingUnit, Army defendingArmy, Unit defendingUnit)
    {
        int modifierToReturn = 0;

        for(Ability ability : attackerArmy.abilities)
        {

        }


        return modifierToReturn;
    }



}

