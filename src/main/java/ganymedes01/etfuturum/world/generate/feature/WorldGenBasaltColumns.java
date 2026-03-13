package ganymedes01.etfuturum.world.generate.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.google.common.collect.ImmutableList;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.core.utils.Utils;

public class WorldGenBasaltColumns extends WorldGenerator {

    private final int minHeight;
    private final int maxHeight;
    private final int minReach;
    private final int maxReach;

    public WorldGenBasaltColumns(int minHeight, int maxHeight, int minReach, int maxReach) { // TODO: small columns are
                                                                                             // 1-4, big are 5-10
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.minReach = minReach;
        this.maxReach = maxReach;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        // int i = p_159444_.chunkGenerator().getSeaLevel();
        int i = 32;
        if (!canPlaceAt(world, i, x, y, z)) {
            return false;
        } else {
            int j = MathHelper.getRandomIntegerInRange(rand, minHeight, maxHeight);
            boolean flag = rand.nextFloat() < 0.9F;
            int size = Math.min(j, flag ? 5 : 8);
            int l = flag ? 50 : 15;
            boolean flag1 = false;

            for (int k = 0; k < l; k++) {
                int randomX = x + MathHelper.getRandomIntegerInRange(rand, -size, size);
                int randomZ = z + MathHelper.getRandomIntegerInRange(rand, -size, size);
                int i1 = j - Utils.distManhattan(randomX, y, randomZ, x, y, z);
                if (i1 >= 0) {
                    flag1 |= this.placeColumn(
                        world,
                        i,
                        randomX,
                        y,
                        randomZ,
                        i1,
                        MathHelper.getRandomIntegerInRange(rand, minReach, maxReach));
                }
            }

            return flag1;
        }
    }

    private boolean placeColumn(World world, int seaLevel, int x, int y, int z, int p_65171_, int size) {
        boolean flag = false;

        for (int x1 = -size; x1 <= size; x1++) {
            for (int z1 = -size; z1 <= size; z1++) {
                int offX = x1 + x;
                int offY;
                int offZ = z1 + z;
                int i = Utils.distManhattan(x, y, z, offX, y, offZ);
                offY = isAirOrLavaOcean(world, seaLevel, offX, y, offZ)
                    ? findSurfaceY(world, seaLevel, offX, y, offZ, i)
                    : findAirY(world, offX, y, offZ, i);
                if (offY >= 0) {
                    int j = p_65171_ - i / 2;

                    for (; j >= 0; --j) {
                        if (isAirOrLavaOcean(world, seaLevel, offX, offY, offZ)) {
                            setBlockAndNotifyAdequately(world, offX, offY, offZ, ModBlocks.BASALT.get(), 0);
                            offY++;
                            flag = true;
                        } else {
                            if (world.getBlock(offX, offY, offZ) != ModBlocks.BASALT.get()) {
                                break;
                            }

                            offY++;
                        }
                    }
                }
            }
        }

        return flag;
    }

    private static int findSurfaceY(World world, int seaLevel, int x, int y, int z, int p_65162_) {
        while (y > 1 && p_65162_ > 0) {
            --p_65162_;
            if (canPlaceAt(world, seaLevel, x, y, z)) {
                return y;
            }

            y--;
        }

        return 0;
    }

    // private static final ImmutableList<Block> CANNOT_PLACE_ON = ImmutableList.of(
    // Blocks.lava, Blocks.flowing_lava, Blocks.bedrock, ModBlocks.MAGMA.get(), Blocks.soul_sand, Blocks.nether_brick,
    // Blocks.nether_brick_fence,
    // Blocks.nether_brick_stairs, ModBlocks.NETHER_WART.get(), Blocks.chest, Blocks.mob_spawner);
    private static final ImmutableList<Block> CAN_PLACE_ON = ImmutableList
        .of(ModBlocks.BLACKSTONE.get(), ModBlocks.BASALT.get());

    private static boolean canPlaceAt(World world, int seaLevel, int x, int y, int z) {
        if (!isAirOrLavaOcean(world, seaLevel, x, y, z)) {
            return false;
        } else {
            Block block = world.getBlock(x, y - 1, z);
            return !block.isAir(world, x, y, z)
                /* && !CANNOT_PLACE_ON.contains(block) */ && CAN_PLACE_ON.contains(block);
        }
    }

    private static int findAirY(World world, int x, int y, int z, int p_65176_) {
        while (y < world.getActualHeight() && p_65176_ > 0) {
            --p_65176_;
            Block block = world.getBlock(x, y, z);
            if (/* CANNOT_PLACE_ON.contains(block) */!CAN_PLACE_ON.contains(block)) {
                return 0;
            }

            if (block.isAir(world, x, y, z)) {
                return y;
            }

            y++;
        }

        return 0;
    }

    private static boolean isAirOrLavaOcean(World world, int seaLevel, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block.isAir(world, x, y, z) || block.getMaterial() == Material.lava && y <= seaLevel;
    }
}
