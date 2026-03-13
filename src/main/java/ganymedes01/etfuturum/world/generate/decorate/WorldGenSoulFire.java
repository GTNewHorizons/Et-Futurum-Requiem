package ganymedes01.etfuturum.world.generate.decorate;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import ganymedes01.etfuturum.ModBlocks;

public class WorldGenSoulFire extends WorldGenerator {

    public WorldGenSoulFire() {}

    @Override
    public boolean generate(World p_76484_1_, Random p_76484_2_, int p_76484_3_, int p_76484_4_, int p_76484_5_) {
        for (int l = 0; l < 64; ++l) {
            int i1 = p_76484_3_ + p_76484_2_.nextInt(8) - p_76484_2_.nextInt(8);
            int j1 = p_76484_4_ + p_76484_2_.nextInt(4) - p_76484_2_.nextInt(4);
            int k1 = p_76484_5_ + p_76484_2_.nextInt(8) - p_76484_2_.nextInt(8);
            if (p_76484_1_.isAirBlock(i1, j1, k1) && p_76484_1_.getBlock(i1, j1 - 1, k1)
                == (ModBlocks.SOUL_SOIL.isEnabled() ? ModBlocks.SOUL_SOIL.get() : Blocks.soul_sand)) {
                p_76484_1_.setBlock(i1, j1, k1, Blocks.fire, 0, 2);
            }
        }

        return true;
    }
}
