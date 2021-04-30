package net.octopvp.octocore.paper.utils.errorhandling;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class ErrorData {
    /**
     * Extra error data
     */
    private HashMap<String,String> errorData = new HashMap<>();
    /**
     * The exeptions
     */
    private ArrayList<Exception> exceptions = new ArrayList<>();

    private ArrayList<String> description = new ArrayList<>();
    /**
     * Adds extra data to the top
     * @param key
     * @param value
     */
    public void addData(String key, String value){
        errorData.put(key,value);
    }

    public void addDescription(String desc){description.add(desc);}

    /**
     * Adds a exception to where the stacktrace is
     * @param e
     */
    public void addException(Exception e){
        exceptions.add(e);
    }
}
