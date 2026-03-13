package ganymedes01.etfuturum.core.utils.helpers;

import net.minecraft.world.World;

public class WorldHelper {

    public static boolean isAreaLoaded(World world, int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd) {
        if (yEnd >= 0 && yStart < 256) {
            xStart = xStart >> 4;
            zStart = zStart >> 4;
            xEnd = xEnd >> 4;
            zEnd = zEnd >> 4;

            for (int i = xStart; i <= xEnd; ++i) {
                for (int j = zStart; j <= zEnd; ++j) {
                    if (!isChunkLoaded(world, i, j)) {
                        return false;
                    }
                }
            }

            return true;
        }
        return false;
    }

    private static boolean isChunkLoaded(World world, int x, int z) {
        return world.getChunkProvider()
            .chunkExists(x, z);
    }

}
