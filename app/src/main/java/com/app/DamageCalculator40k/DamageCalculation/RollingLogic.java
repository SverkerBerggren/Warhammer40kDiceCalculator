package com.app.DamageCalculator40k.DamageCalculation;

import android.os.Trace;
import android.util.Log;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Abilities.WeaponAbilities.DevastatingWounds;
import com.app.DamageCalculator40k.Conditions;
import com.app.DamageCalculator40k.DatasheetModeling.Army;
import com.app.DamageCalculator40k.DatasheetModeling.DiceAmount;
import com.app.DamageCalculator40k.DatasheetModeling.Model;
import com.app.DamageCalculator40k.DatasheetModeling.Weapon;
import com.app.DamageCalculator40k.DatasheetModeling.Unit;
import com.app.DamageCalculator40k.Enums.AbilityTiming;
import com.app.DamageCalculator40k.Enums.StatModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class RollingLogic {
    private Conditions conditionen;

    private enum WoundType
    {
        NormalWound,
        MortalWound,
        DevastatingWound
    }
    private int amountOfHits = 0;
    private int amountOfAttacksDebug = 0;
    private int amountOfWoundsTotal = 0;
    private int currentModelDamage = 0;
    private int amountOfModelsKilled = 0;
    private int currentDefendingModelIndex = 0;
    private Model defendingModel;

    public RollResult newCalculateDamage(ArrayList<Unit> attackerList, Unit defendingUnit, Army attackingArmy, Army defendingArmy, Conditions condtitions) {
        //Debug
        int[] resultsAmountOfHits = new int[10000];
        int[] resultsAmountOfAttacks = new int[10000];

        int[] resultWoundsDealt = new int[10000];
        int[] resultModelsSlain = new int[10000];
        conditionen = condtitions;

        AbilitySources attackingAbilitySources = new AbilitySources(attackingArmy);
        AbilitySources defendingAbilitySources = new AbilitySources(defendingArmy);
        defendingAbilitySources.unit = defendingUnit;

        StatModifiers attackingStatModifiers = new StatModifiers();
        StatModifiers defendingStatModifiers = new StatModifiers();

        Trace.beginSection("loopen");
        for (int attackSequenceCount = 0; attackSequenceCount < 10000; attackSequenceCount++)
        {
            //Debug
            amountOfHits = 0;
            amountOfAttacksDebug = 0;


            amountOfWoundsTotal = 0;
            amountOfModelsKilled = 0;
            currentModelDamage = 0;
            int currentDefendingModelIndex = 0;

            defendingModel = defendingUnit.listOfModels.get(0);
            defendingAbilitySources.model = defendingModel;
            SetStatModifiers(defendingArmy,defendingUnit,defendingStatModifiers);


            for(Unit attackingUnit : attackerList )
            {
                attackingAbilitySources.unit = attackingUnit;

                int mortalWounds = 0;
                //TODO: ar detta snabbare?
                ArrayList<Integer> devastatingWounds = new ArrayList<>();
                for (Model attackingModel : attackingUnit.listOfModels)
                {
                    if(!attackingModel.active)
                    {
                        continue;
                    }

                    attackingAbilitySources.model = attackingModel;

                    SetStatModifiers(attackingArmy,attackingUnit,attackingStatModifiers);

                    for (Weapon attackingWeapon : attackingModel.weapons)
                    {
                        if(ShouldSkipWeapon(attackingWeapon,condtitions))
                        {
                            continue;
                        }
                        attackingAbilitySources.weapon = attackingWeapon;

                        int requiredHitRoll = attackingWeapon.ballisticSkill - attackingStatModifiers.GetModifier(StatModifier.HitRoll);

                        if(requiredHitRoll < 2)
                        {
                            requiredHitRoll = 2;
                        }

                        int amountOfAttacks = AmountOfAttacks(attackingAbilitySources,attackingWeapon);
                        amountOfAttacksDebug += amountOfAttacks;


                        for (int attackCount = 0; attackCount < amountOfAttacks; attackCount++) {
                            AttackResults attackResults = new AttackResults();
                            DiceResult hitRoll = CreateD6Result();
                            HitRoll(attackResults,attackingAbilitySources,defendingAbilitySources,hitRoll,requiredHitRoll);

                            amountOfHits += attackResults.hits;

                            for (int woundRollCount = 0; woundRollCount < attackResults.hits; woundRollCount++) {
                                DiceResult woundRoll = CreateD6Result();
                                WoundRoll(attackResults, attackingAbilitySources,defendingAbilitySources,woundRoll,defendingStatModifiers);
                            }
                            int requiredSaveRoll = defendingModel.armorSave - attackResults.ap - attackingWeapon.ap - defendingStatModifiers.GetModifier(StatModifier.ArmorSave);
                            int invulnerableSave = defendingModel.invulnerableSave - defendingStatModifiers.GetModifier(StatModifier.InvulnerableSave);

                            if(requiredSaveRoll > invulnerableSave && !(invulnerableSave > 6))
                            {
                                requiredSaveRoll = defendingModel.invulnerableSave;
                            }

                            for (int saveRollCount = 0; saveRollCount < attackResults.wounds; saveRollCount++)
                            {
                                DiceResult defenderSaveRoll = CreateD6Result();
                                if (defenderSaveRoll.result < requiredSaveRoll) {

                                    ApplyDamage(RollDiceAmount(attackingWeapon.damageAmount),defenderSaveRoll,WoundType.NormalWound,attackResults,attackingAbilitySources,defendingAbilitySources,requiredSaveRoll);
                                }
                            }
                            mortalWounds += attackResults.mortalWounds;
                            devastatingWounds.addAll(attackResults.devastatingWounds);
                        }
                    }
                }
                if(mortalWounds > 0)
                {
                    //Yikes med create d6 result och required roll result
                    ApplyDamage(mortalWounds,CreateD6Result(),WoundType.MortalWound,new AttackResults(),attackingAbilitySources,defendingAbilitySources,7);
                }
                if(!devastatingWounds.isEmpty())
                {
                    for(Integer integer : devastatingWounds)
                    {
                        ApplyDamage(integer,CreateD6Result(),WoundType.DevastatingWound,new AttackResults(),attackingAbilitySources,defendingAbilitySources,7);
                    }
                }
            }
            resultWoundsDealt[attackSequenceCount] = amountOfWoundsTotal;
            resultModelsSlain[attackSequenceCount] = amountOfModelsKilled;
            resultsAmountOfHits[attackSequenceCount] = amountOfHits;
            resultsAmountOfAttacks[attackSequenceCount] = amountOfAttacksDebug;
        }
        Trace.endSection();



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

        float amountOfAttacks = 0;
        for(int attacks : resultsAmountOfAttacks)
        {
            amountOfAttacks += attacks;
        }

        float amountOfHitsFloat = 0;
        for(int hits : resultsAmountOfHits)
        {
            amountOfHitsFloat += hits;
        }

        average = sum / 10000;
        averageModelsKilled = anotherSum / 10000;
        Log.d("Result:", " Average amount of attacks: " + (amountOfAttacks/10000));
        Log.d("Result:", " Average amount of hits: " + (amountOfHitsFloat/10000));
        Log.d("Result", "Average amount of wounds: " + average);
        Log.d("Result", "Average amount of killed models: " + averageModelsKilled);
        RollResult returnResult = new RollResult();
        returnResult.modelsSlain = resultModelsSlain;
        returnResult.woundsDealt = resultWoundsDealt;
        returnResult.averageAmountOfWounds = average;
        returnResult.averageAmountOfModelsSlain = averageModelsKilled;
        return returnResult;
    }

    private void ApplyDamage(int damage, DiceResult diceResult, WoundType woundType, AttackResults attackResults, AbilitySources attackingAbilities,AbilitySources defendingAbilities,int requiredSaveRoll )
    {
        attackResults.damage = damage;

        if(woundType == WoundType.NormalWound)
        {
            defendingAbilities.ApplyAbility(AbilityTiming.ReduceDamageCharacteristic,diceResult,attackResults,attackingAbilities,defendingAbilities,requiredSaveRoll,conditionen);
        }

        if(woundType == WoundType.DevastatingWound || woundType == WoundType.MortalWound)
        {
            defendingAbilities.ApplyAbility(AbilityTiming.PreventMortalWoundDamage,diceResult,attackResults,attackingAbilities,defendingAbilities,requiredSaveRoll,conditionen);
        }

        amountOfWoundsTotal += damage;
        if(woundType == WoundType.MortalWound)
        {
            int mortalWoundsLeft = damage;
            while (mortalWoundsLeft > 0)
            {
                int mortalWoundsToDeal = (defendingModel.wounds - currentModelDamage);
                if(mortalWoundsToDeal > mortalWoundsLeft)
                {
                    mortalWoundsToDeal = mortalWoundsLeft;
                }
                if(mortalWoundsToDeal == (defendingModel.wounds - currentModelDamage))
                {
                    currentModelDamage = 0;
                    amountOfModelsKilled += 1;
                }
                else
                {
                    currentModelDamage += mortalWoundsToDeal;
                }
                mortalWoundsLeft -= mortalWoundsToDeal;
            }
        }
        else
        {
            currentModelDamage += damage;

            if (defendingModel.wounds <= currentModelDamage)
            {
                amountOfModelsKilled += 1;

                if (!(currentDefendingModelIndex == defendingAbilities.unit.listOfModels.size() - 1))
                {
                    currentDefendingModelIndex += 1;
                    defendingModel = defendingAbilities.unit.listOfModels.get(currentDefendingModelIndex);
                    defendingAbilities.model = defendingModel;
                }
                currentModelDamage = 0;
            }
        }
    }
    private DiceResult CreateD6Result()
    {
        DiceResult ret = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));
        ret.isD6Roll = true;
        return ret;
    }

    private DiceResult CreateD3Result()
    {
        DiceResult ret = new DiceResult(ThreadLocalRandom.current().nextInt(1, 3 + 1));
        ret.isD3Roll = true;
        return ret;
    }

    // Assumes that there are no defending units that can modify the attack amount of a unit
    private int AmountOfAttacks( AbilitySources attackerAbilities,Weapon weapon) {
        int amountOfAttacks = 0;

        amountOfAttacks += weapon.amountOfAttacks.baseAmount;
        List<DiceResult> amountOffAttacksRollD3 = new ArrayList<>();
        for (int d3Count = 0; d3Count < weapon.amountOfAttacks.numberOfD3; d3Count++) {
            DiceResult diceResult = CreateD3Result();
            amountOffAttacksRollD3.add(diceResult);
        }
        if (weapon.amountOfAttacks.numberOfD3 != 0)
        {
            for (Ability ability : attackerAbilities.model.GetAbilities())
            {
                ability.rollNumberOfShots(amountOffAttacksRollD3);
            }
            for (Ability ability : attackerAbilities.unit.GetAbilities())
            {
                ability.rollNumberOfShots(amountOffAttacksRollD3 );
            }
            for (Ability ability : attackerAbilities.army.GetAbilities())
            {
                ability.rollNumberOfShots(amountOffAttacksRollD3 );
            }
            for (Ability ability : attackerAbilities.weapon.GetAbilities())
            {
                ability.rollNumberOfShots(amountOffAttacksRollD3 );
            }
        }

        for (int l = 0; l < amountOffAttacksRollD3.size(); l++) {
            amountOfAttacks += amountOffAttacksRollD3.get(l).result;
        }
        List<DiceResult> amountOffAttacksRollD6 = new ArrayList<>();
        for (int p = 0; p < weapon.amountOfAttacks.numberOfD6; p++) {

            //  Log.d(("Testar loopar: "),"Fungerar vapen loopen loopen");

            DiceResult diceResult = new DiceResult(ThreadLocalRandom.current().nextInt(1, 6 + 1));
            diceResult.isD6Roll = true;
            amountOffAttacksRollD6.add(diceResult);
        }

        if (weapon.amountOfAttacks.numberOfD6 != 0)
        {
            for (Ability ability : attackerAbilities.model.GetAbilities())
            {
                ability.rollNumberOfShots(amountOffAttacksRollD6);
            }
            for (Ability ability : attackerAbilities.unit.GetAbilities())
            {
                ability.rollNumberOfShots(amountOffAttacksRollD6);
            }
            for (Ability ability : attackerAbilities.army.GetAbilities())
            {
                ability.rollNumberOfShots(amountOffAttacksRollD6);
            }
            for (Ability ability : attackerAbilities.weapon.GetAbilities())
            {
                ability.rollNumberOfShots(amountOffAttacksRollD6);
            }
        }
        for (int l = 0; l < amountOffAttacksRollD6.size(); l++) {
            amountOfAttacks += amountOffAttacksRollD6.get(l).result;
        }

        return amountOfAttacks;
    }

    private void HitRoll(AttackResults attackResults, AbilitySources attackingAbilities, AbilitySources defendingAbilities, DiceResult hitRoll, int requiredHit) {

        attackingAbilities.ApplyAbility(AbilityTiming.ReRollHits, hitRoll, attackResults, attackingAbilities, defendingAbilities,requiredHit,conditionen);
        attackingAbilities.ApplyAbility(AbilityTiming.TriggerOnHitRoll, hitRoll, attackResults, attackingAbilities, defendingAbilities,requiredHit,conditionen);

        if (hitRoll.result >= requiredHit) {
            attackResults.hits += 1;
        }
    }

    private void WoundRoll(AttackResults attackResults, AbilitySources attackingAbilities, AbilitySources defendingAbilities, DiceResult woundRoll, StatModifiers defendingModifiers) {

        int requiredResult = 0;
        Weapon weapon = attackingAbilities.weapon;
        int toughness = defendingAbilities.model.toughness + defendingModifiers.GetModifier(StatModifier.Toughness);
        if (weapon.strength ==  toughness) {
            requiredResult = 4;
        } else if (weapon.strength >= toughness *2) {
            requiredResult = 2;
        } else if (weapon.strength > toughness) {
            requiredResult = 3;
        }
        if (weapon.strength <= toughness / 2) {
            requiredResult = 6;
        } else if (weapon.strength < defendingAbilities.model.toughness) {
            requiredResult = 5;
        }
        attackingAbilities.ApplyAbility(AbilityTiming.ReRollWounds, woundRoll, attackResults, attackingAbilities, defendingAbilities,requiredResult,conditionen);
        attackingAbilities.ApplyAbility(AbilityTiming.TriggerOnWoundRoll, woundRoll, attackResults, attackingAbilities, defendingAbilities,requiredResult,conditionen);

        if (woundRoll.result >= requiredResult) {
            attackResults.wounds += 1;
        }
    }
    //TODO: hitta ett battre namespace
    public static int RollDiceAmount(DiceAmount diceAmount)
    {
        int ret = 0;
        ret += diceAmount.baseAmount;
        for(int i = 0; i < diceAmount.numberOfD3; i++)
        {
            ret += ThreadLocalRandom.current().nextInt(1,4);
        }
        for(int i = 0; i < diceAmount.numberOfD6; i++)
        {
            ret += ThreadLocalRandom.current().nextInt(1,7);
        }
        return  ret;
    }

    // TODO: Refactor so it inherits instead
    private void SetStatModifiers(Army army, Unit unit, StatModifiers statModifiers)
    {
        statModifiers.SetStatModifier(StatModifier.Toughness, (short) (army.toughnessModifier + unit.toughnessModifier ) );
        statModifiers.SetStatModifier(StatModifier.Strength, (short) (army.strengthModifier + unit.strengthModifier));
        statModifiers.SetStatModifier(StatModifier.ArmorSave, (short) (army.armorSaveModifier + unit.armorSaveModifier));
        statModifiers.SetStatModifier(StatModifier.InvulnerableSave, (short) (army.invulnerableSaveModifier + unit.invulnerableSaveModifier));
        statModifiers.SetStatModifier(StatModifier.WoundAmount, (short) (army.woundsModifier + unit.woundsModifier));
        statModifiers.SetStatModifier(StatModifier.Attacks, (short) (army.attacksModifier + unit.attacksModifier));
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

