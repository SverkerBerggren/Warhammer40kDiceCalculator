package com.example.warhammer40kdicecalculator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.Function;

import com.example.warhammer40kdicecalculator.WahapediaUpdate;

class Runnable_Function<FunctionArgument,FunctionReturnValue> implements Runnable
{
    Function<FunctionArgument,FunctionReturnValue> m_FunctionToRun = null;
    FunctionArgument m_ArgumentToUse = null;
    Runnable_Function(Function<FunctionArgument,FunctionReturnValue> FunctionToRun,FunctionArgument ArgumentToUse)
    {
        m_ArgumentToUse = ArgumentToUse;
        m_FunctionToRun = FunctionToRun;
    }
    //@RequiresApi(api = Build.VERSION_CODES.N)
    public void run()
    {
        m_FunctionToRun.apply(m_ArgumentToUse);
    }
}
class Callback_Runner <RunArgumentType,RunResultType,CallbackResultType> implements Runnable
{
    Context m_AssociatedContext = null;
    private Function<RunResultType,CallbackResultType> m_FunctionCallback = null;
    private Function<RunArgumentType,RunResultType> m_CodeToRun = null;
    private RunArgumentType m_RunArgument = null;
    Callback_Runner(Context AssociatedContext, Function<RunResultType,CallbackResultType> Callback, Function<RunArgumentType,RunResultType> CodeToRun, RunArgumentType RunArgument)
    {
        m_AssociatedContext = AssociatedContext;
        m_FunctionCallback = Callback;
        m_CodeToRun = CodeToRun;
        m_RunArgument = RunArgument;
    }
    public void run()
    {
        RunResultType RunReturnValue = m_CodeToRun.apply(m_RunArgument);
        new Handler(m_AssociatedContext.getMainLooper()).post(new Runnable_Function<RunResultType,CallbackResultType>(m_FunctionCallback,RunReturnValue));
    }
}

public class MainActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        try {
            InputStream Input = this.getAssets().open("TestParing.txt");
            BattlescribeParser Parser = new BattlescribeParser();
            ArrayList<BattlescribeUnit> Models = Parser.ParseUnits(Input);

            int Hej2 = 0;
        }
        catch (Exception e)
        {
            String Error = e.getMessage();
            int hej = 2;
        }

        EXAMPLEUPDATE();
    }

    Integer p_UpdateCallback(String Result)
    {
        Toast.makeText(this,"Update result: "+Result,Toast.LENGTH_SHORT).show();
        return(0);
    }

    void EXAMPLEUPDATE()
    {
        WahapediaUpdate Updater = new WahapediaUpdate();
        UpdateArgumentStruct Arguments = new UpdateArgumentStruct();
        Arguments.URLPrefix = "https://wahapedia.ru/wh40k9ed/factions/aeldari/";
        Arguments.OutputPrefix = this.getDataDir().toString();
        Arguments.FilesToDownload.add("Asurmen");
        Arguments.FilesToDownload.add("Baharroth");

        Thread UploadDataThread = new Thread(new Callback_Runner<UpdateArgumentStruct,String,Integer>(this,this::p_UpdateCallback,Updater::UpdateFiles,Arguments));
        UploadDataThread.start();
    }

    public void printTest(View v)
    {
        Log.d("aboo", "printTest: ");
        TestLasaCsv readCsvTest = new TestLasaCsv(context);
       // readCsvTest.testReadFile();
        ArrayList<ArrayList<String>> dataSheetData = readCsvTest.ReadCsvFile("Datasheets.csv");
        Log.d("Csv tester", "" + dataSheetData.get(5).get(1));

        RollingLogic hej = new RollingLogic();
        getAssets();
        hej.TestOmFungerar();
        DataSheet conscript = new DataSheet(5,3,90,1,1,0,3,5,-1);
        DataSheet spaceMarine = new DataSheet(3,4,1,2,1,1,4,3,-1);
    //    conscript.hammerOfEmperor = true;
   //     hej.CalculateDamage(conscript,spaceMarine);

        RangedWeapon lasgun = new RangedWeapon(3,0,1,new RangedAttackAmount(1,0,0));

        Unit conscripts = new Unit();

        Model guardsman = new Model();
        guardsman.ballisticSkill = 5;
        guardsman.listOfRangedWeapons.add(lasgun);

        for(int i = 0; i < 100; i++)
        {
            conscripts.listOfModels.add(guardsman);
        }


        Unit spaceMarineIntercessorUnit = new Unit();
        Model intercessor = new Model();
        intercessor.wounds = 2;
        intercessor.armorSave = 3;

        for(int i =0; i <10; i ++)
        {
            spaceMarineIntercessorUnit.listOfModels.add(intercessor);
        }

        hej.newCalculateDamage(conscripts,spaceMarineIntercessorUnit);


    }
}