package ganymedes01.etfuturum.client.loading;

public class LoadingScreenChunkSnapshot {

    private final int radius;
    private final int diameter;
    private final int[] colors;
    private final boolean approximate;

    public LoadingScreenChunkSnapshot(int radius, int diameter, int[] colors, boolean approximate) {
        this.radius = radius;
        this.diameter = diameter;
        this.colors = colors.clone();
        this.approximate = approximate;
    }

    public int getRadius() {
        return radius;
    }

    public int getDiameter() {
        return diameter;
    }

    public boolean isApproximate() {
        return approximate;
    }

    public int getColor(int x, int z) {
        return colors[x + z * diameter];
    }
}
