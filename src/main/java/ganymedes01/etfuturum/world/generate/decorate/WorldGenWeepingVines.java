package ganymedes01.etfuturum.world.generate.decorate;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

import ganymedes01.etfuturum.ModBlocks;

public class WorldGenWeepingVines extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        if (!world.isAirBlock(x, y, z)) {
            return false;
        } else {
            Block block = world.getBlock(x, y + 1, z);
            if (!(block instanceof BlockNetherrack)
                && !(block == ModBlocks.NETHER_WART.get() && world.getBlockMetadata(x, y + 1, z) == 0)) {
                return false;
            } else {
                this.placeRoofNetherWart(world, rand, x, y, z);
                this.placeRoofWeepingVines(world, rand, x, y, z);
                return true;
            }
        }
    }

    private void placeRoofNetherWart(World world, Random rand, int x, int y, int z) {
        world.setBlock(x, y, z, ModBlocks.NETHER_WART.get());

        for (int i = 0; i < 200; ++i) {
            int xOff = x + (rand.nextInt(6) - rand.nextInt(6));
            int yOff = y + (rand.nextInt(2) - rand.nextInt(5));
            int zOff = z + (rand.nextInt(6) - rand.nextInt(6));
            if (world.isAirBlock(xOff, yOff, zOff)) {
                int j = 0;

                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                    Block block = world.getBlock(xOff + dir.offsetX, yOff + dir.offsetY, zOff + dir.offsetZ);
                    if (block instanceof BlockNetherrack
                        || (block == ModBlocks.NETHER_WART.get() && world.getBlockMetadata(x, y + 1, z) == 0)) {
                        ++j;
                    }

                    if (j > 1) {
                        break;
                    }
                }

                if (j == 1) {
                    world.setBlock(xOff, yOff, zOff, ModBlocks.NETHER_WART.get());
                }
            }
        }

    }

    private void placeRoofWeepingVines(World world, Random rand, int x, int y, int z) {
        for (int i = 0; i < 100; ++i) {
            int xOff = x + (rand.nextInt(8) - rand.nextInt(8));
            int yOff = y + (rand.nextInt(2) - rand.nextInt(7));
            int zOff = z + (rand.nextInt(8) - rand.nextInt(8));
            if (world.isAirBlock(xOff, yOff, zOff)) {
                Block block = world.getBlock(xOff, yOff + 1, zOff);
                if (block instanceof BlockNetherrack
                    || (block == ModBlocks.NETHER_WART.get() && world.getBlockMetadata(x, y + 1, z) == 0)) {
                    int j = MathHelper.getRandomIntegerInRange(rand, 1, 8);
                    if (rand.nextInt(6) == 0) {
                        j *= 2;
                    }

                    if (rand.nextInt(5) == 0) {
                        j = 1;
                    }
                    placeWeepingVinesColumn(world, rand, xOff, yOff, zOff, j/* , 17, 25 */);
                }
            }
        }
    }

    public static void placeWeepingVinesColumn(World world, Random rand, int x, int y, int z,
        int size/* , int minAge, int maxAge */) {
        for (int i = 0; i <= size; ++i) {
            if (world.isAirBlock(x, y - i, z)) {
                if (i == size || !world.isAirBlock(x, y - i - 1, z)) {
                    // My weeping vines don't have an age value. However 1 is no-grow weeping vines, so we use that
                    // instead if the age is 25 (max age)
                    // So at the end of natural vine growth we'll just set them all to not grow any more. Not exactly
                    // vanilla but whatever
                    world.setBlock(x, y - i, z, ModBlocks.WEEPING_VINES.get(), 1, 2);
                    break;
                } else {
                    world.setBlock(x, y - i, z, ModBlocks.WEEPING_VINES.get());
                }
            }
        }
    }
}
