package ganymedes01.etfuturum.client.loading;

import ganymedes01.etfuturum.client.SpawnChunkProgress;

public class LoadingScreenWorldGenTracker {

    public static void beginVanillaSpawn(int spawnChunkX, int spawnChunkZ, int radius) {

    }

    public static void markGenerated(int chunkX, int chunkZ) {
        SpawnChunkProgress.markGenerated(chunkX, chunkZ);
        LoadingScreenHooks.updateServerChunkStage(chunkX, chunkZ, LoadingScreenChunkStage.BIOMES);
        LoadingScreenHooks.updateServerChunkProgress();
    }

    public static void markStage(int chunkX, int chunkZ, LoadingScreenChunkStage stage) {
        LoadingScreenHooks.updateServerChunkStage(chunkX, chunkZ, stage);
    }

    public static void markFull(int chunkX, int chunkZ) {
        SpawnChunkProgress.markPopulated(chunkX, chunkZ);
        LoadingScreenHooks.updateServerChunkStage(chunkX, chunkZ, LoadingScreenChunkStage.FULL);
        LoadingScreenHooks.updateServerChunkProgress();
    }

    public static void finishVanillaSpawn() {

    }
}
