package ganymedes01.etfuturum.world.generate.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.ImmutableList;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.core.utils.Utils;

public class WorldGenDeltas extends WorldGenerator {

    private final int sizeMin = 7;
    private final int sizeMax = 3;
    private final int rimSizeMin = 0;
    private final int rimSizeMax = 2;
    private static final double RIM_SPAWN_CHANCE = 0.9D;

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        boolean flag = false;
        boolean flag1 = rand.nextDouble() < RIM_SPAWN_CHANCE;
        int rimXSize = flag1 ? MathHelper.getRandomIntegerInRange(rand, sizeMin, sizeMax) : 0;
        int rimZSize = flag1 ? MathHelper.getRandomIntegerInRange(rand, sizeMin, sizeMax) : 0;
        boolean flag2 = flag1 && rimXSize != 0 && rimZSize != 0;
        int xSize = MathHelper.getRandomIntegerInRange(rand, rimSizeMin, rimSizeMax);
        int zSize = MathHelper.getRandomIntegerInRange(rand, rimSizeMin, rimSizeMax);
        int size = Math.max(xSize, zSize);

        for (int x1 = -xSize; x1 <= xSize; x1++) {
            for (int z1 = -zSize; z1 <= zSize; z1++) {
                int xOff = x1 + x;
                int zOff = z1 + z;
                // if (blockpos1.distManhattan(blockpos) > i1) {
                // break;
                // }
                // The below function was originally this but I probably did something wrong, it feels like there's too
                // much magma.
                // However the code in distManhattan seems needlessly convoluted.
                if (Math.abs(xOff - x) + Math.abs(zOff - z) > size + size) {
                    break;
                }
                if (Utils.distManhattan(x, y, z, xOff, y, zOff) > size) {
                    continue;
                }

                if (isClear(world, xOff, y, zOff)) {
                    if (flag2) {
                        flag = true;
                        if (ModBlocks.MAGMA.isEnabled()) {
                            setBlockAndNotifyAdequately(world, xOff, y, zOff, ModBlocks.MAGMA.get(), 0);
                        } else {
                            setBlockAndNotifyAdequately(world, xOff, y, zOff, ModBlocks.BASALT.get(), 0);
                        }
                    }

                    if (isClear(world, xOff + rimXSize, y, zOff + rimZSize)) {
                        flag = true;
                        setBlockAndNotifyAdequately(world, xOff + rimXSize, y, zOff + rimZSize, Blocks.lava, 0);
                    }
                }
            }
        }

        return flag;
    }

    // private static final ImmutableList<Block> CANNOT_REPLACE = ImmutableList.of(
    // Blocks.bedrock, Blocks.nether_brick, Blocks.nether_brick_fence, Blocks.nether_brick_stairs,
    // ModBlocks.NETHER_WART.get(), Blocks.chest, Blocks.mob_spawner);
    private static final ImmutableList<Block> CAN_REPLACE = ImmutableList
        .of(ModBlocks.BLACKSTONE.get(), ModBlocks.BASALT.get());

    private static boolean isClear(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.lava) {
            return false;
        } else if (/* CANNOT_REPLACE.contains(block) */!CAN_REPLACE.contains(block)) {
            return false;
        } else {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                boolean flag = world.isAirBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
                if (flag && dir != ForgeDirection.UP || !flag && dir == ForgeDirection.UP) {
                    return false;
                }
            }

            return true;
        }
    }
}
