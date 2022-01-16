package net.octopvp.octocore.paper.utils.errorhandling;

import net.octopvp.octocore.common.RNG;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.DatabaseHelper;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class ErrorHandling {
    public static String formatError(Exception e){
        StringBuilder sb = new StringBuilder(e + "");
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            sb.append("\n").append("\tat ").append(stackTraceElement.toString());
        }
        return sb.toString();
    }
    public static String[] handleError(ErrorData e)  {
        //TODO use logger
        String rng = RNG.genRandomString(5).toUpperCase();
        e.addData("Time",new Date().toString());
        e.addData("CurrentTimeMillis",System.currentTimeMillis() + "");
        e.addData("ID",rng);
        Logger.debug("Error detected. Posting to hastebin. Error code: " + rng);
        StringBuilder toHasteBin = new StringBuilder("---------------------------------------\nAny extra data that may be needed\n\n");
        e.getDescription().forEach(k ->{
            toHasteBin.append("\n" + k);
        });
        toHasteBin.append("\n\n");
        e.getErrorData().forEach((k,v)->{
            toHasteBin.append("\n" + k + ": " + v);
        });
        toHasteBin.append("\n\n" + "---------------------------------------");
        toHasteBin.append("\nThe exception(s) that have occurred\n\n");
        e.getExceptions().forEach(e1 -> {
            String format = formatError(e1);
            toHasteBin.append(format).append("\n\n--------\n\n");
        });
        toHasteBin.append("\n\n---------------------------------------");
        String hastebinLink;
        try {
            hastebinLink = new Hastebin().post(toHasteBin.toString(),false).get();
        } catch (InterruptedException | ExecutionException interruptedException) {
            //dont post this since it may become repeating
            interruptedException.printStackTrace();
            hastebinLink = "Failed to post to hastebin";
        }
        String[] ret = new String[]{rng,hastebinLink};
        Logger.debug("Error Code: " + rng + " | Hastebin link: " + hastebinLink + " logging to sql.");
        try {
            OctoCore.getConnection().prepareStatement(DatabaseHelper.ADD_ERROR_DATA.getSql(rng,hastebinLink));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ret;
    }
}
