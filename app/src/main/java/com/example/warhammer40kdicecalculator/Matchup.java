package com.example.warhammer40kdicecalculator;

import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;

public class Matchup {
    public String name;

    public Army friendlyArmy;

    public Army enemyArmy;

    public Matchup(String name, Army friendlyArmy,Army enemyArmy)
    {
        this.name = name;

        this.friendlyArmy = friendlyArmy;

        this.enemyArmy = enemyArmy;
    }
}
