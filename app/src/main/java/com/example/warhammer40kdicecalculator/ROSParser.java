package com.example.warhammer40kdicecalculator;
import com.example.warhammer40kdicecalculator.Abilities.Ability;
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
            Node ArmyModels = p_FirsChildByType(ForceNode,"selections");
            ReturnValue.abilities.addAll(ParseRule(ArmyRules));
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

            }
        }
        catch(Exception e)
        {
            String Error = e.getMessage();
            String Error2 = e.getMessage();
        }
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
        for(int i = 0; i < ProfileNode.getChildNodes().getLength();i++)
        {
            Node CurrentNode = ProfileNode.getChildNodes().item(i);
            if(CurrentNode.getAttributes().getNamedItem("typeName").getNodeValue().equals("Abilities"))
            {
                Ability NewAbility = Ability.getAbilityType(CurrentNode.getAttributes().getNamedItem("name").getNodeValue());
            }
            else if(CurrentNode.getAttributes().getNamedItem("typeName").getNodeValue().equals("Unit"))
            {
                Node Characteristics = p_FirsChildByType(CurrentNode,"characteristics");
                String FirstChildValue = Characteristics.getChildNodes().item(0).getTextContent();
                BaseModel.weaponSkill =  p_ParseUnitStat(Characteristics.getChildNodes().item(1).getTextContent());
                BaseModel.ballisticSkill = p_ParseUnitStat(Characteristics.getChildNodes().item(2).getTextContent());
                BaseModel.strength = p_ParseUnitStat(Characteristics.getChildNodes().item(3).getTextContent());
                BaseModel.toughness = p_ParseUnitStat(Characteristics.getChildNodes().item(4).getTextContent());
                BaseModel.wounds = p_ParseUnitStat(Characteristics.getChildNodes().item(5).getTextContent());
                BaseModel.attacks = p_ParseUnitStat(Characteristics.getChildNodes().item(6).getTextContent());
                BaseModel.armorSave = p_ParseUnitStat(Characteristics.getChildNodes().item(8).getTextContent());
            }
        }
        Node WeaponsNode = p_FirsChildByType(ModelNode,"selections");
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
                    //we are dealing with the alternative stats for a unit
                    Node FirstCharacterStats = p_FirsChildByType(p_FirsChildByType(ProfilesNode,"profile"),"characteristics");
                    BaseModel.ballisticSkill = p_ParseUnitStat(FirstCharacterStats.getChildNodes().item(2).getTextContent());
                    BaseModel.attacks = p_ParseUnitStat(FirstCharacterStats.getChildNodes().item(3).getTextContent());
                    continue;
                }
                Node CurrentProfile = p_FirsChildByType(ProfilesNode,"profile");
                Node CharacteristicNode = p_FirsChildByType(CurrentProfile,"characteristics");
                if(CharacteristicNode.getChildNodes().getLength() < 5)
                {
                    String AbilityName =    CurrentProfile.getAttributes().getNamedItem("name").getNodeValue();
                    Ability NewAbility = null;
                    continue;
                }
                RangedWeapon NewWeapon = new RangedWeapon();
                NewWeapon.name =CurrentProfile.getAttributes().getNamedItem("name").getNodeValue();
                NewWeapon.amountOfAttacks = new RangedAttackAmount(p_DamageFromString(CharacteristicNode.getChildNodes().item(1).getTextContent()));
                NewWeapon.strength = p_ParseUnitStat(CharacteristicNode.getChildNodes().item(2).getTextContent());
                NewWeapon.ap = p_ParseUnitStat(CharacteristicNode.getChildNodes().item(3).getTextContent());
                NewWeapon.damageAmount = p_DamageFromString(CharacteristicNode.getChildNodes().item(4).getTextContent());

                int WeaponAmount = Integer.parseInt(CurrentSelection.getAttributes().getNamedItem("number").getNodeValue())/ModelCount;
                for(int j = 0; j < WeaponAmount;j++)
                {
                    BaseModel.listOfRangedWeapons.add(NewWeapon);
                }
            }
        }

        for(int i = 0; i < ModelCount;i++)
        {
            ReturnValue.add(BaseModel);
        }
        return(ReturnValue);
    }
    Unit ParseUnit(Node UnitNode)
    {
        Unit ReturnValue = new Unit();
        ReturnValue.unitName = UnitNode.getAttributes().getNamedItem("name").getNodeValue();
        Node SelectionsNode = p_FirsChildByType(UnitNode,"selections");
        for(int i = 0; i < SelectionsNode.getChildNodes().getLength();i++)
        {
            if(SelectionsNode.getChildNodes().item(i).getAttributes().getNamedItem("type").getNodeValue().equals("model"))
            {
                ReturnValue.listOfModels.addAll(ParseModel(SelectionsNode.getChildNodes().item(i)));
            }
        }
        return(ReturnValue);
    }
    ArrayList<Ability> ParseRule(Node RuleNode)
    {
        ArrayList<Ability> ReturnValue = new ArrayList<>();
        for(int i = 0; i < RuleNode.getChildNodes().getLength();i++)
        {
            Ability abilityToAdd = null;
            String AbilityName = RuleNode.getChildNodes().item(i).getAttributes().getNamedItem("name").getNodeValue();
            Ability abilityFromName = Ability.getAbilityType(AbilityName);

            abilityToAdd = abilityFromName;

            if(abilityFromName == null)
            {
                Ability abilityStub = new Ability(AbilityName) {
                    @Override
                    public void hitRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking) {

                    }

                    @Override
                    public void woundRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking) {

                    }

                    @Override
                    public void saveRollAbility(DiceResult diceResult, MetricsOfAttacking metricsOfAttacking) {

                    }

                    @Override
                    public void rollNumberOfShots(List<DiceResult> diceResult, MetricsOfAttacking metricsOfAttacking) {

                    }
                };

                abilityStub.name = AbilityName;

                abilityToAdd = abilityStub;
            }
            ReturnValue.add(abilityToAdd);
        }
        return(ReturnValue);
    }


}
