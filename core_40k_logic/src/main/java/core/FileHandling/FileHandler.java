package core.FileHandling;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;

import core.Enums.Faction;
import core.Logging.Logging;
import core.Util.Pair;
public abstract class FileHandler {

    public static FileHandler instance;


    public abstract ArrayList< Pair<String,Faction>> GetXMLData();
    public abstract ArrayList< Pair<String,Faction>> GetJsonData();

    public static void SetFileHandler(FileHandler fileHandler)
    {
        if(instance != null)
        {
            Logging.d("File handling","Instance is already set");
        }
        instance = fileHandler;
    }

    public void SaveTextFile(File directory, String name, String content )
    {
        try {
            FileWriter writer = new FileWriter(new File(directory, name));
            writer.write( content);
            writer.flush();
            writer.close();
        }
        catch (Exception e)
        {
            Logging.d("FileHandler","sket sig nar det skulle sparas localt senaste updaterat");
        }
    }

    public static FileHandler GetInstance( )
    {
        if(instance == null)
        {
            Logging.d("File handler knas", "Trying to access the filehandler before it is initialized");
            return null;
        }
        return instance;
    }


}
