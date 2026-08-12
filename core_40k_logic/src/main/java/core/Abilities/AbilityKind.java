package core.Abilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import core.DatasheetModeling.DiceAmount;
import core.Enums.Keyword;
import core.Logging.Logging;

public enum AbilityKind {

    LETHAL_HITS(
            List.of("Lethal Hits"),
            "Lethal Hits",
            "Critical hits auto-wound.",
            List.of(),
            false,
            args -> new LethalHits()),

    MORTAL_WOUND_ON_HIT(
            List.of("Mortal Wound on Hit"),
            "Mortal Wound on Hit",
            "Deals mortal wounds on a hit roll.",
            List.of(new ParamSpec("resultToTriggerOn",ParamType.INT,"Mortal wound on hit")),
            false,
            args -> new MortalWoundOnHit((int)args.get(0))),

    RE_ROLL_ONES(
            List.of("Re-roll Ones", "Reroll 1s"),
            "Re-roll Ones",
            "Re-roll hit or wound rolls of 1.",
            List.of(),
            false,
            args -> new ReRollOnes()),

    RE_ROLL_HITS(
            List.of("Re-roll Hits"),
            "Re-roll Hits",
            "Re-roll failed hit rolls.",
            List.of(),
            false,
            args -> new ReRollHits()),

    DEVASTATING_WOUNDS(
            List.of("Devastating Wounds"),
            "Devastating Wounds",
            "Mortal wounds on critical hits.",
            List.of(),
            false,
            args -> new DevastatingWounds()),

    TORRENT(
            List.of("Torrent"),
            "Torrent",
            "Automatically hits, no hit roll required.",
            List.of(),
            false,
            args -> new Torrent()),

    ANTI_KEYWORD(
            List.of("Anti"),
            "Anti-Keyword",
            "Wounds on a fixed roll against a specific keyword.",
            List.of(new ParamSpec("keyword", ParamType.KEYWORD, "Keyword"),
                    new ParamSpec("woundThreshold", ParamType.INT, "Threshold")),
            false,
            args -> new AntiKeyword( Keyword.fromString((String)args.get(0)), (int) args.get(1))),

    MELTA(
            List.of("Melta"),
            "Melta",
            "Extra wounds depending on range.",
            List.of(new ParamSpec("extraWounds", ParamType.INT, "extra wounds")),
            true,
            args -> new Melta((int) args.get(0))),

    BLAST(
            List.of("Blast"),
            "Blast",
            "Extra hits depending on size of defending unit.",
            List.of(new ParamSpec("extraAttacks", ParamType.INT, "extra attacks")),
            false,
            args -> new Blast((int) args.get(0))),

    CLEAVE(
            List.of("Cleave"),
            "Cleave",
            "Extra hits depending on size of defending unit.",
            List.of(new ParamSpec("extraAttacks", ParamType.INT, "extra attacks")),
            false,
            args -> new Cleave((int) args.get(0))),

    RAPID_FIRE(
            List.of("Rapid Fire"),
            "Rapid fire",
            "Extra hits when in half range.",
            List.of(new ParamSpec("extraAttacks", ParamType.DICE_AMOUNT, "extra attacks")),
            true,
            args -> new RapidFire((DiceAmount) args.get(0))),

    SUSTAINED_HITS(
            List.of("Sustained Hits"),
            "Sustained hits",
            "Extra hits on critical hits.",
            List.of(new ParamSpec("extraHits", ParamType.DICE_AMOUNT, "extra hits")),
            false,
            args -> new SustainedHits((DiceAmount) args.get(0))),

    TWIN_LINKED(
            List.of("Twin Linked"),
            "Twin Linked",
            "Reroll wound roll.",
            List.of(),
            false,
            args -> new TwinLinked()),

    UNIMPLEMENTED(
            List.of(),
            "Unimplemented",
            "Placeholder for an ability not yet supported by the parser.",
            List.of(),
            false,
            args -> { throw new UnsupportedOperationException("Cannot construct UNIMPLEMENTED ability."); });


    private final List<String> aliases;
    private final AbilityDefinition definition;
    private final AbilityFactory factory;

    AbilityKind(List<String> aliases, String displayName, String description,
                List<ParamSpec> params, boolean isRangeGated, AbilityFactory factory) {
        this.aliases = aliases;
        this.definition = new AbilityDefinition(this, displayName, description, params, isRangeGated);
        this.factory = factory;
    }

    public Ability construct(List<String> rawParams) {
        List<ParamSpec> specs = definition.params();
        if (rawParams.size() != specs.size()) {
            throw new IllegalArgumentException(
                    "%s expects %d params, got %d".formatted(name(), specs.size(), rawParams.size()));
        }

        List<Object> typedArgs = new ArrayList<>(specs.size());
        for (int i = 0; i < specs.size(); i++) {
            typedArgs.add(coerce(specs.get(i), rawParams.get(i)));
        }
        return factory.create(typedArgs);
    }
    private static Object coerce(ParamSpec spec, String raw) {
        try {
            return switch (spec.type()) {
                case INT -> Integer.parseInt(raw.trim());
                case BOOLEAN -> Boolean.parseBoolean(raw.trim());
                case KEYWORD -> raw.trim();
                case DICE_AMOUNT -> DiceAmount.Parse(raw.trim()); // e.g. "D6", "D3+1"
            };
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Param '%s' expected %s, got '%s'".formatted(spec.fieldName(), spec.type(), raw), e);
        }
    }


    public List<String> aliases() { return aliases; }
    public AbilityDefinition definition() { return definition; }

    private static final Map<String, AbilityKind> ALIAS_TO_KIND =
            Arrays.stream(AbilityKind.values())
                    .flatMap(k -> k.aliases().stream().map(alias -> Map.entry(alias, k)))
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

    private static final List<AbilityDefinition> AbilityDefinitionCatalog =
           Arrays.stream(AbilityKind.values()).map(abilityKind -> abilityKind.definition).toList();

    public static  List<AbilityDefinition> getAbilityDefinitionCatalog()
    {
        return AbilityDefinitionCatalog;
    }
    public static AbilityKind fromAlias(String alias) {
        AbilityKind kind = ALIAS_TO_KIND.get(alias);
        if (kind == null) {
            throw new IllegalArgumentException("Unknown ability alias: " + alias);
        }
        return kind;
    }

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
    public String abilityName() {
        return switch (this) {
            case LETHAL_HITS -> LethalHits.baseName;
            case MORTAL_WOUND_ON_HIT -> MortalWoundOnHit.baseName;
            case RE_ROLL_ONES -> ReRollOnes.baseName;
            case RE_ROLL_HITS -> ReRollHits.baseName;
            case TORRENT -> Torrent.baseName;
            case ANTI_KEYWORD -> AntiKeyword.baseName;
            case DEVASTATING_WOUNDS -> DevastatingWounds.baseName;
            case MELTA -> Melta.baseName;
            case BLAST -> Blast.baseName;
            case CLEAVE-> Cleave.baseName;
            case RAPID_FIRE -> RapidFire.baseName;
            case SUSTAINED_HITS -> SustainedHits.baseName;
            case TWIN_LINKED -> TwinLinked.baseName;
            case UNIMPLEMENTED -> "unimplemented";
        };
    }

    public static Optional<Ability> getAbility(String name, List<String> paramValues) {
        try{
            return ALIAS_TO_KIND.get(name) == null
                    ? Optional.empty()
                    : Optional.of(ALIAS_TO_KIND.get(name).construct(paramValues));
        }
        catch (IllegalArgumentException e)
        {
            if(!name.equals("Blast"))
            {
                Logging.d("Ability construction","Incorrectly constructed ability " +name + " " + paramValues);
            }
            return Optional.empty();
        }
    }
    public interface AbilityFactory {
        Ability create(List<Object> args);
    }
}