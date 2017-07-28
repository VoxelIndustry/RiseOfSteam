package net.qbar.common.util;

import java.util.Random;

public class MathUtils
{

    /**
     * Return a random int between the specified minimum and maximum values
     * (inclusives)
     * 
     * @param min
     * @param max
     */
    public static int randBetweenRange(Random rand, int min, int max)
    {
        return rand.nextInt(max - min) + min;
    }

    public static int randBetweenGap(Random rand, int median, int gapMin, int gapMax)
    {
        return randBetweenRange(rand, median - gapMin, median + gapMax);
    }

    public static int randBetweenGapRatio(Random rand, int median, float ratio)
    {
        return randBetweenGap(rand, median, (int) Math.ceil(median * (1 - ratio)),
                (int) Math.floor(median * (1 + ratio)));
    }
}
