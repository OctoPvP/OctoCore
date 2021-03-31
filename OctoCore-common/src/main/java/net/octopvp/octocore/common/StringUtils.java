package net.octopvp.octocore.common;

public class StringUtils {
    public static String arraytoString(String[] args){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < args.length; i++) {
            sb.append(" " + args[i]);
        }
        return sb.toString();
    }
    public static String[] stringToArray(String input,String delim){
        return input.split(delim);
    }
    public static String replaceIgnoreCase(String what_to_replace,String replace_to,String input){
        return input.replaceAll("(?i)" + what_to_replace,replace_to);
    }
}
