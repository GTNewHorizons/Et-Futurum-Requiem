package ganymedes01.etfuturum.world.generate.decorate;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import ganymedes01.etfuturum.ModBlocks;

public class WorldGenTwistingVines extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        if (isInvalidPlacementLocation(world, x, y, z)) {
            return false;
        } else {
            int spreadWidth = 8;
            int spreadHeight = 4;
            int maxHeight = 8;

            for (int l = 0; l < spreadWidth * spreadWidth; ++l) {
                int xOff = x + MathHelper.getRandomIntegerInRange(rand, -spreadWidth, spreadWidth);
                int yOff = y + MathHelper.getRandomIntegerInRange(rand, -spreadHeight, spreadHeight);
                int zOff = z + MathHelper.getRandomIntegerInRange(rand, -spreadWidth, spreadWidth);

                do {
                    yOff--;
                    if (yOff > 0) {
                        break;
                    }
                } while (world.isAirBlock(xOff, yOff + 1, zOff));

                yOff++;

                if (/* findFirstAirBlockAboveGround(world, x, y, z) this was moved to above && */!isInvalidPlacementLocation(
                    world,
                    xOff,
                    yOff,
                    zOff)) {
                    int i1 = MathHelper.getRandomIntegerInRange(rand, 1, maxHeight);
                    if (rand.nextInt(6) == 0) {
                        i1 *= 2;
                    }

                    if (rand.nextInt(5) == 0) {
                        i1 = 1;
                    }

                    placeTwistingVinesColumn(world, rand, xOff, yOff, zOff, i1/* , 17, 25 */);
                }
            }

            return true;
        }
    }

    // This was moved above because it used to use a mutable BlockPos and changed its y value here.
    // We'll just mutate the y value above directly so we don't need BlockPos.
    // private static boolean findFirstAirBlockAboveGround(World world, BlockPos.MutableBlockPos pos) {
    // do {
    // pos.move(0, -1, 0);
    // if (world.isOutsideBuildHeight(pos)) {
    // return false;
    // }
    // } while(world.getBlockState(pos).isAir());
    //
    // pos.move(0, 1, 0);
    // return true;
    // }

    // Originally this was named place WEEPING vines column. Mojang getting lazy with copied code without at least
    // renaming it...
    public static void placeTwistingVinesColumn(World world, Random rand, int x, int y, int z,
        int size/* , int minAge, int maxAge */) {
        for (int i = 0; i <= size; ++i) {
            if (world.isAirBlock(x, y + i, z)) {
                if (i == size || !world.isAirBlock(x, y + i + 1, z)) {
                    // My twisting vines don't have an age value. However 1 is no-grow weeping vines, so we use that
                    // instead if the age is 25 (max age)
                    // So at the end of natural vine growth we'll just set them all to not grow any more. Not exactly
                    // vanilla but whatever
                    world.setBlock(x, y + i, z, ModBlocks.TWISTING_VINES.get(), 1, 2);
                    break;
                } else {
                    world.setBlock(x, y + i, z, ModBlocks.TWISTING_VINES.get());
                }
            }
        }
    }

    private static boolean isInvalidPlacementLocation(World world, int x, int y, int z) {
        if (!world.isAirBlock(x, y, z)) {
            return true;
        } else {
            Block block = world.getBlock(x, y - 1, z);
            int meta = world.getBlockMetadata(x, y - 1, z);
            return !(block instanceof BlockNetherrack || (block == ModBlocks.NYLIUM.get() && meta == 1)
                || (block == ModBlocks.NETHER_WART.get() && meta == 1));
        }
    }
}
