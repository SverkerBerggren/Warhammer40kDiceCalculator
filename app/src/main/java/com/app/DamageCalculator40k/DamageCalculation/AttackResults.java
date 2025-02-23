package com.app.DamageCalculator40k.DamageCalculation;

import java.util.ArrayList;

public class AttackResults {

    public int ap = 0;
    public int attacks = 0;
    public int hits = 0;
    public int wounds = 0;
    public int mortalWounds = 0;
    public int damage = 0;
    public ArrayList<Integer> devastatingWoundsToPrevent = new ArrayList<>();
    //TODO: may need a more general solution
    public ArrayList<Integer> devastatingWounds = new ArrayList<>();
    public int MortalWoundsToBeDealt;
}
