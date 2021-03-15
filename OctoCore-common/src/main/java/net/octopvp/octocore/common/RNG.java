package net.octopvp.octocore.common;

import java.util.Random;

public class RNG {
    public static int getRandomInt(int min, int max){
        return (int) ((Math.random() * (max - min)) + min);
    }
    public static int getRandomIntWithTimeSeed(){
        return new Random(System.currentTimeMillis()).nextInt();
    }
}
