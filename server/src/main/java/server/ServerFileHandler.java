package server;

import static core.Parsing.ParseUtils.ReadFileAsString;
import static core.Parsing.ParseUtils.getFactionFromName;

import java.io.File;
import java.util.ArrayList;

import core.Enums.Faction;
import core.FileHandling.FileHandler;
import core.Logging.Logging;
import core.Util.Pair;

public class ServerFileHandler extends FileHandler {
    private File bsDataDirectory;

    public ServerFileHandler(File bsDataDirectory)
    {
        this.bsDataDirectory = bsDataDirectory;
    }
    @Override
    public ArrayList< Pair<String,Faction>> GetXMLData()
    {
        ArrayList<Pair<String,Faction>> retValues = new ArrayList<>();
        File[] files = bsDataDirectory.listFiles((dir, name) ->
                name.endsWith(".cat") || name.endsWith(".gst"));

        for (File file : files) {

            Faction faction = getFactionFromName(file.getName());

            retValues.add( new Pair<>(ReadFileAsString(bsDataDirectory.toString(),file.getName()),faction));

            if(faction == Faction.Unidentified)
            {
                Logging.d("Database source problem","Faction for file name could not be found " + file.getName());
            }
        }
        return  retValues;
    }
    public ArrayList< Pair<String,Faction>> GetJsonData()
    {
        ArrayList<Pair<String,Faction>> retValues = new ArrayList<>();
        File[] files = bsDataDirectory.listFiles((dir, name) ->
                name.endsWith(".json") || name.endsWith(".gst"));

        for (File file : files) {

            Faction faction = getFactionFromName(file.getName());

            retValues.add( new Pair<>(ReadFileAsString(bsDataDirectory.toString(),file.getName()),faction));

            if(faction == Faction.Unidentified)
            {
                Logging.d("Database source problem","Faction for file name could not be found " + file.getName());
            }
        }
        return  retValues;
    }
}
