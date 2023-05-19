package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DatasheetModeling.DeactivatableInterface;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Ability implements DeactivatableInterface {

    public String name = "";
    public boolean active = true;


    public abstract void hitRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking);

    public abstract void woundRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking);

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

        return new AbilityStub(name );
    }


    public static ArrayList<Ability> getWeaponAbilities(String AbilitiesString)
    {
        return new ArrayList<Ability>();
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
