package ganymedes01.etfuturum.core.utils.helpers;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.LongFunction;

import org.apache.commons.lang3.tuple.Pair;

import ganymedes01.etfuturum.core.utils.Utils;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

public class OctavePerlinNoiseSampler implements NoiseSampler {

    private final PerlinNoiseSampler[] octaveSamplers;
    private final TDoubleList amplitudes;
    private final double persistence;
    private final double lacunarity;

    // public OctavePerlinNoiseSampler(Random random, IntStream octaves) {
    // this(random, (List)octaves.boxed().collect(ImmutableList.toImmutableList())); ????
    // }

    public OctavePerlinNoiseSampler(Random random, List<Integer> octaves) {
        this(random, new TreeSet<Integer>(octaves));
    }

    public static OctavePerlinNoiseSampler create(Random random, int offset, double... amplitudes) {
        return create(random, offset, new TDoubleArrayList(amplitudes));
    }

    public static OctavePerlinNoiseSampler create(Random random, int offset, TDoubleList amplitudes) {
        return new OctavePerlinNoiseSampler(random, Pair.of(offset, amplitudes));
    }

    private static Pair<Integer, TDoubleList> calculateAmplitudes(SortedSet<Integer> octaves) {
        if (octaves.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        }
        int i = -octaves.first();
        int j = octaves.last();
        int k = i + j + 1;
        if (k < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
        }
        TDoubleList doubleList = new TDoubleArrayList(new double[k]);
        Iterator<Integer> intBidirectionalIterator = octaves.iterator();

        while (intBidirectionalIterator.hasNext()) {
            int l = intBidirectionalIterator.next();
            doubleList.set(l + i, 1.0D);
        }

        return Pair.of(-i, doubleList);
    }

    private OctavePerlinNoiseSampler(Random random, SortedSet<Integer> octaves) {
        this(random, octaves, Random::new);
    }

    private OctavePerlinNoiseSampler(Random random, SortedSet<Integer> octaves, LongFunction<Random> randomFunction) {
        this(random, calculateAmplitudes(octaves), randomFunction);
    }

    protected OctavePerlinNoiseSampler(Random random, Pair<Integer, TDoubleList> offsetAndAmplitudes) {
        this(random, offsetAndAmplitudes, Random::new);
    }

    protected OctavePerlinNoiseSampler(Random random, Pair<Integer, TDoubleList> octaves,
        LongFunction<Random> randomFunction) {
        int i = octaves.getLeft();
        this.amplitudes = octaves.getRight();
        PerlinNoiseSampler perlinNoiseSampler = new PerlinNoiseSampler(random);
        int j = this.amplitudes.size();
        int k = -i;
        this.octaveSamplers = new PerlinNoiseSampler[j];
        if (k >= 0 && k < j) {
            double d = this.amplitudes.get(k);
            if (d != 0.0D) {
                this.octaveSamplers[k] = perlinNoiseSampler;
            }
        }

        for (int l = k - 1; l >= 0; --l) {
            if (l < j) {
                double e = this.amplitudes.get(l);
                if (e != 0.0D) {
                    this.octaveSamplers[l] = new PerlinNoiseSampler(random);
                } else {
                    skipCalls(random);
                }
            } else {
                skipCalls(random);
            }
        }

        if (k < j - 1) {
            throw new IllegalArgumentException("Positive octaves are temporarily disabled");
        }
        this.lacunarity = Math.pow(2.0D, -k);
        this.persistence = Math.pow(2.0D, j - 1) / (Math.pow(2.0D, j) - 1.0D);
    }

    private static void skipCalls(Random random) {
        for (int i = 0; i < 262; ++i) {
            random.nextInt();
        }
    }

    public double sample(double x, double y, double z) {
        return this.sample(x, y, z, 0.0D, 0.0D, false);
    }

    @Deprecated
    public double sample(double x, double y, double z, double yScale, double yMax, boolean useOrigin) {
        double d = 0.0D;
        double e = this.lacunarity;
        double f = this.persistence;

        for (int i = 0; i < this.octaveSamplers.length; ++i) {
            PerlinNoiseSampler perlinNoiseSampler = this.octaveSamplers[i];
            if (perlinNoiseSampler != null) {
                double g = perlinNoiseSampler.sample(
                    maintainPrecision(x * e),
                    useOrigin ? -perlinNoiseSampler.originY : maintainPrecision(y * e),
                    maintainPrecision(z * e),
                    yScale * e,
                    yMax * e);
                d += this.amplitudes.get(i) * g * f;
            }

            e *= 2.0D;
            f /= 2.0D;
        }

        return d;
    }

    public static double maintainPrecision(double value) {
        return value - (double) Utils.lfloor(value / 3.3554432E7D + 0.5D) * 3.3554432E7D;
    }

    public double sample(double x, double y, double yScale, double yMax) {
        return this.sample(x, y, 0.0D, yScale, yMax, false);
    }
}
