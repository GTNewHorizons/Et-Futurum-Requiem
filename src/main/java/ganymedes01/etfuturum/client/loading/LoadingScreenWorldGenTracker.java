package ganymedes01.etfuturum.client.loading;

import ganymedes01.etfuturum.client.SpawnChunkProgress;
import net.minecraft.world.chunk.Chunk;

public class LoadingScreenWorldGenTracker {

    public static void markTerrain(int chunkX, int chunkZ, Chunk chunk) {
        // populate/loadChunk fire during normal play too, skip unless the loading screen is up
        if (!LoadingScreenStateTracker.isActive()) {
            return;
        }
        SpawnChunkProgress.markPopulated(chunkX, chunkZ);
        LoadingScreenHooks.updateServerChunkColor(chunkX, chunkZ, LoadingScreenChunkColorSampler.sample(chunk));
        LoadingScreenHooks.updateServerChunkProgress();
    }
}
