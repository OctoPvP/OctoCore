package net.octopvp.octocore.common;

import java.util.Random;

public class RNG {
    public static int getRandomInt(int min, int max){
        return (int) ((Math.random() * (max - min)) + min);
    }
    public static int getRandomIntWithTimeSeed(){
        return new Random(System.currentTimeMillis()).nextInt();
    }
    public static String genRandomString(int targetStringLength){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
}
