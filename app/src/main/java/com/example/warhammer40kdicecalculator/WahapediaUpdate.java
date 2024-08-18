package com.example.warhammer40kdicecalculator;


import android.util.Log;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;

class UpdateArgumentStruct
{
    public String URLPrefix = "";
    public String OutputPrefix = "";
    public ArrayList<String> FilesToDownload = new ArrayList<String>();
}

public class WahapediaUpdate
{


    public String UpdateFiles(UpdateArgumentStruct Arguments)
    {
      //  Log.d("Wahapedia grejen: ", "den uppdaterade bra");
        String ReturnValue = "Success!";
        try
        {
            for(String File : Arguments.FilesToDownload)
            {
                URL API_URL = new URL(Arguments.URLPrefix+File);
                HttpURLConnection con = (HttpURLConnection)API_URL.openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);

                DataInputStream HTTPInput = new DataInputStream( con.getInputStream() );

                OutputStream FileOutput = new FileOutputStream(Arguments.OutputPrefix+"/"+File);
                int ReadChunkSize = 4096;
                byte[] Buffer = new byte[ReadChunkSize];
                while(true)
                {
                    int ReadBytes = HTTPInput.read(Buffer);
                    FileOutput.write(Buffer,0,ReadBytes);
                    if(ReadBytes < ReadChunkSize)
                    {
                        break;
                    }
                }
                FileOutput.flush();
                FileOutput.close();
                HTTPInput.close();
            }
        }
        catch (Exception e)
        {
            Log.d ("Hej hej", e.getMessage());
            ReturnValue = "Error updating files: "+e.getMessage();
        }
        return(ReturnValue);
    }
}
