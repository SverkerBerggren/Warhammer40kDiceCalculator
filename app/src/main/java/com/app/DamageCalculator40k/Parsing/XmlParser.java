package com.app.DamageCalculator40k.Parsing;

import android.util.Log;


import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Abilities.GenericAbilities.MortalWoundOnHit;
import com.app.DamageCalculator40k.Abilities.UnimplementedAbility;
import com.app.DamageCalculator40k.DatabaseManager;
import com.app.DamageCalculator40k.DatasheetModeling.Model;
import com.app.DamageCalculator40k.DatasheetModeling.Unit;
import com.app.DamageCalculator40k.DatasheetModeling.Weapon;
import com.app.DamageCalculator40k.Enums.Faction;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XmlParser
{
    // Todo: fixa skiten ghetto af, men testar walla
    public final HashMap<DatabaseManager.NameFactionKey, Unit> nameToUnit = new HashMap<>();
    public final HashMap<String, Unit> idToUnit = new HashMap<>();
    public final HashMap<DatabaseManager.NameFactionKey, Model> nameToModel = new HashMap<>();
    public final HashMap<String, Model> idToModel = new HashMap<>();
    public final HashMap<String, ArrayList<Weapon>> idToWeapon = new HashMap<>();
    public final HashMap<DatabaseManager.NameFactionKey, ArrayList<Weapon>> nameToWeapon = new HashMap<>();
    public final HashMap<DatabaseManager.NameFactionUnitKey, ArrayList<Weapon>> nameUnitToWeapon = new HashMap<>();
    public final  HashMap<DatabaseManager.NameFactionUnitKey,Weapon> nameFactionUnitToWeapon = new HashMap<>();

    int p_ParseUnitStat(String StatString)
    {
        if (StatString == null || StatString.trim().isEmpty()
                || StatString.equals("-") || StatString.equals("N/A")) {
            return 0;
        }
        // Take only the first value if slash-separated (e.g. plasma gun "2/3")
        String firstPart = StatString.split("/")[0];
        String cleaned = firstPart.replaceAll("\\+|\\*|[a-zA-Z]+|\\s+", "").trim();
        if (cleaned.isEmpty()) return 0;
        try {
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    static Node p_FirsChildByType(Node TopNode,String ChildType)
    {
        Node ReturnValue = null;
        for(int i = 0; i < TopNode.getChildNodes().getLength();i++)
        {
            String NodeName =TopNode.getChildNodes().item(i).getNodeName();
            if(NodeName.equals(ChildType))
            {
                ReturnValue = TopNode.getChildNodes().item(i);
                break;
            }
        }
        return(ReturnValue);
    }

    static boolean ContainsTypeWithValue(Node node, String type,String value)
    {
        Node nodeToFind = node.getAttributes().getNamedItem(type);
        if(nodeToFind != null)
        {
            return  nodeToFind.getNodeValue().equals(value);
        }
        return  false;
    }

    static Node GetChildNodeWithAttributeValue(Node node, String attribute,String value)
    {
        for(int i = 0; i < node.getChildNodes().getLength();i++)
        {
            Node childNode = node.getChildNodes().item(i);
            Node attributeNode = childNode.getAttributes().getNamedItem(attribute);
            if(attributeNode != null)
            {
                if(attributeNode.getNodeValue().equals(value))
                {
                    return childNode;
                }
            }
        }
        return null;
    }

    // If attributeType is null so does it only retrieve a node based on its element type
    private Node GetFirstNodeOfTypeRecursively(Node node,String elementName,String attributeType,String attributeValue)
    {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
        {
            String nodeName = children.item(i).getNodeName();

            if(children.item(i).getNodeName().equalsIgnoreCase(elementName))
            {
                if(attributeType != null)
                {
                    Node namedItem = children.item(i).getAttributes().getNamedItem(attributeType);
                    if(namedItem != null)
                    {
                        if (namedItem.getNodeValue().equalsIgnoreCase(attributeValue))
                        {
                            return children.item(i);
                        }
                    }
                }
                else
                {
                    return children.item(i);
                }
            }
        }
        for (int i = 0; i < children.getLength(); i++)
        {
            Node nodeToFind =  GetFirstNodeOfTypeRecursively(children.item(i),elementName,attributeType,attributeValue);
            if(nodeToFind != null)
            {
                return nodeToFind;
            }
        }
        return null;
    }

    private String OnlyNumeric(String string)
    {
        StringBuilder retString = new StringBuilder();
        for(int i = 0; i < string.length();i++)
        {
            if(Character.isDigit( string.charAt(i)))
            {
                retString.append(string.charAt(i));
            }
        }
        return retString.toString();
    }

    //TODO: finns redan en metod som ska tas bort
    private Model ParseModelFromProfile(Node node)
    {
        Model modelToReturn = new Model();
        modelToReturn.name = node.getAttributes().getNamedItem("name").getNodeValue();
        Node characteristics = p_FirsChildByType(node,"characteristics");

        String toughnessString = GetChildNodeWithAttributeValue(characteristics,"name","T").getFirstChild().getNodeValue();
        modelToReturn.toughness = Integer.parseInt(toughnessString);

        String saveString = OnlyNumeric( GetChildNodeWithAttributeValue(characteristics,"name","SV").getFirstChild().getNodeValue());
        modelToReturn.armorSave = Integer.parseInt(saveString);

        String woundString = GetChildNodeWithAttributeValue(characteristics,"name","W").getFirstChild().getNodeValue();
        modelToReturn.wounds = Integer.parseInt(woundString);

        return modelToReturn;
    }

    private Document ParseXML(String data)
    {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(data.getBytes(Charset.defaultCharset()));
            Document doc = dBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            //COPY PASTE
            XPathFactory xpathFactory = XPathFactory.newInstance();
// XPath to find empty text nodes.
            XPathExpression xpathExp = xpathFactory.newXPath().compile(
                    "//text()[normalize-space(.) = '']");
            NodeList emptyTextNodes = (NodeList)
                    xpathExp.evaluate(doc, XPathConstants.NODESET);
// Remove each empty text node from document.
            for (int i = 0; i < emptyTextNodes.getLength(); i++) {
                Node emptyTextNode = emptyTextNodes.item(i);
                emptyTextNode.getParentNode().removeChild(emptyTextNode);
            }
            return  doc;
        }
        catch (Exception e)
        {
            Log.d("Knas i parse","abow");
            e.printStackTrace();
        }
        return null;
    }

    // TODO: temporar skas andras trust
    public void FillDatabase(ArrayList<String> xmlDocs)
    {
        for (String xmlDoc : xmlDocs)
        {
            Faction faction = Faction.AstraMilitarum;
            Document doc = ParseXML(xmlDoc);
            CreateSharedModelStats(doc,faction);   // builds idToModel from sharedProfiles
        }
        for (String xmlDoc : xmlDocs)
        {
            Document doc = ParseXML(xmlDoc);
            Faction faction = Faction.AstraMilitarum;
            IndexAllWeapons(doc.getDocumentElement(),faction);
            IndexAllModels(doc.getDocumentElement(),faction);
            IndexAllUnits(doc.getDocumentElement(),faction);
        }
        //Debug
        Map<String,Integer> duplicateUnits = new HashMap<>();
        idToUnit.forEach((key,value)->{
            idToUnit.forEach((otherKey,otherValue)->{
                if(!key.equals( otherKey) && value.unitName.equals(otherValue.unitName) )
                {
                    Integer duplicateValue = duplicateUnits.get(otherValue.unitName);
                    if(duplicateValue == null)
                    {
                        duplicateUnits.put(otherValue.unitName,1);
                    }
                    else
                    {
                        duplicateUnits.put(otherValue.unitName,duplicateValue+1);
                    }
                }
            });
        });
    }
    public void IndexAllUnits(Node node,Faction faction) {
        if (node.getNodeType() != Node.ELEMENT_NODE) return;

        NamedNodeMap attrs = node.getAttributes();
        if (attrs != null) {
            if(node.getNodeName().equalsIgnoreCase("selectionEntry"))
            {
                Node typeAttr = attrs.getNamedItem("type");
                Unit unit = null;

                if(typeAttr != null)
                {
                    // Assumes that it is a composite unit consisting of different models, in that case so is the selection entry "unit"
                    if (typeAttr.getNodeValue().equalsIgnoreCase("unit") ) {
                        unit = ParseUnitFromSelectionEntry(node);
                    }
                    // Makes the assumptions that a single model unit is defined by its Model first. This will create more units than there are such in the case of sentinels, but that should not be a problem

                    if(typeAttr.getNodeValue().equalsIgnoreCase("model"))
                    {
                        Model model = ParseModelFromSelectionEntry(node);

                        if (IsValidModel(model)) {
                            unit = new Unit();
                            unit.unitName = model.name;
                            unit.GetAbilities().addAll(model.GetAbilities());
                        }
                    }
                }
                if(unit != null)
                {
                    Node idAttr = attrs.getNamedItem("id");
                    if (idAttr != null) {
                        idToUnit.put( idAttr.getNodeValue(), unit);
                    }
                    if(unit.unitName.equalsIgnoreCase("lord solar leontus"))
                    {
                        Log.d("Unit parsing","Found unit with no weapons " );

                    }
                    nameToUnit.put(new DatabaseManager.NameFactionKey( unit.unitName,faction), unit);
                    ArrayList<Weapon> weapons =  CollectWeaponsFromSubtree(node);
                    if(weapons.isEmpty())
                    {
                        String message = unit.unitName;
                        if(idAttr != null)
                        {
                            message += " " + idAttr.getNodeValue();
                        }
                        Log.d("Unit parsing","Found unit with no weapons " + message);
                    }
                    else
                    {
                        for(Weapon weapon : weapons)
                        {
                            ArrayList<Weapon> weaponArrayList = new ArrayList<>();
                            weaponArrayList.add(weapon);
                            nameUnitToWeapon.put(new DatabaseManager.NameFactionUnitKey(weapon.name,faction,unit.unitName),weaponArrayList);
                            if(weapon.name.equalsIgnoreCase("tempestus dagger"))
                            {
                                Log.d("abow","multi weapon bullshit");
                            }
                        }
                    }
                }
            }
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            IndexAllUnits(children.item(i),faction);
        }
    }

    private Unit ParseUnitFromSelectionEntry(Node unitNode) {
        Unit unit = new Unit();

        Node nameAttr = unitNode.getAttributes().getNamedItem("name");
        if (nameAttr == null) return null;
        unit.unitName = nameAttr.getNodeValue();

        // Unit-level abilities (things like "Grim Demeanour", "Jungle Fighters" etc.)
        // These live in the direct profiles child of the unit node, not nested deeper
        Node profilesNode = p_FirsChildByType(unitNode, "profiles");

        if (profilesNode != null) {
            NodeList profileChildren = profilesNode.getChildNodes();
            for (int i = 0; i < profileChildren.getLength(); i++) {
                Node profileNode = profileChildren.item(i);
                if (profileNode.getNodeType() != Node.ELEMENT_NODE) continue;

                NamedNodeMap attrs = profileNode.getAttributes();
                if (attrs == null) continue;

                Node typeNameAttr = attrs.getNamedItem("typeName");
                if (typeNameAttr == null) continue;

                if (typeNameAttr.getNodeValue().equals("Abilities")) {
                    Ability ability = parseAbilityFromProfileNode(profileNode);
                    if (ability != null) {
                        unit.GetAbilities().add(ability);
                    }
                }
            }
        }

        // Also collect abilities from infoGroups (Leader ability, special rules etc.)
        // These are in <infoGroups> > <infoGroup> > <profiles> > <profile typeName="Abilities">
        Node infoGroupsNode = p_FirsChildByType(unitNode, "infoGroups");
        if (infoGroupsNode != null) {
            NodeList infoGroups = infoGroupsNode.getChildNodes();
            for (int i = 0; i < infoGroups.getLength(); i++) {
                Node infoGroup = infoGroups.item(i);
                if (infoGroup.getNodeType() != Node.ELEMENT_NODE) continue;
                Node infoProfilesNode = p_FirsChildByType(infoGroup, "profiles");
                if (infoProfilesNode != null) {
                    NodeList infoProfiles = infoProfilesNode.getChildNodes();
                    for (int j = 0; j < infoProfiles.getLength(); j++) {
                        Node profileNode = infoProfiles.item(j);
                        if (profileNode.getNodeType() != Node.ELEMENT_NODE) continue;
                        NamedNodeMap attrs = profileNode.getAttributes();
                        if (attrs == null) continue;
                        Node typeNameAttr = attrs.getNamedItem("typeName");
                        if (typeNameAttr != null
                                && typeNameAttr.getNodeValue().equals("Abilities")) {
                            Ability ability = parseAbilityFromProfileNode(profileNode);
                            if (ability != null) {
                                unit.GetAbilities().add(ability);
                            }
                        }
                    }
                }
            }
        }

        // Now collect the models that make up this unit
        // They live inside selectionEntryGroups, potentially nested several levels deep


        // collectModelsFromUnitSubtree(unitNode, unit);

        return unit;
    }

    private void collectModelsFromUnitSubtree(Node node, Unit unit) {
        if (node.getNodeType() != Node.ELEMENT_NODE) return;

        NamedNodeMap attrs = node.getAttributes();
        if (attrs != null) {
            Node typeAttr = attrs.getNamedItem("type");
            if (typeAttr != null && typeAttr.getNodeValue().equals("model")) {
                Model m = ParseModelFromSelectionEntry(node);
                if (m != null) {
                    unit.listOfModels.add(m);
                }
                // Don't recurse further once we found a model entry,
                // its children are weapon/upgrade options not more models
                return;
            }
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            collectModelsFromUnitSubtree(children.item(i), unit);
        }
    }
    private boolean IsValidModel(Model model)
    {
        if(model == null)
        {
            return false;
        }
        if(model.wounds == -1 || model.toughness == -1 || model.armorSave == -1 )
        {
            return false;
        }
        return true;
    }
    private void IndexAllModels(Node node,Faction faction) {
        if (node.getNodeType() != Node.ELEMENT_NODE) return;

        NamedNodeMap attrs = node.getAttributes();
        if (attrs != null) {
            String nodeName = node.getNodeName();
            Node typeNode = attrs.getNamedItem("type");

            if (typeNode != null && nodeName.equalsIgnoreCase("selectionEntry")) {
                String typeString = typeNode.getNodeValue();
                // Selection entries are scuffed
                if (typeString.equalsIgnoreCase("model")) {
                    Node idAttr = attrs.getNamedItem("id");
                    String idValue = "";
                    if (idAttr != null)
                        idValue = idAttr.getNodeValue();

                    Model model = ParseModelFromSelectionEntry(node);
                    if(model.name.equalsIgnoreCase("tempestor aquilon"))
                    {
                        Log.d("Model parsing","Tempestor aquilon");
                    }

                    if (IsValidModel(model)) {
                        if (idAttr != null) {
                            idValue = idAttr.getNodeValue();
                            idToModel.put(idValue, model);
                        }
                        nameToModel.put(new DatabaseManager.NameFactionKey(model.name,faction), model);
                    }
                    else
                    {
                        String message = "";
                        if(model != null)
                        {
                            message = model.name + idValue;
                        }
                        Log.d("Model parsing","Invalid model found " + message);
                    }
                }
            }
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            IndexAllModels(children.item(i),faction);
        }
    }

    private Node GetFirstParentNodeRecursively(Node node, String elementName, String attributeName, String attributeValue )
    {
        Node parentNode = node.getParentNode();
        if(parentNode == null)
        {
            return  null;
        }
        if(parentNode.getNodeName().equalsIgnoreCase(elementName))
        {
            Node foundNodeType = parentNode.getAttributes().getNamedItem(attributeName);
            if(foundNodeType != null)
            {
                if(foundNodeType.getNodeValue().equalsIgnoreCase(attributeValue))
                {
                    return parentNode;
                }
            }
        }

        return GetFirstParentNodeRecursively(parentNode,elementName,attributeName,attributeValue);
    }

    private Model ParseModelFromSelectionEntry(Node selectionEntry) {
        Model model = new Model();

        Node nameAttr = selectionEntry.getAttributes().getNamedItem("name");
        if (nameAttr == null) return null;
        model.name = nameAttr.getNodeValue();

        // First try: look for a Unit profile directly on this node
        boolean foundStats = false;
        Node profilesNode = p_FirsChildByType(selectionEntry, "profiles");
        if (profilesNode != null) {
            foundStats = TryParseUnitStatsIntoModel(profilesNode, model);
        }

        // Second try: follow infoLinks to shared profiles
        if (!foundStats) {
            Node infoLinksNode = p_FirsChildByType(selectionEntry, "infoLinks");
            if (infoLinksNode != null) {
                NodeList infoLinks = infoLinksNode.getChildNodes();
                for (int i = 0; i < infoLinks.getLength(); i++) {
                    Node infoLink = infoLinks.item(i);
                    if (infoLink.getNodeType() != Node.ELEMENT_NODE) continue;

                    NamedNodeMap attrs = infoLink.getAttributes();
                    if (attrs == null) continue;

                    Node typeAttr = attrs.getNamedItem("type");
                    if (typeAttr == null || !typeAttr.getNodeValue().equals("profile"))
                        continue;

                    Node targetIdAttr = attrs.getNamedItem("targetId");
                    if (targetIdAttr == null) continue;

                    String targetId = targetIdAttr.getNodeValue();

                    // Look up in the shared profiles we indexed earlier
                    Model sharedModel = idToModel.get(targetId);
                    if (sharedModel != null) {
                        model.toughness = sharedModel.toughness;
                        model.armorSave = sharedModel.armorSave;
                        model.wounds = sharedModel.wounds;
                        model.attacks = sharedModel.attacks;
                        model.strength = sharedModel.strength;
                        foundStats = true;
                        break;
                    }
                }
            }
            // This case is very weird, Tempestor aquilons do not have their model profile defined except for inline in the unit selection. Hopefully does not result in weird side effects
            if(!foundStats)
            {
                Node parentSelectionEntry = GetFirstParentNodeRecursively(selectionEntry,"selectionEntry","type","unit");
                if(parentSelectionEntry != null)
                {
                    Node profileNode = GetFirstNodeOfTypeRecursively(parentSelectionEntry,"profile","typeName","unit");
                    if(profileNode != null)
                    {
                        TryParseUnitStatsIntoModel(profileNode.getParentNode(),model);
                        model.name = nameAttr.getNodeValue();
                    }
                }
            }
        }

        ArrayList<Weapon> weapons = CollectWeaponsFromSubtree(selectionEntry);
        if(weapons.isEmpty())
        {
            String message = model.name;
            message += " "+ selectionEntry.getAttributes().getNamedItem("id").getNodeValue();
            Log.d("Weapon parsing","Model with no weapons found " + message);
        }
        model.weapons.addAll(weapons);
        model.GetAbilities().addAll(ParseAbilitiesFromSubtree(selectionEntry));

        return model;
    }
    // Returns true if it found and parsed a Unit profile
    private boolean TryParseUnitStatsIntoModel(Node profilesNode, Model model) {
        NodeList children = profilesNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node profileNode = children.item(i);
            if (profileNode.getNodeType() != Node.ELEMENT_NODE) continue;

            NamedNodeMap attrs = profileNode.getAttributes();
            if (attrs == null) continue;

            Node typeNameAttr = attrs.getNamedItem("typeName");
            if (typeNameAttr == null) continue;

            if (typeNameAttr.getNodeValue().equals("Unit")) {
                Node chars = p_FirsChildByType(profileNode, "characteristics");
                if (chars == null) continue;

                Node tNode = GetChildNodeWithAttributeValue(chars, "name", "T");
                Node svNode = GetChildNodeWithAttributeValue(chars, "name", "SV");
                Node wNode = GetChildNodeWithAttributeValue(chars, "name", "W");
                Node aNode = GetChildNodeWithAttributeValue(chars, "name", "A");
                Node sNode = GetChildNodeWithAttributeValue(chars, "name", "S");

                if (tNode != null) model.toughness = p_ParseUnitStat(tNode.getTextContent());
                if (svNode != null) model.armorSave = p_ParseUnitStat(svNode.getTextContent());
                if (wNode != null) model.wounds = p_ParseUnitStat(wNode.getTextContent());
                if (aNode != null) model.attacks = p_ParseUnitStat(aNode.getTextContent());
                if (sNode != null) model.strength = p_ParseUnitStat(sNode.getTextContent());
                return true;
            }
        }
        return false;
    }

    // Walks the subtree collecting weapons by looking up already-indexed idToWeapon
    private ArrayList<Weapon> CollectWeaponsFromSubtree(Node node) {
        ArrayList<Weapon> result = new ArrayList<>();
        collectWeaponsRecursive(node, result);
        return result;
    }

    private void collectWeaponsRecursive(Node node, ArrayList<Weapon> result) {
        if (node.getNodeType() != Node.ELEMENT_NODE) return;

        NamedNodeMap attrs = node.getAttributes();
        if (attrs != null) {

            Node idNode = attrs.getNamedItem("id");
            if(idNode != null)
            {
                if(idNode.getNodeValue().equalsIgnoreCase("3f82-134-f2ed-b02e"))
                {
                    // Log.d("Weapon database","Target link did not find its weapon ");
                }
            }
            // entryLink nodes point to weapon selectionEntries by targetId
            Node targetIdAttr = attrs.getNamedItem("targetId");
            if (targetIdAttr != null) {
                String targetId = targetIdAttr.getNodeValue();
                ArrayList<Weapon> weapons = new ArrayList<>();
                ArrayList<Weapon> databaseWeapons = idToWeapon.get(targetId);
                if(databaseWeapons != null)
                {
                    weapons.addAll(databaseWeapons);
                }
                else
                {
                    Model model = idToModel.get(targetId);
                    if(model != null)
                    {
                        weapons.addAll(model.weapons);
                    }
                }
                if(!weapons.isEmpty())
                {
                    result.addAll(weapons);
                }
                else
                {
                    String name  = attrs.getNamedItem("name").getNodeValue();
                    Log.d("Weapon database","Target link did not find its weapon " + targetId + " " + name);
                }

            }

            // Also check if this node itself is a weapon profile
            Node typeNameAttr = attrs.getNamedItem("type");
            if (typeNameAttr != null) {
                String typeName = typeNameAttr.getNodeValue();
                if (typeName.equalsIgnoreCase("upgrade")) {
                    Node idAttr = attrs.getNamedItem("id");
                    if (idAttr != null) {
                        ArrayList<Weapon> weapons = idToWeapon.get(idAttr.getNodeValue());
                        if (weapons != null) {
                            for(int i = 0; i < weapons.size(); i++)
                            {
                                result.add(weapons.get(i).Copy());
                            }
                            return; // don't recurse into the weapon profile itself
                        }
                    }
                }
            }
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            collectWeaponsRecursive(children.item(i), result);
        }
    }

    private boolean IsValidWeapon(Weapon weapon)
    {
        if(weapon == null)
        {
            return  false;
        }
        if(weapon.amountOfAttacks == null)
        {
            return false;
        }
        if(weapon.amountOfAttacks.numberOfD3 == 0 && weapon.amountOfAttacks.numberOfD6 == 0 && weapon.amountOfAttacks.baseAmount == 0 )
        {
            return  false;
        }
        return true;
    }
    // Multi mode weapons are a bit tricky, implemented as a list of weapons for now
    private ArrayList<Weapon> ParseWeaponsFromUpgradeEntry(Node node)
    {
        Node profilesNode =  GetFirstNodeOfTypeRecursively(node,"profiles",null,null);
        ArrayList<Weapon> parsedWeapons = new ArrayList<>();
        if(profilesNode != null)
        {
            NodeList profilesChildren = profilesNode.getChildNodes();
            for(int i = 0; i < profilesChildren.getLength(); i++)
            {
                Node weaponNode = profilesChildren.item(i);
                Node typeNameNode = weaponNode.getAttributes().getNamedItem("typeName");
                if(typeNameNode == null)
                {
                    continue;
                }
                if(typeNameNode.getNodeValue().equalsIgnoreCase("Ranged Weapons") || typeNameNode.getNodeValue().equalsIgnoreCase("Melee Weapons"))
                {
                    Weapon weapon = ClaudeParseWeapon(weaponNode);
                    if(IsValidWeapon(weapon))
                    {
                        parsedWeapons.add(weapon);
                    }
                }
            }
        }

        return parsedWeapons;
    }
    private void IndexAllWeapons(Node node, Faction faction)
    {
        if (node.getNodeType() != Node.ELEMENT_NODE) return;

        NamedNodeMap attrs = node.getAttributes();
        if (attrs != null) {
            Node typeAttr = attrs.getNamedItem("type");
            if (typeAttr != null) {
                String type = typeAttr.getNodeValue();
                ArrayList<Weapon> weapons = null;
                if (type.equalsIgnoreCase("upgrade")) {
                    weapons = ParseWeaponsFromUpgradeEntry(node);
                }
                Node idAttr = node.getAttributes().getNamedItem("id");
                if (idAttr != null && weapons != null && !weapons.isEmpty() ) {
                    String id = idAttr.getNodeValue();
                    String name = attrs.getNamedItem("name").getNodeValue();
                    nameToWeapon.put(new DatabaseManager.NameFactionKey( name,faction), weapons);
                    idToWeapon.put(id,weapons);
                    if(weapons.size() > 1)
                    {
                        Log.d("abow","multi weapon bullshit");
                    }
                    if(name.equalsIgnoreCase("tempestus dagger"))
                    {
                        Log.d("abow","multi weapon bullshit");
                    }
                }
            }
        }

        // Recurse into all children
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            IndexAllWeapons(children.item(i),faction);
        }
    }

    private String findOwningUnitName(Node weaponProfileNode) {
        Node current = weaponProfileNode.getParentNode();

        while (current != null) {
            if (current.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap attrs = current.getAttributes();
                if (attrs != null) {
                    Node typeAttr = attrs.getNamedItem("type");
                    if (typeAttr != null) {
                        String type = typeAttr.getNodeValue();
                        if (type.equals("unit") || type.equals("model")) {
                            Node nameAttr = attrs.getNamedItem("name");
                            if (nameAttr != null) {
                                return nameAttr.getNodeValue();
                            }
                        }
                    }

                    // Hit the sharedSelectionEntries container,
                    // this weapon has no owning unit
                    Node nodeNameAttr = attrs.getNamedItem("name");
                    if (nodeNameAttr != null && current.getNodeName()
                            .equals("sharedSelectionEntries")) {
                        return null;
                    }
                }
            }
            current = current.getParentNode();
        }
        return null;
    }
    private Weapon ClaudeParseWeapon(Node profileNode)
    {
        Weapon returnWeapon = new Weapon();
        returnWeapon.name = profileNode.getAttributes().getNamedItem("name").getNodeValue();

        String typeName = profileNode.getAttributes()
                .getNamedItem("typeName").getNodeValue();
        returnWeapon.isMelee = typeName.equals("Melee Weapons");

        Node chars = p_FirsChildByType(profileNode, "characteristics");
        if (chars == null) return returnWeapon;

        // Use name-based lookup instead of positional
        Node attacksNode = GetChildNodeWithAttributeValue(chars, "name", "A");
        Node strengthNode = GetChildNodeWithAttributeValue(chars, "name", "S");
        Node apNode = GetChildNodeWithAttributeValue(chars, "name", "AP");
        Node damageNode = GetChildNodeWithAttributeValue(chars, "name", "D");
        Node bsWsNode = GetChildNodeWithAttributeValue(chars, "name",
                returnWeapon.isMelee ? "WS" : "BS");

        if (attacksNode != null)
            returnWeapon.amountOfAttacks = Parsing.ParseDiceAmount(attacksNode.getTextContent());
        if (strengthNode != null)
            returnWeapon.strength = p_ParseUnitStat(strengthNode.getTextContent());
        if (apNode != null)
            returnWeapon.ap = p_ParseUnitStat(apNode.getTextContent());
        if (damageNode != null)
            returnWeapon.damageAmount = Parsing.ParseDiceAmount(damageNode.getTextContent());
        if (bsWsNode != null)
            returnWeapon.ballisticSkill = p_ParseUnitStat(bsWsNode.getTextContent());

        // Replace the ParseAbilitiesFromSubtree call with keyword parsing
        Node keywordsNode = GetChildNodeWithAttributeValue(chars, "name", "Keywords");
        if (keywordsNode != null) {
            String keywords = keywordsNode.getTextContent().trim();
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

    private ArrayList<Ability> ParseAbilitiesFromSubtree(Node root) {
        ArrayList<Ability> result = new ArrayList<>();
        collectAbilities(root, result);
        return result;
    }

    private void collectAbilities(Node node, ArrayList<Ability> result) {
        if (node.getNodeType() != Node.ELEMENT_NODE) return;

        NamedNodeMap attrs = node.getAttributes();
        if (attrs != null) {
            Node typeNameAttr = attrs.getNamedItem("typeName");
            if (typeNameAttr != null
                    && typeNameAttr.getNodeValue().equals("Abilities")) {
                Ability ability = parseAbilityFromProfileNode(node);
                if (ability != null) {
                    result.add(ability);
                }
                return; // don't recurse into the ability's own children
            }
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            collectAbilities(children.item(i), result);
        }
    }
    private String claudeGetKeywordDescription(String keyword) {
        // Strip trailing numbers first for lookup: "Rapid Fire 1" -> "Rapid Fire"
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
                return keyword; // fallback: just store the raw string
        }
    }
    private Ability parseAbilityFromProfileNode(Node profileNode) {
        NamedNodeMap attrs = profileNode.getAttributes();
        if (attrs == null) return null;

        Node nameAttr = attrs.getNamedItem("name");
        if (nameAttr == null) return null;
        String abilityName = nameAttr.getNodeValue();

        // Description lives in characteristics > characteristic[name="Description"]
        String description = "";
        Node characteristics = p_FirsChildByType(profileNode, "characteristics");
        if (characteristics != null) {
            Node descNode = GetChildNodeWithAttributeValue(
                    characteristics, "name", "Description");
            if (descNode != null) {
                description = descNode.getTextContent().trim();
            }
        }
        UnimplementedAbility retAbility = new UnimplementedAbility(abilityName);
        retAbility.description = description;
        return retAbility;
    }

    private void CreateSharedModelStats(Document doc,Faction faction)
    {
        Node nodeToFind = p_FirsChildByType(doc.getDocumentElement(),"sharedProfiles");
        if(nodeToFind != null)
        {
            for(int i = 0; i < nodeToFind.getChildNodes().getLength(); i++)
            {
                Node sharedProfileNode = nodeToFind.getChildNodes().item(i);
                if( ContainsTypeWithValue(sharedProfileNode,"typeName","Unit"))
                {
                    Model parsedModel = ParseModelFromProfile(sharedProfileNode);
                    nameToModel.put(new DatabaseManager.NameFactionKey(parsedModel.name,faction),parsedModel);
                    idToModel.put(sharedProfileNode.getAttributes().getNamedItem("id").getNodeValue(),parsedModel);
                }
            }
        }
    }
}