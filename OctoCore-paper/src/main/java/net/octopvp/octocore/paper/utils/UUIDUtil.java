package net.octopvp.octocore.paper.utils;

import java.util.UUID;

public class UUIDUtil {
    public static UUID addDashes(String unformatted){ // ty https://stackoverflow.com/questions/18986712/creating-a-uuid-from-a-string-with-no-dashes
        return UUID.fromString(
                unformatted
                        .replaceFirst(
                                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
                        )
        );
    }
}
