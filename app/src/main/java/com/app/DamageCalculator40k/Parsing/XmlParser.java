package com.app.DamageCalculator40k.Parsing;

import android.util.Log;


import com.app.DamageCalculator40k.Abilities.Ability;
import com.app.DamageCalculator40k.Abilities.GenericAbilities.MortalWoundOnHit;
import com.app.DamageCalculator40k.DatabaseManager;
import com.app.DamageCalculator40k.DatasheetModeling.Army;
import com.app.DamageCalculator40k.DatasheetModeling.DiceAmount;
import com.app.DamageCalculator40k.DatasheetModeling.Model;
import com.app.DamageCalculator40k.DatasheetModeling.Unit;
import com.app.DamageCalculator40k.DatasheetModeling.Weapon;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class XmlParser
{
    int p_ParseUnitStat(String StatString)
    {
        int ReturnValue = 0;
        String StringToParse = StatString.replaceAll("\\+|\\*|D|[a-zA-Z]+|\\s+","");
        if(StringToParse.equals(""))
        {
            return(ReturnValue);
        }
        ReturnValue = Integer.parseInt(StringToParse);

        return ReturnValue;
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
    public Army ParseArmy(String ArmyData)
    {
        Army ReturnValue = new Army();
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(ArmyData.getBytes(Charset.defaultCharset()));
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
            //END COPY PASTE
            Node ForceNode = p_FirsChildByType(p_FirsChildByType(doc.getDocumentElement(),"forces"),"force");
            Node ArmyRules = p_FirsChildByType(ForceNode,"rules");
            if(ArmyRules != null)
            {
                ReturnValue.GetAbilities().addAll(ParseRule(ArmyRules));
            }
            Node ArmyModels = p_FirsChildByType(ForceNode,"selections");
            for(int i = 0; i < ArmyModels.getChildNodes().getLength();i++)
            {
                Node Child = ArmyModels.getChildNodes().item(i);
                if(Child.getAttributes().getNamedItem("type").getNodeValue().equals("model"))
                {
                    Unit NewUnit = new Unit();
                    NewUnit.listOfModels = ParseModel(Child);
                    NewUnit.unitName = NewUnit.listOfModels.get(0).name;
                    ReturnValue.units.add(NewUnit);
                }
                else if(Child.getAttributes().getNamedItem("type").getNodeValue().equals("unit"))
                {
                    ReturnValue.units.add(ParseUnit(Child));
                }
                else if(Child.getAttributes().getNamedItem("type").getNodeValue().equals("upgrade"))
                {
                    Ability NewArmyRule = p_ParseArmyRule(Child);
                    if(NewArmyRule != null)
                    {
                        ReturnValue.GetAbilities().add(NewArmyRule);
                    }
                }
            }
        }
        catch(Exception e)
        {

            Log.d("Knas i parse", e.getMessage());
            e.printStackTrace();

            String Error = e.getMessage();
            String Error2 = e.getMessage();
        }
        return(ReturnValue);
    }
    Ability p_ParseArmyRule(Node SelectionsNode)
    {
        Ability ReturnValue = null;
        Node SubSelections = p_FirsChildByType(SelectionsNode,"selections");
        if(SubSelections == null)
        {
            return ReturnValue;
        }
        Node RuleSelection = p_FirsChildByType(SubSelections,"selection");
        if(RuleSelection == null) return ReturnValue;

        ReturnValue = DatabaseManager.getInstance().GetAbility(RuleSelection.getAttributes().getNamedItem("name").getNodeValue());
        return(ReturnValue);
    }
    DiceAmount p_DamageFromString(String StringToConvert)
    {
        DiceAmount ReturnValue = new DiceAmount();
        int ParseOffset = 0;
        int CurrentInteger = -1;
        while(ParseOffset < StringToConvert.length())
        {
            if(StringToConvert.charAt(ParseOffset) == 'D')
            {
                ParseOffset += 1;
                if(ParseOffset < StringToConvert.length())
                {
                    if(StringToConvert.charAt(ParseOffset) == '6')
                    {
                        ReturnValue.numberOfD6 = CurrentInteger == -1 ? 1 : CurrentInteger;
                    }
                    else if(StringToConvert.charAt(ParseOffset) == '3')
                    {
                        ReturnValue.numberOfD3 = CurrentInteger == -1 ? 1 : CurrentInteger;
                    }
                    ParseOffset += 1;
                    CurrentInteger = -1;
                }
            }
            else if(StringToConvert.charAt(ParseOffset) == '+')
            {
                if(CurrentInteger != -1)
                {
                    ReturnValue.baseAmount = CurrentInteger;
                }
                ParseOffset += 1;
            }
            else if(StringToConvert.charAt(ParseOffset) == '/')
            {
                break;
            }
            else
            {
                CurrentInteger = Character.getNumericValue(StringToConvert.charAt(ParseOffset));
                ParseOffset +=1;
            }
        }
        if(CurrentInteger != -1)
        {


            ReturnValue.baseAmount = CurrentInteger;
        }
        return(ReturnValue);
    }
    void p_ParseWeaponType(Weapon ResultWeapon, String TypeString)
    {
        if(TypeString.equals("Melee"))
        {
            ResultWeapon.isMelee = true;
            ResultWeapon.amountOfAttacks.baseAmount = 1;
            return;
        }
        int ParseOffset = 0;
        while(ParseOffset < TypeString.length())
        {
            char CurrentChar = TypeString.charAt(ParseOffset);
            if( (CurrentChar == 'D' && ParseOffset + 1 < TypeString.length() && TypeString.charAt(ParseOffset+1) >= '0' &&
                    TypeString.charAt(ParseOffset+1) <= '9')
                    || CurrentChar >= '0' && CurrentChar <= '9')
            {
                break;
            }
            ParseOffset += 1;
        }
        String DamageString = TypeString.substring(ParseOffset);
        if(ParseOffset != 0)
        {
            String AbilityString = TypeString.substring(0,ParseOffset);
            // Gammalt
            if(AbilityString.contains("Dakka")) {
                char LastCharacter = DamageString.charAt(DamageString.length()-1);
                DamageString = "";
                DamageString += LastCharacter;

            }
            else{
                // Gammalt
                ResultWeapon.GetAbilities().add(new MortalWoundOnHit(4));
            }
        }
        ResultWeapon.amountOfAttacks = new DiceAmount(p_DamageFromString(DamageString));
    }
    Weapon p_ParseWeapon(Node ProfileNode)
    {
        Node CharacteristicNode = p_FirsChildByType(ProfileNode,"characteristics");
        Weapon NewWeapon = new Weapon();
        NewWeapon.name =ProfileNode.getAttributes().getNamedItem("name").getNodeValue();

        p_ParseWeaponType(NewWeapon,CharacteristicNode.getChildNodes().item(1).getTextContent());

        String StrengthString = CharacteristicNode.getChildNodes().item(2).getTextContent();
        if(StrengthString.equals("x2"))
        {
            NewWeapon.strength = -2;
        }
        else
        {
            NewWeapon.strength = p_ParseUnitStat(StrengthString);
        }
        NewWeapon.ap = p_ParseUnitStat(CharacteristicNode.getChildNodes().item(3).getTextContent());
        NewWeapon.damageAmount = p_DamageFromString(CharacteristicNode.getChildNodes().item(4).getTextContent());
        String WeaponAbilityString = CharacteristicNode.getChildNodes().item(5).getTextContent();
        if(!WeaponAbilityString.equals("-"))
        {
           // NewWeapon.GetAbilities().addAll(WeaponAbilityString);
        }
        for(Ability ability : NewWeapon.GetAbilities())
        {
            if(ability.name.contains("grenade") ||ability.name.contains("Grenade")  )
            {
                NewWeapon.active = false;
            }
        }
        return(NewWeapon);
    }


    void p_ParseProfile(Node ProfileNode, Model ModelToModify)
    {
        if(ProfileNode.getAttributes().getNamedItem("typeName").getNodeValue().equals("Abilities"))
        {
            String AbilityName =ProfileNode.getAttributes().getNamedItem("name").getNodeValue();
            String AbilityDescription = "";
            Node CharacteristicsNode = p_FirsChildByType(ProfileNode,"characteristics");
            if(CharacteristicsNode != null)
            {
                Node DescriptionNode = p_FirsChildByType(CharacteristicsNode,"characteristic");
                if(DescriptionNode != null)
                {
                    AbilityDescription = DescriptionNode.getTextContent();
                }
            }
            //Ability.addModelAbility(ModelToModify,AbilityName,AbilityDescription);
            //Ability NewAbility = Ability.getAbilityType(ProfileNode.getAttributes().getNamedItem("name").getNodeValue());
            //ModelToModify.listOfAbilites.add(NewAbility);
        }
        else if(ProfileNode.getAttributes().getNamedItem("typeName").getNodeValue().equals("Unit"))
        {
            Node Characteristics = p_FirsChildByType(ProfileNode,"characteristics");
            String FirstChildValue = Characteristics.getChildNodes().item(0).getTextContent();
            ModelToModify.strength = p_ParseUnitStat(Characteristics.getChildNodes().item(3).getTextContent());
            ModelToModify.toughness = p_ParseUnitStat(Characteristics.getChildNodes().item(4).getTextContent());
            ModelToModify.wounds = p_ParseUnitStat(Characteristics.getChildNodes().item(5).getTextContent());
            ModelToModify.attacks = p_ParseUnitStat(Characteristics.getChildNodes().item(6).getTextContent());
            ModelToModify.armorSave = p_ParseUnitStat(Characteristics.getChildNodes().item(8).getTextContent());
        }
        else if(ProfileNode.getAttributes().getNamedItem("typeName").getNodeValue().equals("Weapon"))
        {
            ModelToModify.weapons.add(p_ParseWeapon(ProfileNode));
        }
    }
    ArrayList<Model> ParseModel(Node ModelNode)
    {
        ArrayList<Model> ReturnValue = new ArrayList<>();
        NamedNodeMap Attributes = ModelNode.getAttributes();
        Node NamedItem = Attributes.getNamedItem("number");
        String NodeValue = NamedItem.getNodeValue();
        int ModelCount = Integer.parseInt(ModelNode.getAttributes().getNamedItem("number").getNodeValue());
        Model BaseModel = new Model();
        BaseModel.name = ModelNode.getAttributes().getNamedItem("name").getNodeValue();
        if(p_FirsChildByType(ModelNode,"rules") != null)
        {
            BaseModel.GetAbilities().addAll( ParseRule(p_FirsChildByType(ModelNode,"rules")));
        }
        Node ProfileNode = p_FirsChildByType(ModelNode,"profiles");
        boolean ParsedStat = false;
        for(int i = 0; i < ProfileNode.getChildNodes().getLength();i++)
        {
            Node CurrentNode = ProfileNode.getChildNodes().item(i);
            if(CurrentNode.getAttributes().getNamedItem("typeName").getNodeValue().equals("Unit"))
            {
                if(!ParsedStat)
                {
                    p_ParseProfile(CurrentNode,BaseModel);
                }
                ParsedStat = true;
            }
            else
            {
                p_ParseProfile(CurrentNode,BaseModel);
            }
        }
        Node WeaponsNode = p_FirsChildByType(ModelNode,"selections");
        if(WeaponsNode != null)
        {
            for(int i = 0; i < WeaponsNode.getChildNodes().getLength();i++)
            {
                Node CurrentSelection = WeaponsNode.getChildNodes().item(i);
                String NodeName = CurrentSelection.getNodeName();
                //if(!NodeName.equals("selection"))
                //{
                //    continue;
                //}
                if(CurrentSelection.getAttributes().getNamedItem("type").getNodeValue().equals("upgrade"))
                {
                    Node ProfilesNode = p_FirsChildByType(CurrentSelection,"profiles");
                    if(ProfilesNode == null)
                    {
                        Node NewSelection = p_FirsChildByType(CurrentSelection,"selections");
                        if(NewSelection == null)
                        {
                            continue;
                        }
                        NewSelection = p_FirsChildByType(NewSelection,"selection");
                        if(NewSelection == null)
                        {
                            continue;
                        }
                        CurrentSelection = NewSelection;
                        ProfilesNode = p_FirsChildByType(CurrentSelection,"profiles");
                    }
                    if(ProfilesNode == null)
                    {
                        continue;
                    }
                    if(ProfilesNode.getChildNodes().getLength() > 1)
                    {
                        //HACKY AF, relcis can contain charecteristics, so 2 could be relic instead of alternative stats
                        //completelty breaks of the alternative stats are just 2 variatons however
                        if(ProfilesNode.getChildNodes().getLength() <= 2) {
                            Node CurrentProfile = p_FirsChildByType(ProfilesNode, "profile");
                            if (CurrentProfile == null) {
                                continue;
                            }
                            Node CharacteristicNode = p_FirsChildByType(CurrentProfile, "characteristics");
                            if (CharacteristicNode == null) {
                                continue;
                            }
                            if (CharacteristicNode.getChildNodes().getLength() < 6) {
                                BaseModel.GetAbilities().add(DatabaseManager.getInstance().GetAbility(CurrentProfile.getAttributes().getNamedItem("name").getNodeValue()));
                                continue;
                            }
                        }
                        if(CurrentSelection.getAttributes().getNamedItem("name").getNodeValue().contains("Stat Damage"))
                        {
                            //we are dealing with the alternative stats for a unit
                            Node FirstCharacterStats = p_FirsChildByType(p_FirsChildByType(ProfilesNode,"profile"),"characteristics");
                            BaseModel.attacks = p_ParseUnitStat(FirstCharacterStats.getChildNodes().item(3).getTextContent());
                            continue;
                        }
                    }
                    for(int j = 0; j < ProfilesNode.getChildNodes().getLength();j++)
                    {
                        Node CurrentProfile = ProfilesNode.getChildNodes().item(j);
                        if(!CurrentProfile.getNodeName().equals("profile"))
                        {
                            continue;
                        }
                        Node CharacteristicNode = p_FirsChildByType(CurrentProfile,"characteristics");
                        if(CharacteristicNode.getChildNodes().getLength() < 5)
                        {
                            String AbilityName =    CurrentProfile.getAttributes().getNamedItem("name").getNodeValue();
                            Ability NewAbility = DatabaseManager.getInstance().GetAbility(AbilityName);
                            continue;
                        }
                        Weapon NewWeapon = p_ParseWeapon(CurrentProfile);
                        int WeaponAmount = Integer.parseInt(CurrentSelection.getAttributes().getNamedItem("number").getNodeValue())/ModelCount;
                        for(int k = 0; k < WeaponAmount;k++)
                        {
                            BaseModel.weapons.add(NewWeapon);
                        }
                    }
                }
            }
        }

        for(int i = 0; i < ModelCount;i++)
        {
            // ReturnValue.add(new Model(BaseModel));
        }
        return(ReturnValue);
    }
    Unit ParseUnit(Node UnitNode)
    {
        Unit ReturnValue = new Unit();
        ReturnValue.unitName = UnitNode.getAttributes().getNamedItem("name").getNodeValue();
        Node SelectionsNode = p_FirsChildByType(UnitNode,"selections");
        Node ProfilesNode = p_FirsChildByType(UnitNode,"profiles");
        Model TemporaryModel = new Model();
        if(ProfilesNode != null)
        {
            for(int i = 0; i < ProfilesNode.getChildNodes().getLength();i++)
            {
                p_ParseProfile(ProfilesNode.getChildNodes().item(i),TemporaryModel);
            }
            ReturnValue.GetAbilities().addAll(TemporaryModel.GetAbilities());
            TemporaryModel.GetAbilities().clear();
            //for(int i = 0; i < ReturnValue.listOfModels.size();i++)
            //{
            //    ReturnValue.listOfModels.get(i).listOfRangedWeapons.addAll(TemporaryModel.listOfRangedWeapons);
            //}
        }
        for(int i = 0; i < SelectionsNode.getChildNodes().getLength();i++)
        {
            if(SelectionsNode.getChildNodes().item(i).getAttributes().getNamedItem("type").getNodeValue().equals("model"))
            {
                ArrayList<Model> NewModels = ParseModel(SelectionsNode.getChildNodes().item(i));
                if(TemporaryModel.strength != -1)
                {
                    for(Model ModelToModify : NewModels)
                    {
                       // TemporaryModel.CopyStats(ModelToModify);
                    }
                }
                ReturnValue.listOfModels.addAll(NewModels);
            }
            else if(SelectionsNode.getChildNodes().item(i).getAttributes().getNamedItem("type").getNodeValue().equals("unit"))
            {
                Unit SubUnit = ParseUnit(SelectionsNode.getChildNodes().item(i));
                for(Ability SubAbility : SubUnit.GetAbilities())
                {
                    for(Model SubModel : SubUnit.listOfModels)
                    {
                        SubModel.GetAbilities().add(SubAbility);
                    }
                }
                ReturnValue.listOfModels.addAll(SubUnit.listOfModels);
            }
            else if(SelectionsNode.getChildNodes().item(i).getAttributes().getNamedItem("type").getNodeValue().equals("upgrade"))
            {
                Node Child = SelectionsNode.getChildNodes().item(i);
                Node ChildProfiles = p_FirsChildByType(Child,"profiles");
                if(ChildProfiles == null)
                {
                    continue;
                }
                Node ChildProfileNode = p_FirsChildByType(ChildProfiles,"profile");
                if(ChildProfileNode == null)
                {
                    continue;
                }
                if(ChildProfileNode.getAttributes().getNamedItem("typeName").getNodeValue().equals("Abilities"))
                {
                    ReturnValue.GetAbilities().add(DatabaseManager.getInstance().GetAbility(Child.getAttributes().getNamedItem("name").getNodeValue()));
                }
                else if(ChildProfileNode.getAttributes().getNamedItem("typeName").getNodeValue().equals("Unit"))
                {
                    ReturnValue.listOfModels.addAll(ParseModel(SelectionsNode.getChildNodes().item(i)));
                }
                else if(ChildProfileNode.getAttributes().getNamedItem("typeName").getNodeValue().equals("Weapon"))
                {
                    for(int j = 0; j < ChildProfiles.getChildNodes().getLength();j++)
                    {
                        if(ChildProfiles.getChildNodes().item(j).getNodeName().equals("profile"))
                        {
                            TemporaryModel.weapons.add(p_ParseWeapon(ChildProfiles.getChildNodes().item(j)));
                        }
                    }
                }
            }
        }
        if(ReturnValue.listOfModels.size() == 0 && TemporaryModel.strength != -1)
        {
            int Number = Integer.parseInt(UnitNode.getAttributes().getNamedItem("number").getNodeValue());
            TemporaryModel.name = ReturnValue.unitName;
            for(int i = 0; i < Number;i++)
            {
                ReturnValue.listOfModels.add(TemporaryModel);
            }
        }
        else
        {
            for(int i = 0; i < ReturnValue.listOfModels.size();i++)
            {
                ReturnValue.listOfModels.get(i).weapons.addAll(TemporaryModel.weapons);
            }
        }
        return(ReturnValue);
    }
    ArrayList<Ability> ParseRule(Node RuleNode)
    {
        ArrayList<Ability> ReturnValue = new ArrayList<>();
        for(int i = 0; i < RuleNode.getChildNodes().getLength();i++)
        {
            String AbilityName = RuleNode.getChildNodes().item(i).getAttributes().getNamedItem("name").getNodeValue();
            ReturnValue.add(DatabaseManager.getInstance().GetAbility(AbilityName));
        }
        return(ReturnValue);
    }


}