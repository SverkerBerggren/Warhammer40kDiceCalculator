package DamageCalculation;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Doesn't test damage math. Tests that your *test suite* has adequate shape:
 * every rule you claim to cover (abilitiesUnderTest) must be exercised against
 * at least one "canary" profile from each dimension below at least once,
 * somewhere in the fixture set.
 *
 * This is what catches "I only ever tested devastating wounds against a
 * single-wound, no-invuln target" before it becomes a silent gap.
 */
public class FixtureCoverageTest {

    // The profile-shape canaries you've decided are worth guaranteeing coverage for.
    // Keep this list short and deliberate — it's a checklist, not an exhaustive
    // cross product. Add a new canary only when you've been bitten by a bug in
    // that dimension, or strongly suspect you could be.
    private static final List<String> REQUIRED_PROFILE_TAGS = List.of(
            "multiWound",
            "invulnSave",
            "feelNoPain",
            "variableDamage"   // D3/D6 damage characteristic, vs a fixed value
    );

    @Test
    @Disabled("Re-enable once base regressionMatchup suite is green - coverage gate isn't useful yet")
    void everyAbilityHasCoverageAgainstEveryCanaryProfile() throws IOException {
        List<DamageFixture> fixtures = loadAllFixtures();

        Set<String> allAbilitiesSeen = fixtures.stream()
                .flatMap(f -> safeList(f.abilitiesUnderTest()).stream())
                .collect(Collectors.toSet());

        List<String> gaps = new ArrayList<>();

        for (String ability : allAbilitiesSeen) {
            for (String requiredProfile : REQUIRED_PROFILE_TAGS) {
                boolean covered = fixtures.stream().anyMatch(f ->
                        safeList(f.abilitiesUnderTest()).contains(ability)
                                && safeList(f.profileTags()).contains(requiredProfile));

                if (!covered) {
                    gaps.add(ability + " x " + requiredProfile);
                }
            }
        }

        assertThat(gaps)
                .as("Missing rule x profile fixture coverage. Add a fixture tagged with " +
                        "both the ability and the profile for each gap listed:")
                .isEmpty();
    }

    private static List<String> safeList(List<String> list) {
        return list == null ? List.of() : list;
    }

    private static List<DamageFixture> loadAllFixtures() throws IOException {
        try (var paths = Files.list(Path.of("src/test/resources/fixtures"))) {
            return paths
                    .filter(p -> p.toString().endsWith(".json"))
                    .map(DamageFixture::loadFrom)
                    .collect(Collectors.toList());
        }
    }
}