package ganymedes01.etfuturum.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockPurpurSlab extends BaseSlab {

    public BlockPurpurSlab(boolean isDouble) {
        super(isDouble, Material.rock, "purpur");
        setResistance(6);
        setHardness(2.0F);
        setBlockName(Utils.getUnlocalisedName("purpur_slab"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        IIcon[] icon = new IIcon[getTypes().length];
        icon[0] = ModBlocks.PURPUR_BLOCK.get()
            .getIcon(2, 0);
        setIcons(icon);
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return !(entity instanceof EntityDragon);
    }
}
