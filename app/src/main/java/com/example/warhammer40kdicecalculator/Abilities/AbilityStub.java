package com.example.warhammer40kdicecalculator.Abilities;

import com.example.warhammer40kdicecalculator.DiceResult;
import com.example.warhammer40kdicecalculator.MetricsOfAttacking;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AbilityStub extends Ability{
    public AbilityStub(String name)
    {
        super("");

        if(name.contains(": no behaviour"))
        {

            this.name = name;
        }
        else
        {
            this.name = name + ": no behaviour";
        }
    }
}
