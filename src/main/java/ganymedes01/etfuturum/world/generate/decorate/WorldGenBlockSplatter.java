package ganymedes01.etfuturum.world.generate.decorate;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import ganymedes01.etfuturum.core.utils.TriangularRandom;

public class WorldGenBlockSplatter extends WorldGenerator {

    private final Block block;
    private final int meta;
    private final Block replaceBlock;
    private final int replaceMeta;

    private final int size;

    public WorldGenBlockSplatter(Block block, int meta, Block replaceBlock, int replaceMeta, int size) {
        super(true);

        this.block = block;
        this.meta = meta;
        this.replaceBlock = replaceBlock;
        this.replaceMeta = replaceMeta;
        this.size = size;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        while (y-- > 0) {
            if (!world.isAirBlock(x, y, z)) {
                break;
            }
        }
        if (y <= 0) return false;

        if (world.getBlock(x, y, z) == replaceBlock && world.getBlockMetadata(x, y, z) == replaceMeta) {
            for (int l = 0; l < size * size * size; ++l) {
                int i1 = x + TriangularRandom.nextInt(random, 0, size);
                int j1 = y + TriangularRandom.nextInt(random, 0, 4);
                int k1 = z + TriangularRandom.nextInt(random, 0, size);
                if (world.isAirBlock(i1, j1 + 1, k1) && world.getBlock(i1, j1, k1) == replaceBlock
                    && world.getBlockMetadata(i1, j1, k1) == replaceMeta) {
                    world.setBlock(i1, j1, k1, block, meta, 2);
                }
            }
        } else {
            return false;
        }

        return true;

    }
}
