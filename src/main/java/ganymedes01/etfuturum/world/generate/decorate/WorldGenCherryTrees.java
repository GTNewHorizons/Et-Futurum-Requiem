package ganymedes01.etfuturum.world.generate.decorate;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.util.ForgeDirection;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.core.utils.Utils;

public class WorldGenCherryTrees extends WorldGenAbstractTree {

    private final int branchCountMin;
    private final int branchCountMax;
    private final int branchHorizontalLengthMin;
    private final int branchHorizontalLengthMax;
    // private final int secondBranchStartOffsetFromTop;
    private final int branchStartOffsetFromTopMin;
    private final int branchStartOffsetFromTopMax;
    private final int branchEndOffsetFromTopMin;
    private final int branchEndOffsetFromTopMax;
    private final boolean doNotify;

    public WorldGenCherryTrees(boolean p_i2008_1_) {
        super(p_i2008_1_);
        this.branchCountMin = 1;
        this.branchCountMax = 3;
        this.branchHorizontalLengthMin = 2;
        this.branchHorizontalLengthMax = 4;
        this.branchStartOffsetFromTopMin = -4;
        this.branchStartOffsetFromTopMax = -3;
        // this.secondBranchStartOffsetFromTop = p_272917_;
        this.branchEndOffsetFromTopMin = -1;
        this.branchEndOffsetFromTopMax = 0;
        doNotify = p_i2008_1_;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        if (checkSpace(world, rand, x, y, z)) {
            int height = 7;

            world.setBlock(x, y - 1, z, Blocks.dirt);

            int firstBranchOffsetFromTop = Math.max(
                0,
                height - 1
                    + MathHelper
                        .getRandomIntegerInRange(rand, branchStartOffsetFromTopMin, branchStartOffsetFromTopMax)); // second
                                                                                                                   // branch
            int secondBranchOffsetFromTop = Math.max(
                0,
                height - 1
                    + MathHelper
                        .getRandomIntegerInRange(rand, branchStartOffsetFromTopMin, branchStartOffsetFromTopMax));
            if (secondBranchOffsetFromTop >= firstBranchOffsetFromTop) {
                ++secondBranchOffsetFromTop;
            }

            int branchCount = MathHelper.getRandomIntegerInRange(rand, branchCountMin, branchCountMax);
            boolean flag = branchCount == 3;
            boolean flag1 = branchCount >= 2;
            int treeHeight;
            if (flag) {
                treeHeight = height;
            } else if (flag1) {
                treeHeight = Math.max(firstBranchOffsetFromTop, secondBranchOffsetFromTop) + 1;
            } else {
                treeHeight = firstBranchOffsetFromTop + 1;
            }

            for (int i1 = 0; i1 < treeHeight; ++i1) {
                world.setBlock(x, y + i1, z, ModBlocks.CHERRY_LOG.get());
            }

            if (flag) {
                generateLeaves(world, rand, x, y + treeHeight, z);
            }

            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[rand.nextInt(4) + 2];
            this.generateBranch(
                world,
                x,
                y,
                z,
                rand,
                height,
                direction,
                firstBranchOffsetFromTop,
                firstBranchOffsetFromTop < treeHeight - 1);
            if (flag1) {
                this.generateBranch(
                    world,
                    x,
                    y,
                    z,
                    rand,
                    height,
                    direction.getOpposite(),
                    secondBranchOffsetFromTop,
                    secondBranchOffsetFromTop < treeHeight - 1);
            }

            return true;
        }
        return false;
    }

    private void generateBranch(World world, int x, int y, int z, Random rand, int height, ForgeDirection dir,
        int branchStartOffset, boolean lower) {
        int mutableX = x;
        int mutableY = y + branchStartOffset;
        int mutableZ = z;

        int i = height - 1
            + MathHelper.getRandomIntegerInRange(rand, branchEndOffsetFromTopMin, branchEndOffsetFromTopMax);
        boolean flag = lower || i < branchStartOffset;

        int j = MathHelper.getRandomIntegerInRange(rand, branchHorizontalLengthMin, branchHorizontalLengthMax)
            + (flag ? 1 : 0);

        int k = flag ? 2 : 1;
        int branchEndX = mutableX + (dir.offsetX * j);
        int branchEndY = mutableY + i;
        int branchEndZ = mutableZ + (dir.offsetZ * j);
        for (int l = 0; l < k; ++l) {
            mutableX += dir.offsetX;
            mutableZ += dir.offsetZ;
            setBlockAndNotifyAdequately(
                world,
                mutableX,
                mutableY,
                mutableZ,
                ModBlocks.CHERRY_LOG.get(),
                dir.offsetX != 0 ? 4 : dir.offsetZ != 0 ? 8 : 0);
        }

        ForgeDirection branchDir = branchEndY > mutableY ? ForgeDirection.UP : ForgeDirection.DOWN;

        while (true) {
            int i1 = Utils.distManhattan(branchEndX, branchEndY, branchEndZ, mutableX, mutableY, mutableZ);
            if (i1 == 0) {
                generateLeaves(world, rand, branchEndX, branchEndY + 1, branchEndZ);
                return;
            }

            float f = (float) Math.abs(branchEndY - mutableY) / (float) i1;
            boolean flag1 = rand.nextFloat() < f;
            ForgeDirection logDir = flag1 ? branchDir : dir;
            mutableX += logDir.offsetX;
            mutableY += logDir.offsetY;
            mutableZ += logDir.offsetZ;
            setBlockAndNotifyAdequately(
                world,
                mutableX,
                mutableY,
                mutableZ,
                ModBlocks.CHERRY_LOG.get(),
                logDir.offsetX != 0 ? 4 : logDir.offsetZ != 0 ? 8 : 0);
        }
    }

    private static final float CORNER_HOLE_CHANCE = 0.25F;
    private static final float HANGING_LEAVES_CHANCE = 0.16666667F;
    private static final float HANGING_LEAVES_EXTENSION_CHANCE = 0.33333334F;
    private static final int FOLIAGE_HEIGHT = 5;
    private static final int FOLIAGE_RADIUS = 4;
    private static final float WIDE_BOTTOM_LAYER_HOLE_CHANCE = 0.25F;

    protected void generateLeaves(World world, Random random, int x, int y, int z) {
        // BlockPos blockPos = treeNode.getCenter().up(offset);

        int i = FOLIAGE_RADIUS - 1;

        generateSquare(world, random, x, y, z, i - 2, FOLIAGE_HEIGHT - 3);
        generateSquare(world, random, x, y, z, i - 1, FOLIAGE_HEIGHT - 4);

        for (int j = FOLIAGE_HEIGHT - 5; j == 0; j--) {
            generateSquare(world, random, x, y, z, i, j);
        }

        generateSquareWithHangingLeaves(world, random, x, y, z, i, -1);
        generateSquareWithHangingLeaves(world, random, x, y, z, i - 1, -2);
    }

    // public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
    // return this.height.get(random);
    // }

    protected void generateSquare(World world, Random random, int x, int y, int z, int radius, int height) {
        for (int j = -radius; j <= radius; j++) {
            for (int k = -radius; k <= radius; k++) {
                if (!isPositionInvalid(random, j, height, k, radius)) {
                    placeLeaves(world, x + j, y + height, z + k);
                }
            }
        }
    }

    private void generateSquareWithHangingLeaves(World world, Random random, int x, int y, int z, int radius,
        int height) {
        generateSquare(world, random, x, y, z, radius, height);
        int my = y - radius - 1;
        for (int mx = x - radius; mx <= x + radius; mx++) {
            for (int mz = z - radius; mz <= z + radius; mz++) {
                if (random.nextFloat() < HANGING_LEAVES_CHANCE) {
                    int offset;
                    for (offset = 0; offset <= 3; offset++) {
                        if (offset == 3) {
                            return;
                        }
                        if (world.getBlock(mx, my + offset, mz)
                            .isReplaceable(world, mx, my + offset, mz)
                            && world.getBlock(mx, my + offset + 1, mz) == ModBlocks.LEAVES.get()
                            && world.getBlockMetadata(mx, my + offset + 1, mz) == 1) {
                            break;
                        }
                    }
                    placeLeaves(world, mx, my + offset, mz);
                    if (random.nextFloat() < HANGING_LEAVES_EXTENSION_CHANCE) {
                        placeLeaves(world, mx, my + offset - 1, mz);
                    }
                }
            }
        }
    }

    protected boolean isPositionInvalid(Random random, int dx, int y, int dz, int radius) {
        return isInvalidForLeaves(random, Math.abs(dx), y, Math.abs(dz), radius);
    }

    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius) {
        if (y == -1 && (dx == radius || dz == radius) && random.nextFloat() < WIDE_BOTTOM_LAYER_HOLE_CHANCE) {
            return true;
        }

        boolean bl = (dx == radius && dz == radius);
        boolean bl2 = (radius > 2);

        if (bl2) {
            return (bl || (dx + dz > radius * 2 - 2 && random.nextFloat() < CORNER_HOLE_CHANCE));
        }
        return (bl && random.nextFloat() < CORNER_HOLE_CHANCE);
    }

    protected boolean checkSpace(World world, Random random, int x, int y, int z) {
        if (doNotify) { // If this is false, this is a naturally generated tree, which doesn't need this check.
            for (int my = y + 1; my <= y + 6; my++) {
                for (int mx = x - 1; mx <= x + 1; mx++) {
                    for (int mz = z - 1; mz <= z + 1; mz++) {
                        if (!world.getBlock(mx, my, mz)
                            .isReplaceable(world, mx, my, mz)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void placeLeaves(World world, int x, int y, int z) {
        if (!(world.getBlock(x, y, z) instanceof BlockLog)) {
            setBlockAndNotifyAdequately(world, x, y, z, ModBlocks.LEAVES.get(), 1);
        }
    }

    @Override
    protected void setBlockAndNotifyAdequately(World world, int x, int y, int z, Block p_150516_5_, int p_150516_6_) {
        if (world.getBlock(x, y, z)
            .isReplaceable(world, x, y, z) || isReplaceable(world, x, y, z)) {
            super.setBlockAndNotifyAdequately(world, x, y, z, p_150516_5_, p_150516_6_);
        }
    }
}
