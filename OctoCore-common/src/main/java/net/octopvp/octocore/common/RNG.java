package net.octopvp.octocore.common;

public class RNG {
    public static int getRandomInt(int min, int max){
        return (int) ((Math.random() * (max - min)) + min);
    }
}
