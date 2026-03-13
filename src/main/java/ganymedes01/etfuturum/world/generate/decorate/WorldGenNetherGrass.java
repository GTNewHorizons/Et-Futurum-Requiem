package ganymedes01.etfuturum.world.generate.decorate;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import ganymedes01.etfuturum.ModBlocks;

public class WorldGenNetherGrass extends WorldGenerator {

    private final boolean crimson;

    public WorldGenNetherGrass(boolean crimson) {
        this.crimson = crimson;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        do {
            if (!world.isAirBlock(x, y, z)) {
                break;
            }
            --y;
        } while (y > 0);

        if (y <= 0) return false;

        for (int l = 0; l < 128; ++l) {
            Block grass;
            int meta;
            if (crimson) {
                meta = 0;
                if (rand.nextInt(32) == 0) {
                    grass = ModBlocks.NETHER_FUNGUS.get();
                    if (rand.nextInt(16) == 0) {
                        meta = 1;
                    }
                } else {
                    grass = ModBlocks.NETHER_ROOTS.get();
                }
            } else {
                meta = 1;
                if (rand.nextInt(32) == 0) {
                    grass = ModBlocks.NETHER_FUNGUS.get();
                    if (rand.nextInt(16) == 0) {
                        meta = 0;
                    }
                } else {
                    if (rand.nextInt(3) == 0) {
                        grass = ModBlocks.NETHER_SPROUTS.get();
                        meta = 0;
                    } else {
                        grass = ModBlocks.NETHER_ROOTS.get();
                        if (rand.nextInt(32) == 0) {
                            meta = 0;
                        }
                    }
                }
            }
            int i1 = x + rand.nextInt(8) - rand.nextInt(8);
            int j1 = y + rand.nextInt(4) - rand.nextInt(4);
            int k1 = z + rand.nextInt(8) - rand.nextInt(8);

            if (world.isAirBlock(i1, j1, k1) && grass.canBlockStay(world, i1, j1, k1)) {
                world.setBlock(i1, j1, k1, grass, meta, 2);
            }
        }

        return true;
    }
}
