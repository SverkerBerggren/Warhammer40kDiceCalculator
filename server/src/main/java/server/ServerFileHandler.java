package server;

import static core.Parsing.ParseUtils.ReadFileAsString;
import static core.Parsing.ParseUtils.getFactionFromName;

import java.io.File;
import java.util.ArrayList;

import core.Enums.Faction;
import core.FileHandling.FileHandler;
import core.Logging.Logging;
import core.Util.Pair;
import sun.font.TrueTypeFont;

public class ServerFileHandler extends FileHandler {
    private File bsDataDirectory;
    private final String dataDir = "C:\\WarhammerDiceCalculator\\wh40k-10e";

    @Override
    public ArrayList< Pair<String,Faction>> GetXMLData()
    {
        bsDataDirectory = new File(dataDir);

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
    @Override
    public void UpdateBattlesScribeData(UpdateCallbackBsData updateCallbackBsData)
    {
        Logging.d("Todo","Does not yet update from the online repo");
        updateCallbackBsData.onComplete(true);
        //battleScribeUpdater.checkAndUpdate(updateCallbackBsData);
    }
}
