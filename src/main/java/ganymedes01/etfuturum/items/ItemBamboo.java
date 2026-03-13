package ganymedes01.etfuturum.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ganymedes01.etfuturum.ModBlocks;

public class ItemBamboo extends BaseItem {

    public ItemBamboo() {
        setNames("bamboo");
        setFull3D();
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        Block block = world.getBlock(x, y, z);

        if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1) {
            side = 1;
        } else if (block != Blocks.vine && block != Blocks.tallgrass
            && block != Blocks.deadbush
            && !block.isReplaceable(world, x, y, z)) {
                if (side == 0) {
                    --y;
                }

                if (side == 1) {
                    ++y;
                }

                if (side == 2) {
                    --z;
                }

                if (side == 3) {
                    ++z;
                }

                if (side == 4) {
                    --x;
                }

                if (side == 5) {
                    ++x;
                }
            }

        Block getBlock = world.getBlock(x, y - 1, z);
        Block bamboo = getBlock == ModBlocks.BAMBOO.get() || getBlock == ModBlocks.BAMBOO_SAPLING.get()
            ? ModBlocks.BAMBOO.get()
            : ModBlocks.BAMBOO_SAPLING.get();

        if (stack.stackSize == 0) {
            return false;
        } else if (!player.canPlayerEdit(x, y, z, side, stack)) {
            return false;
        } else if (y == 255) {
            return false;
        } else if (world.canPlaceEntityOnSide(bamboo, x, y, z, false, side, player, stack)) {
            int i1 = this.getMetadata(stack.getItemDamage());
            int j1 = bamboo.onBlockPlaced(world, x, y, z, side, p_77648_8_, p_77648_9_, p_77648_10_, i1);

            if (placeBlockAt(stack, player, world, x, y, z, side, p_77648_8_, p_77648_9_, p_77648_10_, j1, bamboo)) {
                world.playSoundEffect(
                    (float) x + 0.5F,
                    (float) y + 0.5F,
                    (float) z + 0.5F,
                    bamboo.stepSound.func_150496_b()/* getPlaceSound */,
                    (bamboo.stepSound.getVolume() + 1.0F) / 2.0F,
                    bamboo.stepSound.getPitch() * 0.8F);
                --stack.stackSize;

                if (getBlock == ModBlocks.BAMBOO_SAPLING.get()) {
                    placeBlockAt(
                        stack,
                        player,
                        world,
                        x,
                        y - 1,
                        z,
                        side,
                        p_77648_8_,
                        p_77648_9_,
                        p_77648_10_,
                        j1,
                        bamboo);
                }
            }

            return true;
        }
        return false;
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int metadata, Block block) {

        if (!world.setBlock(x, y, z, block, metadata, 3)) {
            return false;
        }

        if (world.getBlock(x, y, z) == block) {
            block.onBlockPlacedBy(world, x, y, z, player, stack);
            block.onPostBlockPlaced(world, x, y, z, metadata);
        }

        return true;
    }

}
