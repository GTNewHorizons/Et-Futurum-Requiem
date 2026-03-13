package ganymedes01.etfuturum.core.utils;

import java.util.Random;

public class TriangularRandom {

    public static int nextInt(Random rand, int triangle_peak, int triangle_wingsize, int lower_trunc, int upper_trunc) {
        float needed_for_inclusive_integer = 0.5F;

        // Perform acceptance-rejection here to do the truncation
        float sample;
        do {
            // Generate two random numbers. This is the basis of a triangular distribution.
            float rand1 = rand.nextFloat();
            float rand2 = rand.nextFloat();
            sample = (rand1 - rand2) * (triangle_wingsize + needed_for_inclusive_integer) + triangle_peak;
        } while (sample < lower_trunc - needed_for_inclusive_integer
            || sample > upper_trunc + needed_for_inclusive_integer);

        // return sample
        return Math.round(sample);
    }

    public static int nextInt(Random rand, int triangle_peak, int triangle_wingsize) {
        return nextInt(rand, triangle_peak, triangle_wingsize, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
}
