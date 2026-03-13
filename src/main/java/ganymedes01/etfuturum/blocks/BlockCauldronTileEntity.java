package ganymedes01.etfuturum.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public abstract class BlockCauldronTileEntity extends BlockContainer {

    protected BlockCauldronTileEntity(Material p_i45386_1_) {
        super(p_i45386_1_);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return Blocks.cauldron.getIcon(side, meta);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask,
        List<AxisAlignedBB> list, Entity collider) {
        Blocks.cauldron.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Items.cauldron;
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Items.cauldron;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

}
