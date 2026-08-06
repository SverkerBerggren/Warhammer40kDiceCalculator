package core.Abilities;

import java.util.List;
import java.util.function.Supplier;
public record AbilityDefinition(
        AbilityKind kind,
        String displayName,
        String description,
        List<ParamSpec> params,
        boolean hasBoosted
) {}