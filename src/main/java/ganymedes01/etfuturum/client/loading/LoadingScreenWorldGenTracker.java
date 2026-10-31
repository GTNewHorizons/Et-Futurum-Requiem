package ganymedes01.etfuturum.client.loading;

import ganymedes01.etfuturum.client.SpawnChunkProgress;
import net.minecraft.world.chunk.Chunk;

public class LoadingScreenWorldGenTracker {

    public static void markTerrain(int chunkX, int chunkZ, Chunk chunk) {
        // Skip populate/loadChunk fires after spawn progress is complete. It can happen even during normal play
        if (!SpawnChunkProgress.isActive()) {
            return;
        }
        SpawnChunkProgress.markPopulated(chunkX, chunkZ);
        LoadingScreenHooks.updateServerChunkColor(chunkX, chunkZ, LoadingScreenChunkColorSampler.sample(chunk));
        LoadingScreenHooks.updateServerChunkProgress();
    }
}
