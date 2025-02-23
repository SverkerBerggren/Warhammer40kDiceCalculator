package com.app.DamageCalculator40k.DamageCalculation;

public class DiceResult {

    public int result;

    public boolean isD6Roll = false;
    public boolean isD3Roll = false; // HELLLOOOOO

    // According to the rules a dice can never be re rolled more than once
    public boolean hasBeenReRolled = false;

    public DiceResult(int result)
    {
        this.result = result;
    }
}
