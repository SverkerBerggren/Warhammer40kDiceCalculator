package core.FileHandling;

import java.util.ArrayList;

import core.Enums.Faction;
import core.Logging.Logging;
import core.Util.Pair;
public abstract class FileHandler {

    public static FileHandler instance;


    public abstract ArrayList< Pair<String,Faction>> GetXMLData();
    public abstract void UpdateBattlesScribeData(UpdateCallbackBsData callback);
    public static void SetFileHandler(FileHandler fileHandler)
    {
        if(instance != null)
        {
            Logging.d("File handling","Instance is already set");
        }
        instance = fileHandler;
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

    public abstract class BattleScribeUpdater
    {

    }
    public interface UpdateCallbackBsData {
        void onProgress(int current, int total, String filename);
        void onComplete(boolean didUpdate);
        void onError(Exception e);
    }
}
