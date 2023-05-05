package com.example.warhammer40kdicecalculator;

public class MetricsOfAttacking {

    public int ap;
    public int damage;
    public int hits;
    public int mortalWounds;
    public int wounds;
    public MetricsOfAttacking(Integer diceRollResult, int ap, int damage, int mortalWounds, int wounds)
    {
        this.ap = ap;
        this.damage = damage;
        this.hits = diceRollResult;
        this.mortalWounds = mortalWounds;
        this.wounds = wounds;
    }

    public  MetricsOfAttacking()
    {

    }
}
