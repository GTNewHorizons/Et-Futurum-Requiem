package ganymedes01.etfuturum.client.loading;

public class LoadingScreenChunkSnapshot {

    private final int radius;
    private final int diameter;
    private final int[] colors;

    public LoadingScreenChunkSnapshot(int radius, int diameter, int[] colors) {
        this.radius = radius;
        this.diameter = diameter;
        this.colors = colors.clone();
    }

    public int getRadius() {
        return radius;
    }

    public int getDiameter() {
        return diameter;
    }

    public int getColor(int x, int z) {
        return colors[x + z * diameter];
    }
}
