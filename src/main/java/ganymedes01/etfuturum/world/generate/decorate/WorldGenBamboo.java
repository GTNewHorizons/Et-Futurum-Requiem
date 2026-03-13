package ganymedes01.etfuturum.world.generate.decorate;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockBamboo;

public class WorldGenBamboo extends WorldGenerator {

    private final Block bamboo;

    public WorldGenBamboo(Block bamboo) {
        this.bamboo = bamboo;
    }

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int height = MathHelper.getRandomIntegerInRange(rand, 6, 16);
        if (!ModBlocks.BAMBOO.get()
            .canBlockStay(world, x, y + 1, z)) {
            return false;
        }

        for (int i = 1; i <= height; i++) {
            if (!canReplace(world, x, y + i, z)) {
                return false;
            }
        }

        for (int i = 1; i <= height; i++) {
            int meta = 1;
            if (i == height) {
                meta = BlockBamboo.setStage(meta, true);
            }

            if (i >= height - 1) {
                meta = BlockBamboo.setLeavesSize(meta, 2);
            } else if (i >= height - 2) {
                meta = BlockBamboo.setLeavesSize(meta, 1);
            }
            world.setBlock(x, y + i, z, bamboo, meta, 2);
        }
        return true;
    }

    private boolean canReplace(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block.getMaterial() == Material.air || (!block.getMaterial()
            .isLiquid() && block.isReplaceable(world, x, y, z));
    }
}
