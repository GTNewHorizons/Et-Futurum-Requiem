package ganymedes01.etfuturum.client.loading;

public class LoadingScreenStateTracker {

    private static final LoadingScreenSession SESSION = new LoadingScreenSession();
    private static final int COMPLETION_RESET_DELAY_TICKS = 20;

    private static volatile boolean active;
    private static volatile boolean completionPending;
    private static volatile boolean downloadTerrainHandoffPending;

    public static synchronized void begin() {
        SESSION.reset();
        LoadingScreenRenderManager.reset();
        active = true;
        completionPending = false;
        downloadTerrainHandoffPending = false;
    }

    public static synchronized void beginIfNeeded() {
        if (!active) {
            begin();
        }
    }

    public static synchronized void reset() {
        SESSION.reset();
        LoadingScreenRenderManager.reset();
        active = false;
        completionPending = false;
        downloadTerrainHandoffPending = false;
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
            completionPending = true;
            downloadTerrainHandoffPending = true;
        }
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean shouldDelayReset(int playerTicksExisted) {
        return completionPending
                && (playerTicksExisted < COMPLETION_RESET_DELAY_TICKS || isDownloadTerrainHandoffPending());
    }

    public static void clearCompletionPending() {
        completionPending = false;
    }

    public static boolean isDownloadTerrainHandoffPending() {
        return downloadTerrainHandoffPending;
    }

    public static boolean shouldPreserveProgressForDownloadTerrain(boolean integratedServer) {
        return integratedServer && isDownloadTerrainHandoffPending();
    }

    public static void onDownloadTerrainOpened(boolean integratedServer) {
        if (integratedServer) {
            downloadTerrainHandoffPending = false;
        }
    }

    public static LoadingScreenSnapshot snapshot() {
        return SESSION.snapshot();
    }
}
