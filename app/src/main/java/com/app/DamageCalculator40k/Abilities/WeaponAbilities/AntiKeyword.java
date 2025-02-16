package com.app.DamageCalculator40k.Abilities.WeaponAbilities;

import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Enums.Keyword;

public class AntiKeyword extends Ability {
    public static String baseName = "anti";
    private Keyword keyword;
    private int diceNumber = 0;
    public AntiKeyword(Keyword keyword, int diceNumber) {
        super(baseName + "-" + " " + keyword.name());
        this.keyword = keyword;
        this.diceNumber = diceNumber;
    }
}
