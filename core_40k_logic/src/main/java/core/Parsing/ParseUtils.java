package core.Parsing;

import java.io.File;
import java.util.Scanner;

import core.Enums.Faction;
import core.Logging.Logging;

public class ParseUtils {
    public static Faction getFactionFromName(String fileName)
    {
        String fileEnumName = Parsing.toEnumName(fileName);

        for( Faction faction : Faction.values())
        {
            String factionName = faction.name();
            if(fileEnumName.contains(factionName))
            {
                return faction;
            }
        }
        return  Faction.Unidentified;
    }

    public static String ReadFileAsString(String directory, String name )
    {
        String stringToReturn = "";
        try{
            stringToReturn = new Scanner(new File(directory + "/" + name)).useDelimiter("\\A").next();
        }
        catch (Exception e)
        {
            Logging.d("file handler","sket sig att lasa filen");
        }

        return stringToReturn;
    }

}
