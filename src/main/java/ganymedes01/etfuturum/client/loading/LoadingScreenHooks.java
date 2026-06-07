package ganymedes01.etfuturum.client.loading;

import ganymedes01.etfuturum.client.ChunkLoadingProgress;
import ganymedes01.etfuturum.client.SpawnChunkProgress;
import net.minecraft.client.Minecraft;

public class LoadingScreenHooks {

    public static final int CHUNK_COLOR_EMPTY = LoadingScreenChunkStage.EMPTY.getColor();
    public static final int CHUNK_COLOR_STRUCTURE_STARTS = LoadingScreenChunkStage.STRUCTURE_STARTS.getColor();
    public static final int CHUNK_COLOR_STRUCTURE_REFERENCES = LoadingScreenChunkStage.STRUCTURE_REFERENCES.getColor();
    public static final int CHUNK_COLOR_BIOMES = LoadingScreenChunkStage.BIOMES.getColor();
    public static final int CHUNK_COLOR_NOISE = LoadingScreenChunkStage.NOISE.getColor();
    public static final int CHUNK_COLOR_SURFACE = LoadingScreenChunkStage.SURFACE.getColor();
    public static final int CHUNK_COLOR_CARVERS = LoadingScreenChunkStage.CARVERS.getColor();
    public static final int CHUNK_COLOR_FEATURES = LoadingScreenChunkStage.FEATURES.getColor();
    public static final int CHUNK_COLOR_INITIALIZE_LIGHT = LoadingScreenChunkStage.INITIALIZE_LIGHT.getColor();
    public static final int CHUNK_COLOR_LIGHT = LoadingScreenChunkStage.LIGHT.getColor();
    public static final int CHUNK_COLOR_SPAWN = LoadingScreenChunkStage.SPAWN.getColor();
    public static final int CHUNK_COLOR_FULL = LoadingScreenChunkStage.FULL.getColor();

    public static void beginOther() {
        LoadingScreenStateTracker.beginIfNeeded();
    }

    public static void beginDownloadTerrain(boolean integratedServer) {
        if (!LoadingScreenStateTracker.shouldPreserveProgressForDownloadTerrain(integratedServer)) {
            LoadingScreenStateTracker.begin();
        }
        LoadingScreenStateTracker.onDownloadTerrainOpened(integratedServer);
    }

    public static void reset() {
        LoadingScreenStateTracker.reset();
    }

    public static void updateServerChunkStage(int chunkX, int chunkZ, LoadingScreenChunkStage stage) {
        updateServerChunkColor(chunkX, chunkZ, stage.getColor());
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
