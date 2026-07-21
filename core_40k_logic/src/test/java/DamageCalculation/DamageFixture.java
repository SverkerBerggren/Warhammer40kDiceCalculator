package DamageCalculation;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import core.Conditions;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import core.Conditions;

public class DamageFixture {
    private String name;
    private String attackerUnitsFile;
    private String defenderUnitFile;
    private String attackerGamePiece;
    private String defenderGamePiece;
    private Conditions conditions;          // swap for your real Conditions type
    private double expectedMeanDamage;
    private double expectedModelsKilled;
    private int referenceSampleSize;
    private String source;
    private List<String> abilitiesUnderTest;   // rule axis: "devastatingWounds", "lethalHits", ...
    private List<String> profileTags;          // profile axis: "multiWound", "invulnSave", "feelNoPain", ...

    // Gson needs a no-arg constructor
    public DamageFixture() {}

    public static DamageFixture loadFrom(Path path) {
        try {
            String json = Files.readString(path);
            return new Gson().fromJson(json, DamageFixture.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load fixture: " + path, e);
        }
    }

    public String name() { return name; }
    public String attackerUnitFile() { return attackerUnitsFile; }
    public String defenderUnitFile() { return defenderUnitFile; }
    public String attackerGamePiece() { return attackerGamePiece; }
    public String defenderGamePiece() { return defenderGamePiece; }
    public Conditions conditions() { return conditions; }
    public double expectedMeanDamage() { return expectedMeanDamage; }
    public int referenceSampleSize() { return referenceSampleSize; }
    public String source() { return source; }
    public List<String> abilitiesUnderTest() { return abilitiesUnderTest; }
    public List<String> profileTags() { return profileTags; }

    @Override
    public String toString() { return name; } // this is what shows in the test report
}