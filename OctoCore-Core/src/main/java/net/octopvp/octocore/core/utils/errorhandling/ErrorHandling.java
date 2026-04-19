package net.octopvp.octocore.core.utils.errorhandling;

import net.octopvp.octocore.common.redis.packets.ServerCrashPacket;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.Utilities;
import net.octopvp.octocore.core.OctoCore;

import java.util.Date;
import java.util.concurrent.ExecutionException;

public class ErrorHandling {
    public static String formatError(Exception e) {
        StringBuilder sb = new StringBuilder(String.valueOf(e));
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            sb.append("\n").append("\tat ").append(stackTraceElement.toString());
        }
        return sb.toString();
    }

    public static String[] handleError(ErrorData e) {
        //TODO use logger
        String rng = Utilities.genRandomString(5).toUpperCase();
        e.addData("Time", new Date().toString());
        e.addData("CurrentTimeMillis", String.valueOf(System.currentTimeMillis()));
        e.addData("ID", rng);
        Logger.debug("Error detected. Posting to hastebin. Error code: " + rng);
        StringBuilder toHasteBin = new StringBuilder("---------------------------------------\nAny extra data that may be needed\n\n");
        e.getDescription().forEach(k -> {
            toHasteBin.append("\n" + k);
        });
        toHasteBin.append("\n\n");
        e.getErrorData().forEach((k, v) -> {
            toHasteBin.append("\n" + k + ": " + v);
        });
        toHasteBin.append("\n\n" + "---------------------------------------");
        toHasteBin.append("\nThe exception(s) that have occurred\n\n");
        e.getExceptions().forEach(e1 -> {
            String format = formatError(e1);
            toHasteBin.append(format).append("\n\n--------\n\n");
        });
        toHasteBin.append("\n\n---------------------------------------");
        
        try {
            new ServerCrashPacket(OctoCore.getServerName(), toHasteBin.toString()).send();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String hastebinLink;
        try {
            hastebinLink = new Hastebin().post(toHasteBin.toString(), false).get();
        } catch (InterruptedException | ExecutionException interruptedException) {
            //dont post this since it may become repeating
            interruptedException.printStackTrace();
            hastebinLink = "Failed to post to hastebin";
        }
        String[] ret = new String[]{rng, hastebinLink};
        Logger.debug("Error Code: " + rng + " | Hastebin link: " + hastebinLink + " logging to sql.");
        //TODO insert into a database (used to be mysql)
        return ret;
    }
}
