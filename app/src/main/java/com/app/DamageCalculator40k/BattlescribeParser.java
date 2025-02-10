package com.app.DamageCalculator40k;


import android.util.Pair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

class BattlescribeUnit
{
    public String Name = "";
    public int Points = 0;
    public  ArrayList<String> Attributes = new ArrayList<>();
    public ArrayList<BattlescribeModel> Models = new ArrayList<>();
}
class BattlescribeModel
{
    public String ModelName = "";
    public ArrayList<Pair<String,Integer>> Weapons = new ArrayList<>();

    public BattlescribeModel()
    {

    }
    public  BattlescribeModel(BattlescribeModel ModelToCopy)
    {
        ModelName = ModelToCopy.ModelName;
        Weapons = new ArrayList<Pair<String,Integer>>(ModelToCopy.Weapons);
    }
}

class LinePeaker
{
    Scanner m_InternalScanner = null;
    String m_CurrentLine = "";
    boolean m_LineBuffered = false;
    boolean m_Finished = false;


    public  LinePeaker(Scanner ScannerToUse)
    {
        m_InternalScanner = ScannerToUse;
    }

    public String PeekLine()
    {
        if(m_LineBuffered == false)
        {
            m_CurrentLine = GetNextLine();
            m_LineBuffered = true;
        }
        return(m_CurrentLine);
    }
    public String GetNextLine()
    {
        if(m_LineBuffered == true)
        {
            m_LineBuffered = false;
            return(m_CurrentLine);
        }
        String ReturnValue = null;
        if(m_InternalScanner.hasNextLine())
        {
            ReturnValue = m_InternalScanner.nextLine();
        }
        if(ReturnValue == null)
        {
            m_Finished = true;
        }
        return(ReturnValue);
    }
    boolean Finished()
    {
        return(m_Finished);
    }
}

public class BattlescribeParser
{

    boolean m_Finished = false;

    String p_GetNextSection(LinePeaker LineRetriever)
    {
        String ReturnValue = "";
        while(!LineRetriever.Finished())
        {
            String CurrentLine = LineRetriever.GetNextLine();
            if(CurrentLine != null && (CurrentLine.length() > 0 && CurrentLine.substring(0,2).equals("+ ")))
            {
                if(CurrentLine.substring(2,CurrentLine.length()-2).equals("Configuration"))
                {
                    continue;
                }
                if(CurrentLine.substring(2,CurrentLine.length()-2).equals("Stratagems"))
                {
                    continue;
                }
                LineRetriever.GetNextLine();
                break;
            }
        }
        return(ReturnValue);
    }
    void p_SkipEmpty(LinePeaker LineRetriever)
    {
        while(!LineRetriever.Finished())
        {
            String CurrentLine = LineRetriever.PeekLine();
            if(CurrentLine.equals(""))
            {
                LineRetriever.GetNextLine();
            }
            else
            {
                break;
            }
        }
    }
    int p_ParseCount(String StringToParse)
    {
        int ReturnValue = 1;
        String[] Parts = StringToParse.split(" ");
        ArrayList<String> UpdatedParts = new ArrayList<>();
        for(String Part : Parts)
        {
            if(!Part.equals(""))
            {
                UpdatedParts.add(Part);
            }
        }

        if(UpdatedParts.get(0).length() > 0 && UpdatedParts.get(0).charAt(UpdatedParts.get(0).length()-1) == 'x')
        {
            ReturnValue = Integer.parseInt(UpdatedParts.get(0).substring(0,UpdatedParts.get(0).length()-1));
        }
        return(ReturnValue);
    }
    ArrayList<BattlescribeModel> p_ParseModel(String ModelLine)
    {
        ArrayList<BattlescribeModel>  ReturnValue = new ArrayList<BattlescribeModel> ();
        int EndOfModelName = ModelLine.indexOf(':');
        BattlescribeModel NewModel = new BattlescribeModel();
        int NumberOfModels = 1;
        ArrayList<Pair<String,Integer>> Attributes = new ArrayList<Pair<String,Integer>>();
        if(EndOfModelName == -1)
        {
            EndOfModelName = ModelLine.length();
        }
        else
        {
            String[] Weapons = ModelLine.substring(EndOfModelName+1).split(",");
            for(String Weapon : Weapons)
            {
                int NumberOfWeapons = p_ParseCount(Weapon);
                if(NumberOfWeapons > 1)
                {
                    Weapon = Weapon.substring(Weapon.indexOf('x')+2);
                }
                Attributes.add(new Pair<String,Integer>(Weapon,NumberOfWeapons));
            }
        }
        NewModel.ModelName = ModelLine.substring(2,EndOfModelName);
        NumberOfModels = p_ParseCount(NewModel.ModelName);
        if(NumberOfModels > 1)
        {
            NewModel.ModelName = NewModel.ModelName.substring(NewModel.ModelName.indexOf(' ')+1);
        }
        for(Pair<String,Integer> Attribute: Attributes)
        {
            if(Attribute.second > 1)
            {
                NewModel.Weapons.add(new Pair<String,Integer>(Attribute.first,Attribute.second/ NumberOfModels));
                //Attribute.second = new Integer(Attribute.second.intValue()/ NumberOfModels);
            }
            else
            {
                NewModel.Weapons.add(new Pair<String,Integer>(Attribute.first,Attribute.second));
            }
        }
        for (int i = 0; i < NumberOfModels;i++)
        {
            ReturnValue.add(new BattlescribeModel(NewModel));
        }
        return(ReturnValue);
    }
    ArrayList<BattlescribeModel> p_ParseSubModels(LinePeaker LineRetriever)
    {
        ArrayList<BattlescribeModel> ReturnValue = new ArrayList<>();
        while(true)
        {
            String CurrentLine = LineRetriever.GetNextLine();
            if(CurrentLine == null || CurrentLine.equals(""))
            {
                break;
            }
            ArrayList<BattlescribeModel> NewModels = p_ParseModel(CurrentLine);
            ReturnValue.addAll(NewModels);
        }
        return(ReturnValue);
    }
    String p_ParseName(String ModelHeader)
    {
        String ReturnValue = "";
        int PointBegin = ModelHeader.indexOf('[');
        ReturnValue = ModelHeader.substring(0,PointBegin);
        return(ReturnValue);
    }
    int p_ParsePoints(String ModelHeader)
    {
        int ReturnValue = 0;
        int PointBegin = ModelHeader.indexOf('[');
        int PointEnd = ModelHeader.indexOf(']');
        String[] Parts =ModelHeader.substring(PointBegin,PointEnd).split(",");
        String PointString = Parts[Parts.length-1];
        String IntString = PointString.substring(1,PointString.length()-3);
        ReturnValue = Integer.parseInt(IntString);
        return(ReturnValue);
    }
    ArrayList<String> p_ParseUnitAttributes(String UnitHeader)
    {
        ArrayList<String> ReturnValue = new ArrayList<>();
        int AttributeBegin = UnitHeader.indexOf(':');
        if(AttributeBegin == -1)
        {
            return(ReturnValue);
        }
        String[] Result = UnitHeader.substring(AttributeBegin+1,UnitHeader.length()).split(",");
        ReturnValue.addAll(Arrays.asList(Result));
        return(ReturnValue);
    }
    BattlescribeUnit p_ParseUnit(LinePeaker LineRetriever)
    {
        BattlescribeUnit ReturnValue = new BattlescribeUnit();
        String ModelHeader = LineRetriever.GetNextLine();
        ReturnValue.Name = p_ParseName(ModelHeader);
        ReturnValue.Points = p_ParsePoints(ModelHeader);
        ReturnValue.Attributes = p_ParseUnitAttributes(ModelHeader);
        ReturnValue.Models = p_ParseSubModels(LineRetriever);
        if(ReturnValue.Models.size() == 0)
        {
            BattlescribeModel NewModel = new BattlescribeModel();
            NewModel.ModelName = ReturnValue.Name;
            //NewModel.Weapons = new ArrayList<>(ReturnValue.Attributes);
            for(String Attribute : ReturnValue.Attributes){
                NewModel.Weapons.add(new Pair<>(Attribute,1));
            }
            ReturnValue.Models.add(NewModel);
        }
        return(ReturnValue);
    }
    void p_ParseUnits(LinePeaker LineRetriever,ArrayList<BattlescribeUnit> UnitsToAppend)
    {
        while(!LineRetriever.Finished())
        {
            p_SkipEmpty(LineRetriever);
            String NextLine = LineRetriever.PeekLine();
            if(NextLine == null || (NextLine.length() > 0 && NextLine.charAt(0) == '+'))
            {
                break;
            }
            UnitsToAppend.add(p_ParseUnit(LineRetriever));
        }
    }

    public  ArrayList<BattlescribeUnit> ParseUnits(InputStream Input) throws Exception
    {
        m_Finished = false;
        ArrayList<BattlescribeUnit> ReturnValue = new ArrayList<>();
        Scanner LineRetriever = new Scanner(Input);
        LinePeaker Peaker = new LinePeaker(LineRetriever);
        while(!Peaker.Finished())
        {
            String NextSection = p_GetNextSection(Peaker);
            p_ParseUnits(Peaker,ReturnValue);
        }
        return(ReturnValue);
    }
}
