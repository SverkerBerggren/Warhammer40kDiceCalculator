package com.example.warhammer40kdicecalculator;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.warhammer40kdicecalculator.Abilities.Ability;
import com.example.warhammer40kdicecalculator.Abilities.AbilityStub;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Army;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Model;
import com.example.warhammer40kdicecalculator.DatasheetModeling.RangedAttackAmount;
import com.example.warhammer40kdicecalculator.DatasheetModeling.RangedWeapon;
import com.example.warhammer40kdicecalculator.DatasheetModeling.Unit;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ROSParser
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
    public com.example.warhammer40kdicecalculator.DatasheetModeling.Army ParseArmy(String ArmyData)
    {
        com.example.warhammer40kdicecalculator.DatasheetModeling.Army ReturnValue = new Army();
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
                ReturnValue.abilities.addAll(ParseRule(ArmyRules));
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
                        ReturnValue.abilities.add(NewArmyRule);
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

        ReturnValue = Ability.getAbilityType(RuleSelection.getAttributes().getNamedItem("name").getNodeValue());
        return(ReturnValue);
    }
    DamageAmount p_DamageFromString(String StringToConvert)
    {
        DamageAmount ReturnValue = new DamageAmount();
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
                        ReturnValue.d6DamageAmount = CurrentInteger == -1 ? 1 : CurrentInteger;
                    }
                    else if(StringToConvert.charAt(ParseOffset) == '3')
                    {
                        ReturnValue.d3DamageAmount = CurrentInteger == -1 ? 1 : CurrentInteger;
                    }
                    ParseOffset += 1;
                    CurrentInteger = -1;
                }
            }
            else if(StringToConvert.charAt(ParseOffset) == '+')
            {
                if(CurrentInteger != -1)
                {
                    ReturnValue.rawDamageAmount = CurrentInteger;
                }
                ParseOffset += 1;
            }
            else
            {
                CurrentInteger = Character.getNumericValue(StringToConvert.charAt(ParseOffset));
                ParseOffset +=1;
            }
        }
        if(CurrentInteger != -1)
        {


            ReturnValue.rawDamageAmount = CurrentInteger;
        }
        return(ReturnValue);
    }
    void p_ParseWeaponType(RangedWeapon ResultWeapon,String TypeString)
    {
        if(TypeString.equals("Melee"))
        {
            ResultWeapon.IsMelee = true;
            ResultWeapon.amountOfAttacks.rawNumberOfAttacks = 1;
            return;
        }
        int ParseOffset = 0;
        while(ParseOffset < TypeString.length())
        {
            char CurrentChar = TypeString.charAt(ParseOffset);
            if(CurrentChar == 'D' || CurrentChar >= '0' && CurrentChar <= '9')
            {
                break;
            }
            ParseOffset += 1;
        }
        if(ParseOffset != 0)
        {
            String AbilityString = TypeString.substring(0,ParseOffset);
            if(AbilityString.contains("/"))
            {
                ResultWeapon.weaponRules.add(Ability.getAbilityType(TypeString.substring(0,ParseOffset)));
            }
        }
        ResultWeapon.amountOfAttacks =new RangedAttackAmount(p_DamageFromString(TypeString));
    }
    RangedWeapon p_ParseWeapon(Node ProfileNode)
    {
        Node CharacteristicNode = p_FirsChildByType(ProfileNode,"characteristics");
        RangedWeapon NewWeapon = new RangedWeapon();
        NewWeapon.name =ProfileNode.getAttributes().getNamedItem("name").getNodeValue();

        p_ParseWeaponType(NewWeapon,CharacteristicNode.getChildNodes().item(1).getTextContent());

        NewWeapon.strength = p_ParseUnitStat(CharacteristicNode.getChildNodes().item(2).getTextContent());
        NewWeapon.ap = p_ParseUnitStat(CharacteristicNode.getChildNodes().item(3).getTextContent());
        NewWeapon.damageAmount = p_DamageFromString(CharacteristicNode.getChildNodes().item(4).getTextContent());
        String WeaponAbilityString = CharacteristicNode.getChildNodes().item(5).getTextContent();
        if(!WeaponAbilityString.equals("-"))
        {
            NewWeapon.weaponRules.addAll(Ability.getWeaponAbilities(WeaponAbilityString));
        }
        return(NewWeapon);
    }

    void p_ParseProfile(Node ProfileNode,Model ModelToModify)
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
            Ability.addModelAbility(ModelToModify,AbilityName,AbilityDescription);
            //Ability NewAbility = Ability.getAbilityType(ProfileNode.getAttributes().getNamedItem("name").getNodeValue());
            //ModelToModify.listOfAbilites.add(NewAbility);
        }
        else if(ProfileNode.getAttributes().getNamedItem("typeName").getNodeValue().equals("Unit"))
        {
            Node Characteristics = p_FirsChildByType(ProfileNode,"characteristics");
            String FirstChildValue = Characteristics.getChildNodes().item(0).getTextContent();
            ModelToModify.weaponSkill =  p_ParseUnitStat(Characteristics.getChildNodes().item(1).getTextContent());
            ModelToModify.ballisticSkill = p_ParseUnitStat(Characteristics.getChildNodes().item(2).getTextContent());
            ModelToModify.strength = p_ParseUnitStat(Characteristics.getChildNodes().item(3).getTextContent());
            ModelToModify.toughness = p_ParseUnitStat(Characteristics.getChildNodes().item(4).getTextContent());
            ModelToModify.wounds = p_ParseUnitStat(Characteristics.getChildNodes().item(5).getTextContent());
            ModelToModify.attacks = p_ParseUnitStat(Characteristics.getChildNodes().item(6).getTextContent());
            ModelToModify.armorSave = p_ParseUnitStat(Characteristics.getChildNodes().item(8).getTextContent());
        }
        else if(ProfileNode.getAttributes().getNamedItem("typeName").getNodeValue().equals("Weapon"))
        {
            ModelToModify.listOfRangedWeapons.add(p_ParseWeapon(ProfileNode));
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
            BaseModel.listOfAbilites = ParseRule(p_FirsChildByType(ModelNode,"rules"));
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
                                BaseModel.listOfAbilites.add(Ability.getAbilityType(CurrentProfile.getAttributes().getNamedItem("name").getNodeValue()));
                                continue;
                            }
                        }
                        if(CurrentSelection.getAttributes().getNamedItem("name").getNodeValue().contains("Stat Damage"))
                        {
                            //we are dealing with the alternative stats for a unit
                            Node FirstCharacterStats = p_FirsChildByType(p_FirsChildByType(ProfilesNode,"profile"),"characteristics");
                            BaseModel.ballisticSkill = p_ParseUnitStat(FirstCharacterStats.getChildNodes().item(2).getTextContent());
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
                            Ability NewAbility = Ability.getAbilityType(AbilityName);
                            continue;
                        }
                        RangedWeapon NewWeapon = p_ParseWeapon(CurrentProfile);
                        int WeaponAmount = Integer.parseInt(CurrentSelection.getAttributes().getNamedItem("number").getNodeValue())/ModelCount;
                        for(int k = 0; k < WeaponAmount;k++)
                        {
                            for(Ability ability : NewWeapon.weaponRules)
                            {
                                if(ability.name.contains("grenade") ||ability.name.contains("Grenade")  )
                                {
                                    NewWeapon.active = false;
                                }
                            }
                            BaseModel.listOfRangedWeapons.add(NewWeapon);
                        }
                    }
                }
            }
        }

        for(int i = 0; i < ModelCount;i++)
        {
            ReturnValue.add(new Model(BaseModel));
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
            ReturnValue.listOfAbilitys.addAll(TemporaryModel.listOfAbilites);
            for(int i = 0; i < ReturnValue.listOfModels.size();i++)
            {
                ReturnValue.listOfModels.get(i).listOfRangedWeapons.addAll(TemporaryModel.listOfRangedWeapons);
            }
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
                        TemporaryModel.CopyStats(ModelToModify);
                    }
                }
                ReturnValue.listOfModels.addAll(NewModels);
            }
            else if(SelectionsNode.getChildNodes().item(i).getAttributes().getNamedItem("type").getNodeValue().equals("unit"))
            {
                Unit SubUnit = ParseUnit(SelectionsNode.getChildNodes().item(i));
                for(Ability SubAbility : SubUnit.listOfAbilitys)
                {
                    for(Model SubModel : SubUnit.listOfModels)
                    {
                        SubModel.listOfAbilites.add(SubAbility);
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
                    ReturnValue.listOfAbilitys.add(Ability.getAbilityType(Child.getAttributes().getNamedItem("name").getNodeValue()));
                }
                else if(ChildProfileNode.getAttributes().getNamedItem("typeName").getNodeValue().equals("Unit"))
                {
                    ReturnValue.listOfModels.addAll(ParseModel(SelectionsNode.getChildNodes().item(i)));
                }
            }
        }
       // Node ProfilesNode = p_FirsChildByType(UnitNode,"profiles");
        if(ProfilesNode != null)
        {
         //   Model TemporaryModel = new Model();
            for(int i = 0; i < ProfilesNode.getChildNodes().getLength();i++)
            {
                p_ParseProfile(ProfilesNode.getChildNodes().item(i),TemporaryModel);
            }
            ReturnValue.listOfAbilitys.addAll(TemporaryModel.listOfAbilites);
            for(int i = 0; i < ReturnValue.listOfModels.size();i++)
            {
                ReturnValue.listOfModels.get(i).listOfRangedWeapons.addAll(TemporaryModel.listOfRangedWeapons);
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
            ReturnValue.add(Ability.getAbilityType(AbilityName));
        }
        return(ReturnValue);
    }


}
