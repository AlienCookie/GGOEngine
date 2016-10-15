package Gameplay;

/**
 * Created by andri on 28-Aug-16.
 */
public class FigureIntersection {
    static boolean cubeIntersection(float[] first, float[] second){
        if(Math.abs((first[6] + first[0]) - (second[6] + second[0])) < Math.abs(first[6] - first[0]) + Math.abs(second[6] - second[0]))
            if(Math.abs((first[7] + first[1]) - (second[7] + second[1])) < Math.abs(first[7] - first[1]) + Math.abs(second[7] - second[1]))
                return true;
        return false;
    }
}
