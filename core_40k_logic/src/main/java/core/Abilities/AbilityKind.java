package core.Abilities;

import java.util.Map;

import core.Abilities.GenericAbilities.MortalWoundOnHit;
import core.Abilities.GenericAbilities.ReRollHits;
import core.Abilities.GenericAbilities.ReRollOnes;
import core.Abilities.WeaponAbilities.AntiKeyword;
import core.Abilities.WeaponAbilities.Blast;
import core.Abilities.WeaponAbilities.Cleave;
import core.Abilities.WeaponAbilities.DevastatingWounds;
import core.Abilities.WeaponAbilities.LethalHits;
import core.Abilities.WeaponAbilities.Melta;
import core.Abilities.WeaponAbilities.RapidFire;
import core.Abilities.WeaponAbilities.SustainedHits;
import core.Abilities.WeaponAbilities.Torrent;
import core.Abilities.WeaponAbilities.TwinLinked;

public enum AbilityKind {
    LETHAL_HITS,
    MORTAL_WOUND_ON_HIT,
    RE_ROLL_ONES,
    RE_ROLL_HITS,
    DEVASTATING_WOUNDS,
    TORRENT,
    ANTI_KEYWORD,
    MELTA,
    BLAST,
    CLEAVE,
    RAPID_FIRE,
    SUSTAINED_HITS,
    TWIN_LINKED,

    UNIMPLEMENTED;

    public Class<? extends Ability> abilityClass() {
        return switch (this) {
            case LETHAL_HITS -> LethalHits.class;
            case MORTAL_WOUND_ON_HIT -> MortalWoundOnHit.class;
            case RE_ROLL_ONES -> ReRollOnes.class;
            case RE_ROLL_HITS -> ReRollHits.class;
            case TORRENT -> Torrent.class;
            case ANTI_KEYWORD -> AntiKeyword.class;
            case DEVASTATING_WOUNDS -> DevastatingWounds.class;
            case MELTA -> Melta.class;
            case BLAST -> Blast.class;
            case CLEAVE-> Cleave.class;
            case RAPID_FIRE -> RapidFire.class;
            case SUSTAINED_HITS -> SustainedHits.class;
            case TWIN_LINKED -> TwinLinked.class;
            case UNIMPLEMENTED -> UnimplementedAbility.class;
        };
    }
}