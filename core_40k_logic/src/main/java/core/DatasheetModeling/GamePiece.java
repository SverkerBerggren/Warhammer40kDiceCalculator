package core.DatasheetModeling;

import core.Abilities.Ability;
import core.DamageCalculation.StatModifiers;

import java.util.ArrayList;


// Halv kasst namn walla
public class GamePiece {

    protected final ArrayList<Ability> abilities = new ArrayList<>();

    public ArrayList<Ability> GetAbilities()
    {
        return abilities;
    }

    public GamePiece toGamePieceSnapshot() {
        GamePiece piece = new GamePiece();
        piece.setStatModifiers(this.getStatModifiers());
        piece.GetAbilities().addAll(this.GetAbilities());
        return piece;
    }
    private StatModifiers statModifiers = new StatModifiers();

    public StatModifiers getStatModifiers() {
        return statModifiers;
    }
    public StatModifiers setStatModifiers(StatModifiers statModifiers) {
        return this.statModifiers = statModifiers;
    }
}
