package core.DatasheetModeling;

import core.Abilities.Ability;
import core.DamageCalculation.StatModifiers;

import java.util.ArrayList;


// Halv kasst namn walla
public abstract class GamePiece {

    protected final ArrayList<Ability> abilities = new ArrayList<>();

    public ArrayList<Ability> GetAbilities()
    {
        return abilities;
    }


    private StatModifiers statModifiers = new StatModifiers();

    public StatModifiers getStatModifiers() {
        return statModifiers;
    }
    public StatModifiers setStatModifiers(StatModifiers statModifiers) {
        return this.statModifiers = statModifiers;
    }
}
