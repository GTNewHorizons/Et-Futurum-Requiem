package ganymedes01.etfuturum.client.loading;

public class LoadingScreenStateTracker {

    private static final LoadingScreenSession SESSION = new LoadingScreenSession();

    private static volatile boolean active;

    public static synchronized void begin() {
        SESSION.reset();
        active = true;
    }

    public static synchronized void beginIfNeeded() {
        if (!active) {
            begin();
        }
    }

    public static synchronized void reset() {
        SESSION.reset();
        active = false;
    }

    public static void updateTitle(String title) {
        if (active) {
            SESSION.setTitle(title);
        }
    }

    public static void updateSubtitle(String subtitle) {
        if (active) {
            SESSION.setSubtitle(subtitle);
        }
    }

    public static void updateProgress(float progress) {
        if (active) {
            SESSION.setProgress(progress);
        }
    }

    public static void updateChunkRadius(int radius, boolean approximate) {
        if (active) {
            SESSION.setChunkRadius(radius, approximate);
        }
    }

    public static void clearChunkMap() {
        if (active) {
            SESSION.clearChunkMap();
        }
    }

    public static void updateChunkColor(int chunkX, int chunkZ, int color) {
        if (active) {
            SESSION.setChunkColor(chunkX, chunkZ, color);
        }
    }

    public static void markDone() {
        if (active) {
            SESSION.setDone(true);
        }
    }

    public static boolean isActive() {
        return active;
    }

    public static LoadingScreenSnapshot snapshot() {
        return SESSION.snapshot();
    }
}
