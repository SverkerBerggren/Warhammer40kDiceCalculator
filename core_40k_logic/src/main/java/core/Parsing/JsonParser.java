package core.Parsing;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import core.Abilities.Ability;
import core.Abilities.UnimplementedAbility;
import core.DatabaseManager;
import core.DatasheetModeling.Model;
import core.DatasheetModeling.Unit;
import core.DatasheetModeling.Weapon;
import core.Enums.Faction;
import core.Enums.Keyword;
import core.Logging.Logging;
import core.Util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class JsonParser {

    public final HashMap<DatabaseManager.NameFactionKey, Unit> nameFactionToUnit = new HashMap<>();
    public final HashMap<String, Unit> nameToUnit = new HashMap<>();
    public final HashMap<String, Unit> idToUnit = new HashMap<>();
    public final HashMap<DatabaseManager.NameFactionKey, Model> nameFactionToModel = new HashMap<>();
    public final HashMap<String, Model> nameToModel = new HashMap<>();
    public final HashMap<String, Model> idToModel = new HashMap<>();
    public final HashMap<String, ArrayList<Weapon>> idToWeapon = new HashMap<>();
    public final HashMap<String, ArrayList<Weapon>> nameToWeapon = new HashMap<>();
    public final HashMap<DatabaseManager.NameFactionUnitKey, ArrayList<Weapon>> nameFactionUnitToWeapon = new HashMap<>();
    public final HashMap<String, Ability> nameToAbility = new CaseInsensitiveMap<>();
    public final HashMap<String, Ability> idToAbility = new HashMap<>();

    private final Gson gson = new Gson();

    public class CaseInsensitiveMap<V> extends HashMap<String, V> {
        @Override
        public V put(String key, V value) {
            return super.put(key.toLowerCase(), value);
        }

        @Override
        public V remove(Object key) {
            return super.remove(((String) key).toLowerCase());
        }

        @Override
        public V get(Object key) {
            return super.get(((String) key).toLowerCase());
        }

        @Override
        public boolean containsKey(Object key) {
            return super.containsKey(((String) key).toLowerCase());
        }
    }

    // ======================= JSON schema POJOs =======================
    // Only what the parser actually reads is modeled. Deliberately left out:
    // constraints / modifiers / conditions — XmlParser never read these either.
    // Verify these field names against a FULL (untruncated) sample file before trusting
    // them blindly; the sample you pasted was cut off mid "profiles" array.

    interface Entry {
        String getId();
        String getName();
        String getType();
        List<Profile> getProfiles();
        List<SelectionEntry> getSelectionEntries();
        List<SelectionEntryGroup> getSelectionEntryGroups();
        List<EntryLink> getEntryLinks();
    }

    static class CatalogueFile {
        Catalogue catalogue;
    }

    static class Catalogue {
        String id;
        String name;
        List<SelectionEntry> sharedSelectionEntries;
        List<SelectionEntry> selectionEntries; // present defensively; may not exist in library catalogues
        List<Profile> sharedProfiles;
    }

    static class SelectionEntry implements Entry {
        String id;
        String name;
        String type; // "unit" | "model" | "upgrade"
        Boolean hidden;
        List<CategoryLink> categoryLinks;
        List<Cost> costs;
        List<Profile> profiles;
        List<SelectionEntry> selectionEntries;
        List<SelectionEntryGroup> selectionEntryGroups;
        List<EntryLink> entryLinks;
        List<InfoLink> infoLinks;
        List<InfoGroup> infoGroups;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getType() { return type; }
        public List<Profile> getProfiles() { return profiles; }
        public List<SelectionEntry> getSelectionEntries() { return selectionEntries; }
        public List<SelectionEntryGroup> getSelectionEntryGroups() { return selectionEntryGroups; }
        public List<EntryLink> getEntryLinks() { return entryLinks; }
    }

    static class SelectionEntryGroup implements Entry {
        String id;
        String name;
        List<Profile> profiles;
        List<SelectionEntry> selectionEntries;
        List<SelectionEntryGroup> selectionEntryGroups;
        List<EntryLink> entryLinks;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getType() { return null; } // groups have no "unit/model/upgrade" type
        public List<Profile> getProfiles() { return profiles; }
        public List<SelectionEntry> getSelectionEntries() { return selectionEntries; }
        public List<SelectionEntryGroup> getSelectionEntryGroups() { return selectionEntryGroups; }
        public List<EntryLink> getEntryLinks() { return entryLinks; }
    }

    // Per BattleScribe 2.02+, entryLinks can carry the same children as selectionEntries
    static class EntryLink implements Entry {
        String id;
        String name;
        String targetId;
        String type;
        Boolean hidden;
        List<Profile> profiles;
        List<SelectionEntry> selectionEntries;
        List<SelectionEntryGroup> selectionEntryGroups;
        List<EntryLink> entryLinks;

        public String getId() { return id; }
        public String getName() { return name; }
        public String getType() { return type; }
        public List<Profile> getProfiles() { return profiles; }
        public List<SelectionEntry> getSelectionEntries() { return selectionEntries; }
        public List<SelectionEntryGroup> getSelectionEntryGroups() { return selectionEntryGroups; }
        public List<EntryLink> getEntryLinks() { return entryLinks; }
    }

    static class InfoLink {
        String id;
        String name;
        String targetId;
        String type; // "profile"
        Boolean hidden;
    }

    static class InfoGroup {
        String id;
        String name;
        List<Profile> profiles;
        List<InfoLink> infoLinks;
    }

    static class Profile {
        String id;
        String name;
        String typeId;
        String typeName; // "Unit" | "Abilities" | "Ranged Weapons" | "Melee Weapons"
        Boolean hidden;
        List<Characteristic> characteristics;
    }

    static class Characteristic {
        String name;
        String typeId;
        @SerializedName("$text")
        String text;
    }

    static class CategoryLink {
        String id;
        String targetId;
        String name;
        Boolean primary;
        Boolean hidden;
    }

    static class Cost {
        String name;
        String typeId;
        double value;
    }

    // ======================= Entry-point =======================

    // TODO: temporar skas andras trust (kept the sentiment from the XML version)
    public void FillDatabase(ArrayList<Pair<String, Faction>> jsonDocs) {
        ArrayList<Pair<Catalogue, Faction>> catalogues = new ArrayList<>();
        for (Pair<String, Faction> filePair : jsonDocs) {
            Catalogue catalogue = parseJson(filePair.first);
            if (catalogue == null) {
                Logging.d("Knas i parse", "Failed to parse catalogue JSON for faction " + filePair.second);
                continue;
            }
            catalogues.add(new Pair<>(catalogue, filePair.second));
        }

        for (Pair<Catalogue, Faction> pair : catalogues) {
            CreateSharedModelStats(pair.first, pair.second); // builds idToModel from sharedProfiles
        }

        for (Pair<Catalogue, Faction> pair : catalogues) {
            List<SelectionEntry> topLevel = allTopLevelEntries(pair.first);
            Faction faction = pair.second;
            for (SelectionEntry entry : topLevel) IndexAbilitySelections(entry);
            for (SelectionEntry entry : topLevel) IndexAllWeapons(entry, faction);
            for (SelectionEntry entry : topLevel) IndexAllModels(entry, faction, null);
            for (SelectionEntry entry : topLevel) IndexAllUnits(entry, faction);
        }

        // Debug (unchanged from XmlParser)
        Map<String, Integer> duplicateUnits = new HashMap<>();
        idToUnit.forEach((key, value) -> {
            idToUnit.forEach((otherKey, otherValue) -> {
                if (!key.equals(otherKey) && value.unitName.equals(otherValue.unitName)) {
                    Integer duplicateValue = duplicateUnits.get(otherValue.unitName);
                    if (duplicateValue == null) {
                        duplicateUnits.put(otherValue.unitName, 1);
                    } else {
                        duplicateUnits.put(otherValue.unitName, duplicateValue + 1);
                    }
                }
            });
        });
    }

    private Catalogue parseJson(String json) {
        try {
            CatalogueFile file = gson.fromJson(json, CatalogueFile.class);
            return file != null ? file.catalogue : null;
        } catch (Exception e) {
            Logging.d("Knas i parse", "abow");
            e.printStackTrace();
            return null;
        }
    }

    // sharedSelectionEntries is the one confirmed container in your sample; selectionEntries is
    // included defensively in case a catalogue also defines root-level (non-shared) entries.
    private List<SelectionEntry> allTopLevelEntries(Catalogue catalogue) {
        List<SelectionEntry> combined = new ArrayList<>();
        if (catalogue.sharedSelectionEntries != null) combined.addAll(catalogue.sharedSelectionEntries);
        if (catalogue.selectionEntries != null) combined.addAll(catalogue.selectionEntries);
        return combined;
    }

    // ======================= Shared traversal helpers =======================

    private List<Entry> childEntries(Entry entry) {
        List<Entry> result = new ArrayList<>();
        if (entry.getSelectionEntries() != null) result.addAll(entry.getSelectionEntries());
        if (entry.getSelectionEntryGroups() != null) result.addAll(entry.getSelectionEntryGroups());
        if (entry.getEntryLinks() != null) result.addAll(entry.getEntryLinks());
        return result;
    }

    // Mirrors XmlParser's GetFirstNodeOfTypeRecursively(node, "profiles", null, null):
    // stop at the first entry in the subtree that HAS a profiles list, don't keep
    // searching past it even if it doesn't contain what the caller is looking for.
    private List<Profile> findProfilesList(Entry entry) {
        if (entry == null) return null;
        if (entry.getProfiles() != null && !entry.getProfiles().isEmpty()) {
            return entry.getProfiles();
        }
        for (Entry child : childEntries(entry)) {
            List<Profile> found = findProfilesList(child);
            if (found != null) return found;
        }
        return null;
    }

    private Profile findProfileByTypeName(Entry entry, String typeName) {
        List<Profile> profiles = findProfilesList(entry);
        if (profiles == null) return null;
        for (Profile p : profiles) {
            if (typeName.equalsIgnoreCase(p.typeName)) return p;
        }
        return null;
    }

    private String findCharacteristic(List<Characteristic> chars, String name) {
        if (chars == null) return null;
        for (Characteristic c : chars) {
            if (name.equalsIgnoreCase(c.name)) return c.text;
        }
        return null;
    }

    // ======================= Stat / dice parsing (unchanged from XmlParser) =======================

    int p_ParseUnitStat(String statString) {
        if (statString == null || statString.trim().isEmpty()
                || statString.equals("-") || statString.equals("N/A")) {
            return 0;
        }
        String firstPart = statString.split("/")[0];
        String cleaned = firstPart.replaceAll("\\+|\\*|[a-zA-Z]+|\\s+", "").trim();
        if (cleaned.isEmpty()) return 0;
        try {
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ======================= Abilities =======================

    private boolean IsValidAbility(Ability ability) {
        if (ability == null) return false;
        return ability.name != null && !ability.name.isEmpty();
    }

    private void IndexAbilitySelections(Entry entry) {
        if (entry == null) return;

        if (entry instanceof SelectionEntry) {
            SelectionEntry se = (SelectionEntry) entry;
            if ("upgrade".equalsIgnoreCase(se.type) && se.profiles != null) {
                for (Profile profile : se.profiles) {
                    if ("Abilities".equals(profile.typeName)) {
                        Ability ability = parseAbilityFromProfile(profile);
                        if (IsValidAbility(ability)) {
                            if (se.id != null) idToAbility.put(se.id, ability);
                            nameToAbility.put(ability.name, ability);
                        }
                    }
                }
            }
        }

        for (Entry child : childEntries(entry)) {
            IndexAbilitySelections(child);
        }
    }

    private Ability parseAbilityFromProfile(Profile profile) {
        if (profile.name == null) return null;
        String description = findCharacteristic(profile.characteristics, "Description");
        UnimplementedAbility ability = new UnimplementedAbility(profile.name);
        ability.description = description != null ? description : "";
        return ability;
    }

    private ArrayList<Ability> ParseAbilitiesFromSubtree(Entry entry) {
        ArrayList<Ability> result = new ArrayList<>();
        collectAbilities(entry, result);
        return result;
    }

    private void collectAbilities(Entry entry, ArrayList<Ability> result) {
        if (entry == null) return;

        if (entry.getProfiles() != null) {
            for (Profile profile : entry.getProfiles()) {
                if ("Abilities".equals(profile.typeName)) {
                    Ability ability = parseAbilityFromProfile(profile);
                    if (ability != null) {
                        result.add(ability);
                    }
                }
            }
        }

        if (entry instanceof EntryLink) {
            EntryLink link = (EntryLink) entry;
            if (link.targetId != null) {
                Ability ability = idToAbility.get(link.targetId);
                if (ability != null) {
                    result.add(ability);
                }
            }
        }

        for (Entry child : childEntries(entry)) {
            collectAbilities(child, result);
        }
    }

    private String claudeGetKeywordDescription(String keyword) {
        String baseName = keyword.replaceAll("\\s+\\d+$", "").trim();
        switch (baseName) {
            case "Blast":
                return "Add 1 to the Attacks for every 5 models in the target unit.";
            case "Rapid Fire":
                return "If target is within half range, increase Attacks by the Rapid Fire value.";
            case "Melta":
                return "If target is within half range, add the Melta value to the Damage.";
            case "Indirect Fire":
                return "Can target units not visible to the firing model.";
            case "Sustained Hits":
                return "Each Critical Hit scores additional hits equal to the Sustained Hits value.";
            case "Lethal Hits":
                return "Critical Hits automatically wound.";
            case "Devastating Wounds":
                return "Critical Wounds cause mortal wounds equal to the Damage characteristic.";
            case "Torrent":
                return "This weapon automatically hits its target.";
            case "Twin-linked":
                return "Re-roll Wound rolls.";
            case "Pistol":
                return "Can be used in the Shooting phase even when within Engagement Range.";
            case "Heavy":
                return "Add 1 to Hit rolls if the bearer did not move this turn.";
            case "Assault":
                return "Can be used even after Advancing.";
            case "One Shot":
                return "This weapon can only be fired once per battle.";
            case "Ignores Cover":
                return "Target does not benefit from Cover.";
            default:
                return keyword;
        }
    }

    // ======================= Units =======================

    private HashSet<Keyword> GetKeywordsFromSelectionEntry(SelectionEntry entry) {
        HashSet<Keyword> retSet = new HashSet<>();
        if (entry.categoryLinks == null) return retSet;

        for (CategoryLink link : entry.categoryLinks) {
            if (link.name == null) continue;
            String prunedString = Parsing.toEnumName(link.name);
            Keyword keyword = Parsing.keywordFromString(prunedString);
            if (keyword != null) {
                retSet.add(keyword);
            } else {
                String message = prunedString + (link.id != null ? " " + link.id : "");
                Logging.d("Unit parsing", "Invalid keyword found " + message);
            }
        }
        return retSet;
    }

    public void IndexAllUnits(Entry entry, Faction faction) {
        if (entry == null) return;

        if (entry instanceof SelectionEntry) {
            SelectionEntry se = (SelectionEntry) entry;
            Unit unit = null;

            if ("unit".equalsIgnoreCase(se.type)) {
                unit = ParseUnitFromSelectionEntry(se);
            } else if ("model".equalsIgnoreCase(se.type)) {
                Model model = ParseModelFromSelectionEntry(se, null);
                if (IsValidModel(model)) {
                    unit = new Unit();
                    unit.unitName = model.name;
                    unit.GetAbilities().addAll(model.GetAbilities());
                }
            }

            if (unit != null) {
                unit.keywords = GetKeywordsFromSelectionEntry(se);
            }

            // Crucible units are not supported
            if (unit != null && !unit.unitName.contains("[Crucible]") && !unit.unitName.contains("[Legends]")) {
                if (se.id != null) {
                    idToUnit.put(se.id, unit);
                }
                nameFactionToUnit.put(new DatabaseManager.NameFactionKey(unit.unitName, faction), unit);
                nameToUnit.put(unit.unitName, unit);

                ArrayList<Weapon> weapons = CollectWeaponsFromSubtree(se);
                if (weapons.isEmpty()) {
                    String message = unit.unitName + (se.id != null ? " " + se.id : "");
                    Logging.d("Unit parsing", "Found unit with no weapons " + message);
                } else {
                    for (Weapon weapon : weapons) {
                        ArrayList<Weapon> weaponArrayList = new ArrayList<>();
                        weaponArrayList.add(weapon);
                        nameFactionUnitToWeapon.put(
                                new DatabaseManager.NameFactionUnitKey(weapon.name, faction, unit.unitName),
                                weaponArrayList);
                    }
                }
            }
        }

        for (Entry child : childEntries(entry)) {
            IndexAllUnits(child, faction);
        }
    }

    private Unit ParseUnitFromSelectionEntry(SelectionEntry unitEntry) {
        Unit unit = new Unit();
        if (unitEntry.name == null) return null;
        unit.unitName = unitEntry.name;

        // Unit-level abilities (e.g. "Grim Demeanour") live directly on this entry's own profiles
        if (unitEntry.profiles != null) {
            for (Profile profile : unitEntry.profiles) {
                if ("Abilities".equals(profile.typeName)) {
                    Ability ability = parseAbilityFromProfile(profile);
                    if (ability != null) {
                        unit.GetAbilities().add(ability);
                    }
                }
            }
        }

        // Also collect abilities from infoGroups (Leader ability, special rules etc.)
        if (unitEntry.infoGroups != null) {
            for (InfoGroup infoGroup : unitEntry.infoGroups) {
                if (infoGroup.profiles == null) continue;
                for (Profile profile : infoGroup.profiles) {
                    if ("Abilities".equals(profile.typeName)) {
                        Ability ability = parseAbilityFromProfile(profile);
                        if (ability != null) {
                            unit.GetAbilities().add(ability);
                        }
                    }
                }
            }
        }

        // Model collection intentionally left out here — same as XmlParser, where
        // collectModelsFromUnitSubtree was defined but never actually called from this method.

        return unit;
    }

    // ======================= Models =======================

    private boolean IsValidModel(Model model) {
        if (model == null) return false;
        if (model.wounds == -1 || model.toughness == -1 || model.armorSave == -1) return false;
        return model.name != null;
    }

    private void IndexAllModels(Entry entry, Faction faction, SelectionEntry unitAncestor) {
        if (entry == null) return;

        SelectionEntry nextAncestor = unitAncestor;

        if (entry instanceof SelectionEntry ) {
            SelectionEntry se = (SelectionEntry) entry;
            if ("unit".equalsIgnoreCase(se.type)) {
                nextAncestor = se;
            }
            if ("model".equalsIgnoreCase(se.type)  ) {
                String idValue = se.id != null ? se.id : "";
                Model model = ParseModelFromSelectionEntry(se, unitAncestor);
                boolean isNotLegendUnit = unitAncestor == null || (!unitAncestor.name.contains("[Legends]") && !unitAncestor.name.contains("Crucible"));
                if(model != null && !model.name.contains("[Legends]") && !model.name.contains("Crucible") && isNotLegendUnit)
                {
                    if (model.weapons.isEmpty() ) {
                        Logging.d("Weapon parsing", "Model with no weapons found " + model.name + " " + idValue);
                    }

                    if (IsValidModel(model)) {
                        if (se.id != null) {
                            idToModel.put(se.id, model);
                        }
                        nameFactionToModel.put(new DatabaseManager.NameFactionKey(model.name, faction), model);
                        nameToModel.put(model.name, model);
                    } else {
                        String message = "";
                        message = model.name + " " + idValue;

                        Logging.d("Model parsing", "Invalid model found " + message);
                    }
                }
            }
        }

        for (Entry child : childEntries(entry)) {
            IndexAllModels(child, faction, nextAncestor);
        }
    }

    private Model ParseModelFromSelectionEntry(SelectionEntry entry, SelectionEntry parentUnitEntry) {
        Model model = new Model();
        if (entry.name == null) return null;
        model.name = entry.name;

        boolean foundStats = false;

        if(model.name.contains("[Legends]") || model.name.contains("Crucible"))
        {
            return model;
        }

        // First try: a Unit-type profile somewhere in this entry's own subtree
        Profile unitProfile = findProfileByTypeName(entry, "Unit");
        if (unitProfile != null) {
            foundStats = TryParseModelStatsFromProfile(unitProfile, model);
        }

        // Second try: follow infoLinks to shared profiles indexed earlier via CreateSharedModelStats
        if (!foundStats && entry.infoLinks != null) {
            for (InfoLink link : entry.infoLinks) {
                if (!"profile".equals(link.type) || link.targetId == null) continue;
                Model sharedModel = idToModel.get(link.targetId);
                if (sharedModel != null) {
                    model.toughness = sharedModel.toughness;
                    model.armorSave = sharedModel.armorSave;
                    model.wounds = sharedModel.wounds;
                    foundStats = true;
                    break;
                }
            }
        }

        // Third try: entries like Tempestor Aquilons don't define their own model profile —
        // it only exists inline on the enclosing unit entry. No parent pointers in this tree,
        // so the ancestor is threaded in from IndexAllModels instead of walking up.
        if (!foundStats && parentUnitEntry != null) {
            Profile parentProfile = findProfileByTypeName(parentUnitEntry, "unit");
            if (parentProfile != null) {
                TryParseModelStatsFromProfile(parentProfile, model);
                model.name = entry.name;
            }
        }

        ArrayList<Weapon> weapons = CollectWeaponsFromSubtree(entry);
        model.weapons.addAll(weapons);
        model.GetAbilities().addAll(ParseAbilitiesFromSubtree(entry));

        return model;
    }

    private boolean TryParseModelStatsFromProfile(Profile profile, Model model) {
        if (profile == null || profile.typeName == null) return false;
        if (!profile.typeName.equals("Unit")) return false;

        // Sample data uses "Sv" not "SV" — findCharacteristic is case-insensitive so both work
        String t = findCharacteristic(profile.characteristics, "T");
        String sv = findCharacteristic(profile.characteristics, "SV");
        String w = findCharacteristic(profile.characteristics, "W");

        if (t != null) model.toughness = p_ParseUnitStat(t);
        if (sv != null) model.armorSave = p_ParseUnitStat(sv);
        if (w != null) model.wounds = p_ParseUnitStat(w);

        return true;
    }

    // ======================= Weapons =======================

    private ArrayList<Weapon> CollectWeaponsFromSubtree(Entry entry) {
        ArrayList<Weapon> result = new ArrayList<>();
        collectWeaponsRecursive(entry, result);
        return result;
    }

    private void collectWeaponsRecursive(Entry entry, ArrayList<Weapon> result) {
        if (entry == null) return;

        if (entry instanceof EntryLink) {
            EntryLink link = (EntryLink) entry;
            if (link.targetId != null) {
                ArrayList<Weapon> weapons = new ArrayList<>();
                ArrayList<Weapon> databaseWeapons = idToWeapon.get(link.targetId);
                if (databaseWeapons != null) {
                    weapons.addAll(databaseWeapons);
                } else {
                    Model model = idToModel.get(link.targetId);
                    if (model != null) {
                        weapons.addAll(model.weapons);
                    }
                }
                if (!weapons.isEmpty()) {
                    result.addAll(weapons);
                }
            }
        }

        if (entry instanceof SelectionEntry) {
            SelectionEntry se = (SelectionEntry) entry;
            if ("upgrade".equalsIgnoreCase(se.type) && se.id != null) {
                ArrayList<Weapon> weapons = idToWeapon.get(se.id);
                if (weapons != null) {
                    for (Weapon w : weapons) {
                        result.add(w.Copy());
                    }
                    return; // don't recurse into the weapon profile's own children
                }
            }
        }

        for (Entry child : childEntries(entry)) {
            collectWeaponsRecursive(child, result);
        }
    }

    private boolean IsValidWeapon(Weapon weapon) {
        if (weapon == null) return false;
        if (weapon.amountOfAttacks == null) return false;
        return !(weapon.amountOfAttacks.numberOfD3 == 0
                && weapon.amountOfAttacks.numberOfD6 == 0
                && weapon.amountOfAttacks.baseAmount == 0);
    }

    private ArrayList<Weapon> ParseWeaponsFromUpgradeEntry(SelectionEntry entry) {
        ArrayList<Weapon> parsedWeapons = new ArrayList<>();
        List<Profile> profiles = findProfilesList(entry);
        if (profiles == null) return parsedWeapons;

        for (Profile profile : profiles) {
            if (profile.typeName == null) continue;
            if ("Ranged Weapons".equalsIgnoreCase(profile.typeName)
                    || "Melee Weapons".equalsIgnoreCase(profile.typeName)) {
                Weapon weapon = ClaudeParseWeapon(profile);
                if (IsValidWeapon(weapon)) {
                    parsedWeapons.add(weapon);
                }
            }
        }
        return parsedWeapons;
    }

    private void IndexAllWeapons(Entry entry, Faction faction) {
        if (entry == null) return;

        if (entry instanceof SelectionEntry) {
            SelectionEntry se = (SelectionEntry) entry;
            if ("upgrade".equalsIgnoreCase(se.type)) {
                ArrayList<Weapon> weapons = ParseWeaponsFromUpgradeEntry(se);
                if (se.id != null && !weapons.isEmpty()) {
                    nameToWeapon.put(se.name, weapons);
                    idToWeapon.put(se.id, weapons);
                }
            }
        }

        for (Entry child : childEntries(entry)) {
            IndexAllWeapons(child, faction);
        }
    }

    private Weapon ClaudeParseWeapon(Profile profile) {
        Weapon returnWeapon = new Weapon();
        returnWeapon.name = profile.name;
        returnWeapon.isMelee = "Melee Weapons".equals(profile.typeName);

        List<Characteristic> chars = profile.characteristics;
        if (chars == null) return returnWeapon;

        String attacks = findCharacteristic(chars, "A");
        String strength = findCharacteristic(chars, "S");
        String ap = findCharacteristic(chars, "AP");
        String damage = findCharacteristic(chars, "D");
        String bsWs = findCharacteristic(chars, returnWeapon.isMelee ? "WS" : "BS");

        if (attacks != null) returnWeapon.amountOfAttacks = Parsing.ParseDiceAmount(attacks);
        if (strength != null) returnWeapon.strength = p_ParseUnitStat(strength);
        if (ap != null) returnWeapon.ap = p_ParseUnitStat(ap);
        if (damage != null) returnWeapon.damageAmount = Parsing.ParseDiceAmount(damage);
        if (bsWs != null) returnWeapon.ballisticSkill = p_ParseUnitStat(bsWs);

        String keywords = findCharacteristic(chars, "Keywords");
        if (keywords != null) {
            keywords = keywords.trim();
            if (!keywords.equals("-") && !keywords.isEmpty()) {
                for (String keyword : keywords.split(",")) {
                    keyword = keyword.trim();
                    if (!keyword.isEmpty()) {
                        UnimplementedAbility ability = new UnimplementedAbility(keyword);
                        ability.description = claudeGetKeywordDescription(keyword);
                        returnWeapon.GetAbilities().add(ability);
                    }
                }
            }
        }

        return returnWeapon;
    }

    // ======================= Shared model stats =======================

    private void CreateSharedModelStats(Catalogue catalogue, Faction faction) {
        if (catalogue.sharedProfiles == null) return;

        for (Profile profile : catalogue.sharedProfiles) {
            if (!"Unit".equals(profile.typeName)) continue;

            Model parsedModel = new Model();
            TryParseModelStatsFromProfile(profile, parsedModel);
            if (profile.name != null) {
                parsedModel.name = profile.name;
            }
            if (IsValidModel(parsedModel) && profile.id != null) {
                nameFactionToModel.put(new DatabaseManager.NameFactionKey(parsedModel.name, faction), parsedModel);
                idToModel.put(profile.id, parsedModel);
            }
        }
    }
}

