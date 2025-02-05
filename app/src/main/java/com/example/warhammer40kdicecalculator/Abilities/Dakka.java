package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

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