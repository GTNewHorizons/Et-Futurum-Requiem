package ganymedes01.etfuturum.world.generate.decorate;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenPinkPetals extends WorldGenerator {

    private final Block petals;

    public WorldGenPinkPetals(Block p_i45452_1_) {
        super();
        petals = p_i45452_1_;
    }

    @Override
    public boolean generate(World p_76484_1_, Random p_76484_2_, int p_76484_3_, int p_76484_4_, int p_76484_5_) {
        int maxTries = p_76484_2_.nextInt(256) + 256;
        for (int tries = 0; tries <= maxTries; ++tries) {
            int x = p_76484_3_ + p_76484_2_.nextInt(8) - p_76484_2_.nextInt(8);
            int y = p_76484_4_ + p_76484_2_.nextInt(4) - p_76484_2_.nextInt(4);
            int z = p_76484_5_ + p_76484_2_.nextInt(8) - p_76484_2_.nextInt(8);

            if (p_76484_1_.isAirBlock(x, y, z) && (!p_76484_1_.provider.hasNoSky || y < 255)
                && petals.canBlockStay(p_76484_1_, x, y, z)) {
                p_76484_1_.setBlock(x, y, z, petals, p_76484_2_.nextInt(16), 2);
            }
        }

        return true;
    }
}
