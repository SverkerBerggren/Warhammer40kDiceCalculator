package server;

import core.Abilities.Ability;
import core.AbilityElementAdapter;
import core.DamageCalculation.RollResult;
import core.DamageCalculation.RollingLogic;
import core.DatabaseManager;
import core.DatasheetModeling.Army;
import core.FileHandling.BsDataUpdater;
import core.FileHandling.FileHandler;
import core.Logging.Logger;
import core.Logging.Logging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;

import core.Parsing.Parsing;
import io.javalin.Javalin;
import io.javalin.http.Context;
import server.Requests.CalculateDamageRequest;
import server.Response.ErrorResponse;

public class Main {

    private static  Gson gson;
    private static final RollingLogic calculator = new RollingLogic();

    private static File resolveDataDirectory() {
        String envPath = System.getenv("WH40K_DATA_DIR");
        if (envPath != null && !envPath.isBlank()) {
            return new File(envPath);
        }
        // sensible default for local dev — lives next to the working directory
        return new File(System.getProperty("user.dir"), "data/wh40k-11e");
    }
    public static void main(String[] args) {


        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Ability.class, new AbilityElementAdapter());
        gson = gsonBuilder.create();

        // Wire up your static Logging facade for server-side output
        Logging.setLogger((tag, msg) -> System.out.println("[" + tag + "] " + msg));

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> rule.anyHost()); // tighten this before going to production
            });
        }).start(7070);
        File bsDataDirectory = resolveDataDirectory();

        ServerFileHandler serverFileHandler = new ServerFileHandler(bsDataDirectory);
        BsDataUpdater bsDataUpdater = new BsDataUpdater(serverFileHandler,bsDataDirectory);
        bsDataUpdater.checkAndUpdate();
        Logger logger = (tag,message) -> {System.out.println(tag + " " + message);};
        Logging.setLogger(logger);

        FileHandler.SetFileHandler(serverFileHandler);

        DatabaseManager.InitializeDatabaseManager();

        app.post("/api/calculate-damage", Main::handleCalculateDamage);
        app.get("/api/implemented-abilities", Main::handleImplementedAbilities);
        app.post("/api/parse-army", Main::parseArmy);

        System.out.println("Server running on http://localhost:7070");
    }
    private static void parseArmy(Context ctx) {
        try{
            System.out.println("hej hej");
            JsonObject armyList = JsonParser.parseString(ctx.body()).getAsJsonObject();
            ctx.contentType("application/json");
            String name = armyList.get("name").getAsString();
            String list = armyList.get("list").getAsString();
            Parsing parser = new Parsing();
            Army army = parser.ParseGWListFormat(list);
            Logging.d("deez",name);
            army.name = name;
            ctx.result(gson.toJson(army));
        }
        catch (Exception e)
        {
            ctx.status(400);
            ctx.result(gson.toJson(new ErrorResponse(e.getMessage())));
        }
    }
    private static void handleImplementedAbilities(Context ctx) {
        try {
            ctx.contentType("application/json");
            ctx.result(gson.toJson(DatabaseManager.GetCatalog()));
        } catch (Exception e) {
            ctx.status(400);
            ctx.result(gson.toJson(new ErrorResponse(e.getMessage())));
        }
    }
    private static void handleCalculateDamage(Context ctx) {
        try {
            CalculateDamageRequest req = gson.fromJson(ctx.body(), CalculateDamageRequest.class);

            RollResult result = calculator.newCalculateDamage(
                    req.attackerList,
                    req.defendingUnit,
                    req.attackingArmy,
                    req.defendingArmy,
                    req.conditions,
                    10000
            );

            ctx.contentType("application/json");
            ctx.result(gson.toJson(result));
        } catch (Exception e) {
            ctx.status(400);
            ctx.result(gson.toJson(new ErrorResponse(e.getMessage())));
        }
    }
}