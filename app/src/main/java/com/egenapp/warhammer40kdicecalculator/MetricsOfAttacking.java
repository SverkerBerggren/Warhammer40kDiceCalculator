package com.egenapp.warhammer40kdicecalculator;

public class MetricsOfAttacking {

    public int ap;
    public int damage;
    public int extraHits;
    public int mortalWounds;
    public int wounds;
    public MetricsOfAttacking(Integer diceRollResult, int ap, int damage, int mortalWounds, int wounds)
    {
        this.ap = ap;
        this.damage = damage;
        this.extraHits = diceRollResult;
        this.mortalWounds = mortalWounds;
        this.wounds = wounds;
    }

    public  MetricsOfAttacking()
    {

    }
}
