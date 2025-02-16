package com.app.DamageCalculator40k.Abilities;

public class Dakka extends Ability {
    public int dakkaAmount = -1;
  //  public Dakka()
  //  {
  //      super("Dakka");
  //  }
    public Dakka(int Amount)
    {
        super("Dakka" +Amount);
        dakkaAmount = Amount;
    }
}