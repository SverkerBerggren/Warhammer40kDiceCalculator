package core.Logging;

public class Logging {
    private static Logger logger = (tag, message) -> { /* no-op until configured */ };

    public static void setLogger(Logger impl) {
        logger = impl;
    }

    public static void d(String tag, String message) {
        logger.debug(tag, message);
    }
}
