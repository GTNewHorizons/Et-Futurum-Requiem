package ganymedes01.etfuturum.client.loading;

import ganymedes01.etfuturum.client.ChunkLoadingProgress;
import ganymedes01.etfuturum.client.SpawnChunkProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;

public class LoadingScreenHooks {

    private static final int CHUNK_COLOR_EMPTY = 0xFF545454;

    // sampling isn't free and the client map rebuilds every frame, so cache per chunk coord
    // (cleared on reset/new world so colors don't carry over)
    private static final Map<Long, Integer> CLIENT_CHUNK_COLOR_CACHE = new HashMap<>();

    public static void beginIntegratedLaunch() {
        // activate the new session; resetForNewLaunch already cleared the old world at launch HEAD
        CLIENT_CHUNK_COLOR_CACHE.clear();
        LoadingScreenStateTracker.begin();
    }

    public static void beginDownloadTerrain(boolean integratedServer) {
        if (!LoadingScreenStateTracker.shouldPreserveProgressForDownloadTerrain(integratedServer)) {
            LoadingScreenStateTracker.begin();
        }
        LoadingScreenStateTracker.onDownloadTerrainOpened(integratedServer);
    }

    public static void reset() {
        CLIENT_CHUNK_COLOR_CACHE.clear();
        LoadingScreenStateTracker.reset();
    }

    public static void resetForNewLaunch() {
        // Clear the previous world's chunk map and progress before a new world loads; the
        // in-world reset is skipped when you quit via the menu.
        CLIENT_CHUNK_COLOR_CACHE.clear();
        LoadingScreenStateTracker.reset();
        SpawnChunkProgress.reset();
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
            CLIENT_CHUNK_COLOR_CACHE.clear();
            LoadingScreenStateTracker.clearChunkMap();
            return;
        }

        int radius = mc.gameSettings.renderDistanceChunks;
        int playerChunkX = ChunkLoadingProgress.getPlayerChunkX();
        int playerChunkZ = ChunkLoadingProgress.getPlayerChunkZ();
        LoadingScreenStateTracker.updateChunkRadius(radius);

        for (int relativeZ = -radius; relativeZ <= radius; relativeZ++) {
            for (int relativeX = -radius; relativeX <= radius; relativeX++) {
                int chunkX = playerChunkX + relativeX;
                int chunkZ = playerChunkZ + relativeZ;
                int color;
                if (ChunkLoadingProgress.isChunkLoaded(chunkX, chunkZ)) {
                    long key = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);
                    Integer cached = CLIENT_CHUNK_COLOR_CACHE.get(key);
                    if (cached != null) {
                        color = cached;
                    } else {
                        Chunk chunk = mc.theWorld.getChunkFromChunkCoords(chunkX, chunkZ);
                        color = LoadingScreenChunkColorSampler.sample(chunk);
                        CLIENT_CHUNK_COLOR_CACHE.put(key, color);
                    }
                } else {
                    color = CHUNK_COLOR_EMPTY;
                }
                LoadingScreenStateTracker.updateChunkColor(relativeX, relativeZ, color);
            }
        }
    }
}
