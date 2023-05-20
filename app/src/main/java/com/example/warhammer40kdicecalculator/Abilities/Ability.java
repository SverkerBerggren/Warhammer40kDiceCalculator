package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DatasheetModeling.DeactivatableInterface;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Ability implements DeactivatableInterface {

    public String name = "";
    public boolean active = true;


    public abstract void hitRollAbilityAttacking(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult);

    public abstract void HitRollAbilityDefender(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult);

    public abstract void woundRollAbilityAttacker(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult);

    public abstract void woundRollAbilityDefender(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, AtomicInteger requiredResult);


    public abstract int saveRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking, int damageToBeTaken);

    public abstract void rollNumberOfShots(List<DiceResult> diceResult, MetricsOfAttacking metricsOfAttacking);

    public Ability(String name)
    {
        this.name = name;
    }


    public static Ability getAbilityType(String name)
    {
        if(name.equals("HammerOfTheEmperor"))
        {
            return new HammerOfTheEmperor();
        }

        if(name.equals("ReRollAmountOfHits"))
        {
            return new ReRollAmountOfHits();
        }

        if(name.equals("ReRollOnes"))
        {
            return new ReRollOnes();
        }
        if(name.equals("FeelNoPain6"))
        {
            return new FeelNoPain6();
        }
        if(name.equals("Blast"))
        {
            return new Blast();
        }
        if(name.equals("ReRollHits"))
        {
            return new ReRollHits();
        }

        if(name.equals("IncreaseAp1"))
        {
            return new IncreaseAp1();
        }
        if(name.equals("MinusOneToHit"))
        {
            return new MinusOneToHit();
        }
        if(name.equals("ReRollWoundRoll"))
        {
            return new ReRollWoundRoll();
        }
        if(name.equals("ReRollOnesWound"))
        {
            return new ReRollOnesWound();
        }

        return new AbilityStub(name );
    }


    public static ArrayList<Ability> getWeaponAbilities(String AbilitiesString)
    {
        ArrayList<Ability> abilitiesToReturn = new ArrayList<>();

        if(AbilitiesString.contains("Blast") ||  AbilitiesString.contains("blast"))
        {
            abilitiesToReturn.add(new Blast());
        }

        return abilitiesToReturn;
    }

    public  static void addModelAbility(Model ModelToParse,String AbilityName,String AbilityDescription)
    {
        ModelToParse.listOfAbilites.add(getAbilityType(AbilityName));
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ability ability = (Ability) o;
        return Objects.equals(name, ability.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, active);
    }

    @Override
    public void FlipActive() {
        active = !active;
    }
}
