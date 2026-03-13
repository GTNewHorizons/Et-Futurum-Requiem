package ganymedes01.etfuturum.world.end.gen;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenEndIsland extends WorldGenerator {

    @Override
    public boolean generate(World worldIn, Random rand, int x, int y, int z) {
        float f = (float) (rand.nextInt(3) + 4);

        for (int i = 0; f > 0.5F; --i) {
            for (int j = MathHelper.floor_float(-f); j <= MathHelper.ceiling_float_int(f); ++j) {
                for (int k = MathHelper.floor_float(-f); k <= MathHelper.ceiling_float_int(f); ++k) {
                    if ((float) (j * j + k * k) <= (f + 1.0F) * (f + 1.0F)) {
                        worldIn.setBlock(x + j, y + i, z + k, Blocks.end_stone);
                    }
                }
            }

            f = (float) ((double) f - ((double) rand.nextInt(2) + 0.5D));
        }

        return true;
    }
}
