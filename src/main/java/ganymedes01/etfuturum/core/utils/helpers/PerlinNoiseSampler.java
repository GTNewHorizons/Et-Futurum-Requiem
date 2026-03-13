package ganymedes01.etfuturum.core.utils.helpers;

import java.util.Random;

import net.minecraft.util.MathHelper;

import ganymedes01.etfuturum.core.utils.Utils;

public final class PerlinNoiseSampler {

    private final byte[] permutations;
    public final double originX;
    public final double originY;
    public final double originZ;

    public PerlinNoiseSampler(Random random) {
        this.originX = random.nextDouble() * 256.0D;
        this.originY = random.nextDouble() * 256.0D;
        this.originZ = random.nextDouble() * 256.0D;
        this.permutations = new byte[256];

        int j;
        for (j = 0; j < 256; ++j) {
            this.permutations[j] = (byte) j;
        }

        for (j = 0; j < 256; ++j) {
            int k = random.nextInt(256 - j);
            byte b = this.permutations[j];
            this.permutations[j] = this.permutations[j + k];
            this.permutations[j + k] = b;
        }

    }

    public double sample(double x, double y, double z) {
        return this.sample(x, y, z, 0.0D, 0.0D);
    }

    @Deprecated
    public double sample(double x, double y, double z, double yScale, double yMax) {
        double d = x + this.originX;
        double e = y + this.originY;
        double f = z + this.originZ;
        int i = MathHelper.floor_double(d);
        int j = MathHelper.floor_double(e);
        int k = MathHelper.floor_double(f);
        double g = d - (double) i;
        double h = e - (double) j;
        double l = f - (double) k;
        double p;
        if (yScale != 0.0D) {
            double n;
            if (yMax >= 0.0D && yMax < h) {
                n = yMax;
            } else {
                n = h;
            }

            p = (double) MathHelper.floor_double(n / yScale + 1.0000000116860974E-7D) * yScale;
        } else {
            p = 0.0D;
        }

        return this.sample(i, j, k, g, h - p, l, h);
    }

    public double sampleDerivative(double x, double y, double z, double[] ds) {
        double d = x + this.originX;
        double e = y + this.originY;
        double f = z + this.originZ;
        int i = MathHelper.floor_double(d);
        int j = MathHelper.floor_double(e);
        int k = MathHelper.floor_double(f);
        double g = d - (double) i;
        double h = e - (double) j;
        double l = f - (double) k;
        return this.sampleDerivative(i, j, k, g, h, l, ds);
    }

    private static double grad(int hash, double x, double y, double z) {
        return SimplexNoiseSampler.dot(SimplexNoiseSampler.GRADIENTS[hash & 15], x, y, z);
    }

    private int getGradient(int hash) {
        return this.permutations[hash & 255] & 255;
    }

    private double sample(int sectionX, int sectionY, int sectionZ, double localX, double localY, double localZ,
        double fadeLocalX) {
        int i = this.getGradient(sectionX);
        int j = this.getGradient(sectionX + 1);
        int k = this.getGradient(i + sectionY);
        int l = this.getGradient(i + sectionY + 1);
        int m = this.getGradient(j + sectionY);
        int n = this.getGradient(j + sectionY + 1);
        double d = grad(this.getGradient(k + sectionZ), localX, localY, localZ);
        double e = grad(this.getGradient(m + sectionZ), localX - 1.0D, localY, localZ);
        double f = grad(this.getGradient(l + sectionZ), localX, localY - 1.0D, localZ);
        double g = grad(this.getGradient(n + sectionZ), localX - 1.0D, localY - 1.0D, localZ);
        double h = grad(this.getGradient(k + sectionZ + 1), localX, localY, localZ - 1.0D);
        double o = grad(this.getGradient(m + sectionZ + 1), localX - 1.0D, localY, localZ - 1.0D);
        double p = grad(this.getGradient(l + sectionZ + 1), localX, localY - 1.0D, localZ - 1.0D);
        double q = grad(this.getGradient(n + sectionZ + 1), localX - 1.0D, localY - 1.0D, localZ - 1.0D);
        double r = Utils.perlinFade(localX);
        double s = Utils.perlinFade(fadeLocalX);
        double t = Utils.perlinFade(localZ);
        return Utils.lerp3(r, s, t, d, e, f, g, h, o, p, q);
    }

    private double sampleDerivative(int sectionX, int sectionY, int sectionZ, double localX, double localY,
        double localZ, double[] ds) {
        int i = this.getGradient(sectionX);
        int j = this.getGradient(sectionX + 1);
        int k = this.getGradient(i + sectionY);
        int l = this.getGradient(i + sectionY + 1);
        int m = this.getGradient(j + sectionY);
        int n = this.getGradient(j + sectionY + 1);
        int o = this.getGradient(k + sectionZ);
        int p = this.getGradient(m + sectionZ);
        int q = this.getGradient(l + sectionZ);
        int r = this.getGradient(n + sectionZ);
        int s = this.getGradient(k + sectionZ + 1);
        int t = this.getGradient(m + sectionZ + 1);
        int u = this.getGradient(l + sectionZ + 1);
        int v = this.getGradient(n + sectionZ + 1);
        int[] is = SimplexNoiseSampler.GRADIENTS[o & 15];
        int[] js = SimplexNoiseSampler.GRADIENTS[p & 15];
        int[] ks = SimplexNoiseSampler.GRADIENTS[q & 15];
        int[] ls = SimplexNoiseSampler.GRADIENTS[r & 15];
        int[] ms = SimplexNoiseSampler.GRADIENTS[s & 15];
        int[] ns = SimplexNoiseSampler.GRADIENTS[t & 15];
        int[] os = SimplexNoiseSampler.GRADIENTS[u & 15];
        int[] ps = SimplexNoiseSampler.GRADIENTS[v & 15];
        double d = SimplexNoiseSampler.dot(is, localX, localY, localZ);
        double e = SimplexNoiseSampler.dot(js, localX - 1.0D, localY, localZ);
        double f = SimplexNoiseSampler.dot(ks, localX, localY - 1.0D, localZ);
        double g = SimplexNoiseSampler.dot(ls, localX - 1.0D, localY - 1.0D, localZ);
        double h = SimplexNoiseSampler.dot(ms, localX, localY, localZ - 1.0D);
        double w = SimplexNoiseSampler.dot(ns, localX - 1.0D, localY, localZ - 1.0D);
        double x = SimplexNoiseSampler.dot(os, localX, localY - 1.0D, localZ - 1.0D);
        double y = SimplexNoiseSampler.dot(ps, localX - 1.0D, localY - 1.0D, localZ - 1.0D);
        double z = Utils.perlinFade(localX);
        double aa = Utils.perlinFade(localY);
        double ab = Utils.perlinFade(localZ);
        double ac = Utils.lerp3(z, aa, ab, is[0], js[0], ks[0], ls[0], ms[0], ns[0], os[0], ps[0]);
        double ad = Utils.lerp3(z, aa, ab, is[1], js[1], ks[1], ls[1], ms[1], ns[1], os[1], ps[1]);
        double ae = Utils.lerp3(z, aa, ab, is[2], js[2], ks[2], ls[2], ms[2], ns[2], os[2], ps[2]);
        double af = Utils.lerp2(aa, ab, e - d, g - f, w - h, y - x);
        double ag = Utils.lerp2(ab, z, f - d, x - h, g - e, y - w);
        double ah = Utils.lerp2(z, aa, h - d, w - e, x - f, y - g);
        double ai = Utils.perlinFadeDerivative(localX);
        double aj = Utils.perlinFadeDerivative(localY);
        double ak = Utils.perlinFadeDerivative(localZ);
        double al = ac + ai * af;
        double am = ad + aj * ag;
        double an = ae + ak * ah;
        ds[0] += al;
        ds[1] += am;
        ds[2] += an;
        return Utils.lerp3(z, aa, ab, d, e, f, g, h, w, x, y);
    }
}
