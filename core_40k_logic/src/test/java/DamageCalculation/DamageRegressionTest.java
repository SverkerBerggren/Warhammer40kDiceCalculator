package DamageCalculation;

import core.Abilities.Ability;
import core.AbilityElementAdapter;
import core.Conditions;
import core.DamageCalculation.RollResult;
import core.DamageCalculation.RollingLogic;
import core.DatasheetModeling.GamePiece;
import core.DatasheetModeling.Unit;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;
import DamageCalculation.DamageFixture;
import core.Util.CustomMath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class DamageRegressionTest {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Ability.class, new AbilityElementAdapter())
            .create();

    @ParameterizedTest(name = "{0}")
    @MethodSource("loadFixtures")
    void regressionMatchup(DamageFixture fixture) {
        ArrayList<Unit> attacker = loadAttackingUnits(fixture.attackerUnitFile());
        Unit defender = LoadDefendingUnit(fixture.defenderUnitFile());

        RollingLogic rollingLogic = new RollingLogic();

        Conditions conditions = fixture.conditions();

        RollResult result = rollingLogic.newCalculateDamage(attacker, defender, new GamePiece(), new GamePiece(), fixture.conditions(), fixture.referenceSampleSize());

        double sampleVariance = CustomMath.sampleVariance(result.woundsDealt);

        double se = Math.sqrt(sampleVariance);
        assertThat(result.averageAmountOfWounds)
                .isCloseTo(fixture.expectedMeanDamage(), within(4 * se));
    }

    static Stream<DamageFixture> loadFixtures() throws IOException {
        return Files.list(Path.of("src/test/resources/fixtures"))
                .filter(p -> p.toString().endsWith(".json"))
                .map(DamageFixture::loadFrom);
    }

    // placeholder — wire up to your real unit-loading utility
    private static ArrayList<Unit> loadAttackingUnits(String path) {

        InputStream is = DamageFixture.class.getClassLoader().getResourceAsStream(path);

        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            String stringToReturn = new Scanner(reader).useDelimiter("\\A").next();

            Type listType = new TypeToken<ArrayList<Unit>>() {
            }.getType();
            return gson.fromJson(stringToReturn, listType);
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    private static Unit LoadDefendingUnit(String path) {
        InputStream is = DamageFixture.class.getClassLoader().getResourceAsStream(path);

        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            String stringToReturn = new Scanner(reader).useDelimiter("\\A").next();

            return gson.fromJson(stringToReturn, Unit.class);
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return new Unit();
    }
}