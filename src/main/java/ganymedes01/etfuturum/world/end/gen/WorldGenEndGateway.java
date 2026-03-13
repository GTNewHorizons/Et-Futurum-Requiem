package ganymedes01.etfuturum.world.end.gen;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import ganymedes01.etfuturum.ModBlocks;

public class WorldGenEndGateway extends WorldGenerator {

    @Override
    public boolean generate(World worldIn, Random rand, int x, int y, int z) {

        int xfrom = x - 1;
        int yfrom = y - 2;
        int zfrom = z - 1;
        int xto = x + 1;
        int yto = y + 2;
        int zto = z + 1;
        for (int genx = xfrom; genx <= xto; genx++) {
            for (int geny = yfrom; geny <= yto; geny++) {
                for (int genz = zfrom; genz <= zto; genz++) {
                    boolean flag = genx == x;
                    boolean flag1 = geny == y;
                    boolean flag2 = genz == z;
                    boolean flag3 = Math.abs(geny - y) == 2;

                    if (flag && flag1 && flag2) {
                        worldIn.setBlock(genx, geny, genz, ModBlocks.END_GATEWAY.get());
                    } else if (flag1) {
                        worldIn.setBlock(genx, geny, genz, Blocks.air);
                    } else if (flag3 && flag && flag2) {
                        worldIn.setBlock(genx, geny, genz, Blocks.bedrock);
                    } else if ((flag || flag2) && !flag3) {
                        worldIn.setBlock(genx, geny, genz, Blocks.bedrock);
                    } else {
                        worldIn.setBlock(genx, geny, genz, Blocks.air);
                    }
                }
            }
        }

        return true;
    }

}
