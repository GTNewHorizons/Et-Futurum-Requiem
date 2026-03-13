package ganymedes01.etfuturum.blocks.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.world.World;

public class BasePotableItemBlock extends ItemBlock {

    public BasePotableItemBlock(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @Override
    public boolean onItemUse(ItemStack heldStack, EntityPlayer entityPlayer, World world, int x, int y, int z,
        int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        if (this.canPlacePot(heldStack, entityPlayer, world, x, y, z)) {
            if (!entityPlayer.isSwingInProgress) entityPlayer.swingItem();
            return true;
        }
        return super.onItemUse(
            heldStack,
            entityPlayer,
            world,
            x,
            y,
            z,
            p_77648_7_,
            p_77648_8_,
            p_77648_9_,
            p_77648_10_);
    }

    @Override
    public boolean func_150936_a(World p_150936_1_, int p_150936_2_, int p_150936_3_, int p_150936_4_, int p_150936_5_,
        EntityPlayer p_150936_6_, ItemStack p_150936_7_) {
        boolean flag = super.func_150936_a(
            p_150936_1_,
            p_150936_2_,
            p_150936_3_,
            p_150936_4_,
            p_150936_5_,
            p_150936_6_,
            p_150936_7_);
        if (!flag && this.canPlacePot(p_150936_7_, p_150936_6_, p_150936_1_, p_150936_2_, p_150936_3_, p_150936_4_)) {
            if (!p_150936_6_.isSwingInProgress) p_150936_6_.swingItem();
            return true;
        }
        return flag;
    }

    private boolean canPlacePot(ItemStack heldStack, EntityPlayer entityPlayer, World world, int x, int y, int z) {
        if (world.getBlock(x, y, z) == Blocks.flower_pot && !entityPlayer.isSneaking()
            && world.getTileEntity(x, y, z) instanceof TileEntityFlowerPot potTile) {

            Item potableItem = heldStack.getItem();
            int potableMeta = heldStack.getItemDamage();

            if (potableItem != null && potTile.getFlowerPotItem() == null) {
                potTile.func_145964_a(potableItem, potableMeta);
                potTile.markDirty();

                if (!world.setBlockMetadataWithNotify(x, y, z, heldStack.getItemDamage(), 2)) {
                    world.markBlockForUpdate(x, y, z);
                }

                if (!entityPlayer.capabilities.isCreativeMode && --heldStack.stackSize <= 0) {
                    entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                }

                entityPlayer.inventoryContainer.detectAndSendChanges();
                return true;
            }
        }
        return false;
    }
}
