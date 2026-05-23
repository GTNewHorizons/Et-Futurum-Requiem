package ganymedes01.etfuturum.client.loading;

import ganymedes01.etfuturum.client.ChunkLoadingProgress;
import ganymedes01.etfuturum.client.SpawnChunkProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class LoadingScreenHooks {

    public static final int CHUNK_COLOR_EMPTY = 0xFF545454;
    public static final int CHUNK_COLOR_STRUCTURE_STARTS = 0xFF999999;
    public static final int CHUNK_COLOR_STRUCTURE_REFERENCES = 0xFF5F6191;
    public static final int CHUNK_COLOR_BIOMES = 0xFF80B252;
    public static final int CHUNK_COLOR_NOISE = 0xFFD1D1D1;
    public static final int CHUNK_COLOR_SURFACE = 0xFF726809;
    public static final int CHUNK_COLOR_CARVERS = 0xFF303572;
    public static final int CHUNK_COLOR_FEATURES = 0xFF21C600;
    public static final int CHUNK_COLOR_INITIALIZE_LIGHT = 0xFFCCCCCC;
    public static final int CHUNK_COLOR_LIGHT = 0xFFFFE0A0;
    public static final int CHUNK_COLOR_SPAWN = 0xFFF26060;
    public static final int CHUNK_COLOR_FULL = 0xFFFFFFFF;

    public static void beginOther() {
        LoadingScreenStateTracker.begin();
    }

    public static void beginDownloadTerrain() {
        LoadingScreenStateTracker.beginIfNeeded();
        LoadingScreenStateTracker.updateTitle(I18n.format("multiplayer.downloadingTerrain"));
        LoadingScreenStateTracker.updateSubtitle("");
    }

    public static void reset() {
        LoadingScreenStateTracker.reset();
    }

    public static void updateServerChunkColor(int chunkX, int chunkZ, int color) {
        int relativeX = chunkX - SpawnChunkProgress.getSpawnChunkX();
        int relativeZ = chunkZ - SpawnChunkProgress.getSpawnChunkZ();

        if (Math.abs(relativeX) > SpawnChunkProgress.SPAWN_CHUNK_RADIUS
                || Math.abs(relativeZ) > SpawnChunkProgress.SPAWN_CHUNK_RADIUS) {
            return;
        }

        LoadingScreenStateTracker.updateChunkColor(relativeX, relativeZ, color);
    }

    public static void updateServerChunkProgress() {
        LoadingScreenStateTracker.updateProgress(SpawnChunkProgress.getProgress());
    }

    public static void updateClientChunkMap(Minecraft mc) {
        if (mc.theWorld == null) {
            LoadingScreenStateTracker.clearChunkMap();
            return;
        }

        int radius = mc.gameSettings.renderDistanceChunks;
        int playerChunkX = ChunkLoadingProgress.getPlayerChunkX();
        int playerChunkZ = ChunkLoadingProgress.getPlayerChunkZ();
        LoadingScreenStateTracker.updateChunkRadius(radius, true);

        for (int relativeZ = -radius; relativeZ <= radius; relativeZ++) {
            for (int relativeX = -radius; relativeX <= radius; relativeX++) {
                boolean loaded = ChunkLoadingProgress.isChunkLoaded(playerChunkX + relativeX, playerChunkZ + relativeZ);
                int color = loaded ? CHUNK_COLOR_FULL : CHUNK_COLOR_EMPTY;
                LoadingScreenStateTracker.updateChunkColor(relativeX, relativeZ, color);
            }
        }
    }
}
