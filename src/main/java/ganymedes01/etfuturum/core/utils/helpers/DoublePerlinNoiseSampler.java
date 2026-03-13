package ganymedes01.etfuturum.core.utils.helpers;

import java.util.Random;

import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

public class DoublePerlinNoiseSampler {

    private final double amplitude;
    private final OctavePerlinNoiseSampler firstSampler;
    private final OctavePerlinNoiseSampler secondSampler;

    public static DoublePerlinNoiseSampler create(Random random, int offset, double... octaves) {
        return new DoublePerlinNoiseSampler(random, offset, new TDoubleArrayList(octaves));
    }

    public static DoublePerlinNoiseSampler create(Random random, int offset, TDoubleList octaves) {
        return new DoublePerlinNoiseSampler(random, offset, octaves);
    }

    private DoublePerlinNoiseSampler(Random random, int offset, TDoubleList octaves) {
        this.firstSampler = OctavePerlinNoiseSampler.create(random, offset, octaves);
        this.secondSampler = OctavePerlinNoiseSampler.create(random, offset, octaves);
        int i = Integer.MAX_VALUE;
        int j = Integer.MIN_VALUE;
        TDoubleIterator doubleListIterator = octaves.iterator();

        while (doubleListIterator.hasNext()) {
            double d = doubleListIterator.next();
            int k = octaves.indexOf(d);
            if (d != 0.0D) {
                i = Math.min(i, k);
                j = Math.max(j, k);
            }
        }

        this.amplitude = 0.16666666666666666D / createAmplitude(j - i);
    }

    private static double createAmplitude(int octaves) {
        return 0.1D * (1.0D + 1.0D / (double) (octaves + 1));
    }

    public double sample(double x, double y, double z) {
        double d = x * 1.0181268882175227D;
        double e = y * 1.0181268882175227D;
        double f = z * 1.0181268882175227D;
        return (this.firstSampler.sample(x, y, z) + this.secondSampler.sample(d, e, f)) * this.amplitude;
    }
}
