package ganymedes01.etfuturum.client.loading;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class LoadingScreenSession {

    private static final int EMPTY_CHUNK_COLOR = 0xFF545454;
    private final ConcurrentHashMap<Long, Integer> chunkColors = new ConcurrentHashMap<Long, Integer>();
    private volatile String title = "";
    private volatile String subtitle = "";
    private volatile float progress;
    private volatile boolean done;
    private volatile boolean chunkMapVisible;
    private volatile int chunkRadius;
    private volatile boolean approximate;

    public void reset() {
        title = "";
        subtitle = "";
        progress = 0.0F;
        done = false;
        chunkMapVisible = false;
        chunkRadius = 0;
        approximate = false;
        chunkColors.clear();
    }

    public void setTitle(String newTitle) {
        title = newTitle == null ? "" : newTitle;
    }

    public void setSubtitle(String newSubtitle) {
        subtitle = newSubtitle == null ? "" : newSubtitle;
    }

    public void setProgress(float newProgress) {
        progress = Math.max(progress, Math.max(0.0F, Math.min(1.0F, newProgress)));
    }

    public void setDone(boolean newDone) {
        done = newDone;
        if (newDone) {
            progress = 1.0F;
        }
    }

    public void setChunkRadius(int newRadius, boolean isApproximate) {
        chunkMapVisible = true;
        chunkRadius = Math.max(0, newRadius);
        approximate = isApproximate;
    }

    public void clearChunkMap() {
        chunkMapVisible = false;
        chunkRadius = 0;
        approximate = false;
        chunkColors.clear();
    }

    public void setChunkColor(int chunkX, int chunkZ, int color) {
        chunkColors.put(key(chunkX, chunkZ), Integer.valueOf(color));
    }

    public LoadingScreenSnapshot snapshot() {
        LoadingScreenChunkSnapshot snapshot = null;

        if (chunkMapVisible) {
            int diameter = chunkRadius * 2 + 1;
            int[] colors = new int[diameter * diameter];
            Arrays.fill(colors, EMPTY_CHUNK_COLOR);

            for (int z = -chunkRadius; z <= chunkRadius; z++) {
                for (int x = -chunkRadius; x <= chunkRadius; x++) {
                    Integer color = chunkColors.get(key(x, z));
                    if (color != null) {
                        int index = x + chunkRadius + (z + chunkRadius) * diameter;
                        colors[index] = color.intValue();
                    }
                }
            }

            snapshot = new LoadingScreenChunkSnapshot(chunkRadius, diameter, colors, approximate);
        }

        return new LoadingScreenSnapshot(title, subtitle, progress, done, snapshot);
    }

    private long key(int chunkX, int chunkZ) {
        return (long) chunkX & 4294967295L | ((long) chunkZ & 4294967295L) << 32;
    }
}
