package ganymedes01.etfuturum.world.generate.decorate;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockNylium;

public class WorldGenHugeFungus extends WorldGenAbstractTree {

    /**
     * True for non-natural grown trees
     */
    private final boolean planted;

    public final Block log;
    private final Block hat;
    private final int logMeta;
    private final int hatMeta;

    public WorldGenHugeFungus(boolean notify, int metaLog, int metaLeaf, Block log, Block leaf) {
        this(notify, metaLog, metaLeaf, log, leaf, false);
    }

    public WorldGenHugeFungus(boolean notify, int metaLog, int metaLeaf, Block log, Block leaf, boolean planted) {
        super(notify);
        this.logMeta = metaLog;
        this.hatMeta = metaLeaf;
        this.log = log;
        this.hat = leaf;
        this.planted = planted;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        Block blockstate = world.getBlock(x, y - 1, z);
        if (blockstate instanceof BlockNylium) {
            int i = MathHelper.getRandomIntegerInRange(random, 4, 13);
            if (random.nextInt(12) == 0) {
                i *= 2;
            }

            // if (!planted) {
            // int j = chunkgenerator.getGenDepth(); //Probably refers to generating below y 0 which we will not need
            // if (blockpos1.getY() + i + 1 >= j) {
            // return false;
            // }
            // }

            boolean flag = !planted && random.nextFloat() < 0.06F;
            world.setBlock(x, y, z, Blocks.air);
            if (!planted) {
                world.setBlock(x, y - 1, z, Blocks.netherrack);
            }
            this.placeStem(world, random, x, y, z, i, flag);
            this.placeHat(world, random, x, y, z, i, flag);
            return true;
        }
        return false;
    }

    @Override
    protected boolean isReplaceable(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block instanceof BlockLeavesBase || block instanceof BlockSapling
            || block instanceof BlockFlower
            || block.isReplaceable(world, x, y, z);
    }

    private void placeStem(World world, Random rand, int x, int y, int z, int p_285249_, boolean p_285355_) {
        int i = p_285355_ ? 1 : 0;

        for (int j = -i; j <= i; ++j) {
            for (int k = -i; k <= i; ++k) {
                boolean flag = p_285355_ && MathHelper.abs(j) == i && MathHelper.abs(k) == i;

                for (int l = 0; l < p_285249_; ++l) {
                    int offX = x + j;
                    int offY = y + l;
                    int offZ = z + k;
                    if (isReplaceable(world, offX, offY, offZ)) {
                        if (planted) {
                            if (!world.isAirBlock(offX, offY - 1, offZ)) {
                                world.setBlockToAir(offX, offY, offZ);
                            }

                            setBlockAndNotifyAdequately(world, offX, offY, offZ, log, logMeta);
                        } else if (flag) {
                            if (rand.nextFloat() < 0.1F) {
                                setBlockAndNotifyAdequately(world, offX, offY, offZ, log, logMeta);
                            }
                        } else {
                            setBlockAndNotifyAdequately(world, offX, offY, offZ, log, logMeta);
                        }
                    }
                }
            }
        }

    }

    private void placeHat(World world, Random rand, int x, int y, int z, int p_285156_, boolean p_285265_) {
        boolean flag = hat == ModBlocks.NETHER_WART.get() && hatMeta == 0;
        int i = Math.min(rand.nextInt(1 + p_285156_ / 3) + 5, p_285156_);
        int j = p_285156_ - i;

        for (int k = j; k <= p_285156_; ++k) {
            int l = k < p_285156_ - rand.nextInt(3) ? 2 : 1;
            if (i > 8 && k < j + 4) {
                l = 3;
            }

            if (p_285265_) {
                l++;
            }

            for (int i1 = -l; i1 <= l; ++i1) {
                for (int j1 = -l; j1 <= l; ++j1) {
                    boolean flag1 = i1 == -l || i1 == l;
                    boolean flag2 = j1 == -l || j1 == l;
                    boolean flag3 = !flag1 && !flag2 && k != p_285156_;
                    boolean flag4 = flag1 && flag2;
                    boolean flag5 = k < j + 3;
                    int offX = x + i1;
                    int offY = y + k;
                    int offZ = z + j1;
                    if (isReplaceable(world, offX, offY, offZ)) {
                        // if (planted && !p_285200_.isAirBlock(mutableblockpos.getX(), mutableblockpos.getY() - 1,
                        // mutableblockpos.getZ())) {
                        // p_285200_.setBlockToAir(mutableblockpos.getX(), mutableblockpos.getY(),
                        // mutableblockpos.getZ());
                        // }

                        if (flag5) {
                            if (!flag3) {
                                this.placeHatDropBlock(world, rand, offX, offY, offZ, flag);
                            }
                        } else if (flag3) {
                            this.placeHatBlock(world, rand, offX, offY, offZ, 0.1F, 0.2F, flag ? 0.1F : 0.0F);
                        } else if (flag4) {
                            this.placeHatBlock(world, rand, offX, offY, offZ, 0.01F, 0.7F, flag ? 0.083F : 0.0F);
                        } else {
                            this.placeHatBlock(world, rand, offX, offY, offZ, 5.0E-4F, 0.98F, flag ? 0.07F : 0.0F);
                        }
                    }
                }
            }
        }
    }

    private void placeHatBlock(World world, Random rand, int x, int y, int z, float p_225054_, float p_225055_,
        float p_225056_) {
        if (rand.nextFloat() < p_225054_) {
            world.setBlock(x, y, z, ModBlocks.SHROOMLIGHT.get(), 0, 3);
        } else if (rand.nextFloat() < p_225055_) {
            setBlockAndNotifyAdequately(world, x, y, z, hat, hatMeta);
            if (rand.nextFloat() < p_225056_) {
                tryPlaceWeepingVines(world, rand, x, y, z);
            }
        }
    }

    private void placeHatDropBlock(World world, Random rand, int x, int y, int z, boolean p_225069_) {
        if (world.getBlock(x, y - 1, z) == hat && world.getBlockMetadata(x, y - 1, z) == hatMeta) {
            setBlockAndNotifyAdequately(world, x, y, z, hat, hatMeta);
        } else if ((double) rand.nextFloat() < 0.15D) {
            setBlockAndNotifyAdequately(world, x, y, z, hat, hatMeta);
            if (p_225069_ && rand.nextInt(11) == 0) {
                tryPlaceWeepingVines(world, rand, x, y, z);
            }
        }
    }

    private static void tryPlaceWeepingVines(World world, Random rand, int x, int y, int z) {
        if (world.getBlock(x, y - 1, z)
            .getMaterial() == Material.air) {
            int i = MathHelper.getRandomIntegerInRange(rand, 1, 5);
            if (rand.nextInt(7) == 0) {
                i *= 2;
            }
            WorldGenWeepingVines.placeWeepingVinesColumn(world, rand, x, y, z, i/* , 23, 25 */);
        }
    }
}
