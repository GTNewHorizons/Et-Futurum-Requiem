package ganymedes01.etfuturum.client.loading;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.world.chunk.Chunk;

/**
 * Builds a single representative top-down map color for a chunk by sampling the highest
 * non-air block in a grid of columns and averaging their {@link MapColor} values. Works for
 * any generator because it reads the finished blocks, not the generation pipeline.
 */
public class LoadingScreenChunkColorSampler {

    private static final int STRIDE = 4;
    private static final int OFFSET = 2;
    private static final int FALLBACK_COLOR = 0xFF000000;

    public static int sample(Chunk chunk) {
        if (chunk == null) {
            return FALLBACK_COLOR;
        }

        long r = 0;
        long g = 0;
        long b = 0;
        int count = 0;

        for (int x = OFFSET; x < 16; x += STRIDE) {
            for (int z = OFFSET; z < 16; z += STRIDE) {
                int rgb = sampleColumn(chunk, x, z);
                if (rgb < 0) {
                    continue;
                }
                r += (rgb >> 16) & 0xFF;
                g += (rgb >> 8) & 0xFF;
                b += rgb & 0xFF;
                count++;
            }
        }

        if (count == 0) {
            return FALLBACK_COLOR;
        }

        int red = (int) (r / count);
        int green = (int) (g / count);
        int blue = (int) (b / count);
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }

    private static int sampleColumn(Chunk chunk, int x, int z) {
        int y = Math.min(chunk.getHeightValue(x, z), 255);
        for (; y >= 0; y--) {
            Block block = chunk.getBlock(x, y, z);
            if (block.getMaterial() == Material.air) {
                continue;
            }
            MapColor mapColor = block.getMapColor(chunk.getBlockMetadata(x, y, z));
            if (mapColor == null || mapColor == MapColor.airColor) {
                continue;
            }
            return mapColor.colorValue;
        }
        return -1;
    }
}
