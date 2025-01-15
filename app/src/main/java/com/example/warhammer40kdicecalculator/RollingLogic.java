package com.example.warhammer40kdicecalculator;

import android.util.Log;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.Abilities.Dakka;
import com.example.warhammer40kdicecalculator.Abilities.IncreaseAp1;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Weapon;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class RollingLogic {


    public void TestOmFungerar() {
        Log.d(("hej"), "hejhej");
    }



    private Conditions conditionen;

    public RollResult newCalculateDamage(ArrayList<Unit> attackerList, Unit defender, Army attackingArmy, Army defendingArmy, Conditions condtitions) {
        int amountOfWoundsTotal;
        int amountOfModelsKilled;
        int currentModelDamage;
        ArrayList<Integer> resultWoundsDealt = new ArrayList<Integer>();
        ArrayList<Integer> resultModelsSlain = new ArrayList<Integer>();
        conditionen = condtitions;

        //Army armyDefenderRules = null;

        Unit OriginalUnit = defender.Copy();
        ArrayList<Unit> originalAttackers = new ArrayList<>();
        for(Unit unit : attackerList)
        {
            originalAttackers.add(unit.Copy());
        }

        ArrayList<Unit> currentAttackers;

        double averageAmountOfAttacks = 0;

        for (int z = 0; z < 10000; z++)
        {   Unit attacker;
            OriginalUnit = defender.Copy();

            //  averageAmountOfAttacks = 0;
            amountOfWoundsTotal = 0;
            amountOfModelsKilled = 0;
            currentModelDamage = 0;
            int currentDefendingModelInteger = 0;
            Model currentDefendingModel = OriginalUnit.listOfModels.get(0);
            //  amountOfWoundsTotal = currentDefendingModel.wounds;
            currentAttackers = new ArrayList<>();
            for(Unit unit : originalAttackers)
            {
                currentAttackers.add(unit.Copy());
            }
            for(int q = 0; q < currentAttackers.size(); q++)
            {
                attacker = currentAttackers.get(q);


                for (int i = 0; i < attacker.listOfModels.size(); i++)
                {
                    Model currentAttackingModel = attacker.listOfModels.get(i);
                    if(!currentAttackingModel.active)
                    {
                        continue;
                    }
                    AddAllModifiersAttacker(attackingArmy,attacker,currentAttackingModel);
                    for (int f = 0; f < currentAttackingModel.listOfRangedWeapons.size(); f++)
                    {
                        Weapon currentWeapon = currentAttackingModel.listOfRangedWeapons.get(f);

                        if(ShouldSkipWeapon(currentWeapon,condtitions))
                        {
                            continue;
                        }

                        if(currentWeapon.isMelee)
                        {
                            currentWeapon.amountOfAttacks.baseAmount = currentAttackingModel.attacks;

                            if(currentWeapon.strength == -2)
                            {
                                currentWeapon.strength = currentAttackingModel.strength *2;
                            }
                            else
                            {
                                currentWeapon.strength += currentAttackingModel.strength;
                            }
                        }

                        int requiredHitRoll = currentWeapon.ballisticSkill;

                        int damage = currentWeapon.damageAmount.baseAmount;
                        int ap = currentWeapon.ap;
                        int strength = currentWeapon.strength;

                    //    requiredHitRoll += CalculateModifierHitRoll(attackingArmy,attacker,defendingArmy,defender) * -1;

                        if(requiredHitRoll < 2)
                            requiredHitRoll = 2;

                        if(condtitions.rapidFireRange )
                        {
                            for(Ability ability : currentWeapon.weaponRules)
                            {
                                if( ability.name.contains("Rapid Fire"))
                                {
                                    currentWeapon.amountOfAttacks.baseAmount *=2;
                                }
                            }
                        }
                        if(condtitions.dakkaHalfRange )
                        {
                            for(Ability ability : currentWeapon.weaponRules)
                            {
                                if( ability.name.contains("Dakka"))
                                {
                                    currentWeapon.amountOfAttacks.baseAmount = ((Dakka)ability).dakkaAmount;
                                }
                            }
                        }


                        CheckWeaponConditions(currentWeapon,condtitions);
                        for(Ability ability : attacker.listOfAbilitys)
                        {
                            if(ability instanceof IncreaseAp1)
                            {
                                currentWeapon.ap -=1;
                            }
                        }
                        MetricsOfAttacking currentMetricsOfAttacking = new MetricsOfAttacking(0, currentWeapon.ap, damage, 0, 0);
                        int amountOfAttacks = AmountOfAttacks(currentMetricsOfAttacking, attacker, currentWeapon, defender, currentAttackingModel);

                        averageAmountOfAttacks +=amountOfAttacks;


                        for (int p = 0; p < amountOfAttacks; p++) {

                            // Log.d(("Testar loopar: "),"Fungerar amount of attacks loopen loopen " + p);
                            DiceResult hitRoll = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));

                            HitRoll(currentMetricsOfAttacking, attackingArmy,attacker,currentWeapon,defendingArmy, defender, currentAttackingModel,currentDefendingModel, hitRoll,new AtomicInteger(requiredHitRoll));

                            for (int j = 0; j < currentMetricsOfAttacking.extraHits; j++) {
                                DiceResult woundRoll = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));

                                WoundRoll(currentMetricsOfAttacking, attackingArmy,defendingArmy,attacker, defender, currentAttackingModel, currentDefendingModel, woundRoll, currentWeapon);
                            }
                            int requiredSaveRoll = currentDefendingModel.armorSave - currentMetricsOfAttacking.ap;
                            if(requiredSaveRoll > currentDefendingModel.invulnerableSave && currentDefendingModel.invulnerableSave != 7)
                            {
                                requiredSaveRoll = currentDefendingModel.invulnerableSave;
                            }

                            for (int e = 0; e < currentMetricsOfAttacking.wounds; e++)
                            {
                                DiceResult defenderSaveRoll = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));
                                int damageToBeTaken = SaveRoll(currentMetricsOfAttacking,attackingArmy,attacker,defendingArmy,defender,currentAttackingModel,currentDefendingModel,currentWeapon,defenderSaveRoll,requiredSaveRoll);



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
        returnResult.averageAmountOfModelsSlain = averageModelsKilled;
        return returnResult;
    }


    private void CheckWeaponConditions(Weapon weapon, Conditions conditions)
    {

        for(Ability ability : weapon.weaponRules)
        {
            if((ability.name.contains("Heavy") || ability.name.contains("Grenade")) && conditions.devastatorDoctrine)
            {
                weapon.ap -= 1;
            }
            if((ability.name.contains("Rapid Fire") || ability.name.contains("Assault")) && conditions.tacticalDoctrine)
            {
                weapon.ap -= 1;
            }
            if((ability.name.contains("Pistol") )&& conditions.assaultDoctrine)
            {
                weapon.ap -= 1;
            }
        }


        if(conditions.assaultDoctrine && weapon.isMelee)
        {
            weapon.ap -=1;
        }


    }

    private int AmountOfAttacks(MetricsOfAttacking metrics, Unit currentAttackingUnit, Weapon currentWeapon, Unit defendingUnit, Model currentAttackingModel) {
        int amountOfAttacks = 0;

        amountOfAttacks += currentWeapon.amountOfAttacks.baseAmount;
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

    private MetricsOfAttacking HitRoll(MetricsOfAttacking metrics, Army attackingArmy, Unit currentAttackingUnit, Weapon attackingWeapon, Army defendingArmy, Unit defendingUnit, Model currentAttackingModel,
                                       Model defendingModel, DiceResult hitRoll,
                                       AtomicInteger requiredHit) {



        AbilitiesHitRollDefender(metrics,hitRoll,defendingArmy,defendingUnit,defendingModel,requiredHit);
        AbilitiesHitRollAttacker(metrics,hitRoll,attackingArmy, currentAttackingModel,defendingArmy,currentAttackingUnit,defendingUnit,attackingWeapon, requiredHit);


        if (hitRoll.result >= requiredHit.get()) {
            metrics.extraHits += 1;
        }
        return metrics;
    }

    private int WoundRoll(MetricsOfAttacking metrics,Army attackingArmy, Army defendingArmy,  Unit currentAttackingUnit, Unit defendingUnit, Model currentAttackingModel, Model defendingModel, DiceResult woundRoll, Weapon weapon) {

        int amountOfWoundsDealt = 0;


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

        if(conditionen.plusOneToWound)
        {
            requiredResult-=1;
        }

        AtomicInteger atomicInteger = new AtomicInteger(requiredResult);

        AbilitiesWoundRollDefender(metrics,woundRoll, attackingArmy,  currentAttackingModel,defendingModel,  defendingArmy,  currentAttackingUnit,  defendingUnit, weapon , atomicInteger);
        AbilitiesWoundRollAttacker(metrics,woundRoll, attackingArmy,  currentAttackingModel,  defendingArmy,  currentAttackingUnit,  defendingUnit, weapon , atomicInteger);


        requiredResult = atomicInteger.get();

        if (woundRoll.result >= requiredResult) {
            metrics.wounds += 1;
            amountOfWoundsDealt+=1;
        }
        return amountOfWoundsDealt;
    }


    private int SaveRoll(MetricsOfAttacking metrics, Army attackingArmy, Unit currentAttackingUnit, Army defendingArmy, Unit defendingUnit, Model currentAttackingModel, Model currentDefendingModel, Weapon weapon, DiceResult saveRoll, int requiredSaveRoll)
    {

        int damageToBeTaken = 0;

        if (saveRoll.result < requiredSaveRoll) {
            //amountOfWoundsTotal += currentWeapon.damageAmount.rawDamageAmount;
            damageToBeTaken = weapon.damageAmount.baseAmount;
            for (int o = 0; o < weapon.damageAmount.numberOfD3; o++) {
                //  Log.d(("Testar loopar: "),"Fungerar vapen loopen loopen");
                DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));


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
            for (int o = 0; o < weapon.damageAmount.numberOfD6; o++) {
                DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));

                damageToBeTaken += diceResult.result;
            }
        }

        damageToBeTaken = SaveRollAbilities(metrics,attackingArmy,defendingArmy,currentAttackingUnit,defendingUnit,weapon,damageToBeTaken);

        return damageToBeTaken;
    }

    private void AmountOfAttacks()
    {

    }

    private void AbilitiesHitRollAttacker(MetricsOfAttacking metrics, DiceResult diceResult, Army attackingArmy, Model currentAttackingModel, Army defendingArmy, Unit attackingUnit, Unit defendingUnit, Weapon attackingWeapon, AtomicInteger requiredResult)
    {
        for(Ability ability : attackingArmy.abilities)
        {
            ability.hitRollAbilityAttacking(diceResult,metrics, requiredResult);
        }
        for(Ability ability : attackingUnit.listOfAbilitys)
        {
            ability.hitRollAbilityAttacking(diceResult,metrics, requiredResult);
        }
        for(Ability ability : currentAttackingModel.listOfAbilites)
        {
            ability.hitRollAbilityAttacking(diceResult,metrics, requiredResult);
        }
        for(Ability ability : attackingWeapon.weaponRules)
        {
            ability.hitRollAbilityAttacking(diceResult,metrics, requiredResult);
        }
    }
    private void AbilitiesWoundRollAttacker(MetricsOfAttacking metrics, DiceResult diceResult, Army attackingArmy, Model currentAttackingModel, Army defendingArmy, Unit attackingUnit, Unit defendingUnit, Weapon attackingWeapon, AtomicInteger requiredResult)
    {
        for(Ability ability : attackingArmy.abilities)
        {
            ability.woundRollAbilityAttacker(diceResult,metrics, requiredResult);
        }
        for(Ability ability : attackingUnit.listOfAbilitys)
        {
            ability.woundRollAbilityAttacker(diceResult,metrics, requiredResult);
        }
        for(Ability ability : currentAttackingModel.listOfAbilites)
        {
            ability.woundRollAbilityAttacker(diceResult,metrics, requiredResult);
        }
        for(Ability ability : attackingWeapon.weaponRules)
        {
            ability.woundRollAbilityAttacker(diceResult,metrics, requiredResult);
        }
    }
    private void AbilitiesWoundRollDefender(MetricsOfAttacking metrics, DiceResult diceResult, Army attackingArmy, Model currentAttackingModel, Model defendingModel, Army defendingArmy, Unit attackingUnit, Unit defendingUnit, Weapon attackingWeapon, AtomicInteger requiredResult)
    {
        for(Ability ability : defendingArmy.abilities)
        {
            ability.woundRollAbilityDefender(diceResult,metrics, requiredResult);
        }
        for(Ability ability : defendingUnit.listOfAbilitys)
        {
            ability.woundRollAbilityDefender(diceResult,metrics, requiredResult);
        }
        for(Ability ability : defendingModel.listOfAbilites)
        {
            ability.woundRollAbilityDefender(diceResult,metrics, requiredResult);
        }

    }
    private void AbilitiesHitRollDefender(MetricsOfAttacking metrics, DiceResult diceResult, Army defendingArmy, Unit defendingUnit, Model defendingModel, AtomicInteger requiredResult)
    {
        for(Ability ability : defendingArmy.abilities)
        {
            ability.HitRollAbilityDefender(diceResult,metrics, requiredResult);
        }
        for(Ability ability : defendingUnit.listOfAbilitys)
        {
            ability.HitRollAbilityDefender(diceResult,metrics, requiredResult);
        }
        for(Ability ability : defendingModel.listOfAbilites)
        {
            ability.HitRollAbilityDefender(diceResult,metrics, requiredResult);
        }

    }

    private int SaveRollAbilities(MetricsOfAttacking metrics, Army attackingArmy, Army defendingArmy, Unit attackingUnit, Unit defendingUnit, Weapon attackingWeapon, int damageToBeTaken)
    {
        DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));


        int damageToReduce = 0;


        for(Ability ability : defendingArmy.abilities)
        {
           damageToReduce += ability.saveRollAbility(diceResult,metrics, damageToBeTaken);
        }
        for(Ability ability : defendingUnit.listOfAbilitys)
        {
            damageToReduce+= ability.saveRollAbility(diceResult,metrics, damageToBeTaken);
        }

        return damageToBeTaken -damageToReduce;
    }



    private void AddAllModifiersAttacker(Army attackingArmy, Unit currentAttackingUnit, Model currentAttackingModel)
    {

        currentAttackingModel.toughness += attackingArmy.toughnessModifier +  currentAttackingUnit.toughnessModifier;
        currentAttackingModel.strength += attackingArmy.strengthModifier +  currentAttackingUnit.strengthModifier;
        currentAttackingModel.armorSave -= attackingArmy.armorSaveModifier +  currentAttackingUnit.armorSaveModifier;
        currentAttackingModel.invulnerableSave -= attackingArmy.invulnerableSaveModifier +  currentAttackingUnit.invulnerableSaveModifier;
        currentAttackingModel.wounds += attackingArmy.woundsModifier +  currentAttackingUnit.woundsModifier;
        currentAttackingModel.attacks +=  attackingArmy.attacksModifier +  currentAttackingUnit.attacksModifier;
    }



    private void AddAllModifiersDefender(Army defendingArmy, Unit defendingUnit, Model defendingModel)
    {

    }




    private int CalculateModifierHitRoll(Army attackerArmy, Unit attackingUnit, Army defendingArmy, Unit defendingUnit)
    {
        int modifierToReturn = 0;


        modifierToReturn += attackerArmy.hitSkill;
        modifierToReturn += attackingUnit.hitSkill;

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



    private boolean ShouldSkipWeapon(Weapon rangedWeapon, Conditions conditions)
    {


        if(!rangedWeapon.active)
            return true;

        if(rangedWeapon.isMelee && !conditions.meleeCombat)
            return true;

        if(!rangedWeapon.isMelee && !conditions.rangedCombat)
            return true;

        return false;
    }


}

