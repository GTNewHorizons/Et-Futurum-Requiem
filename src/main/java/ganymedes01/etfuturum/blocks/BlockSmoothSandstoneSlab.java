package ganymedes01.etfuturum.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockSmoothSandstoneSlab extends BaseSlab {

    private final int meta;

    public BlockSmoothSandstoneSlab(int theMeta, boolean p_i45410_1_) {
        super(p_i45410_1_, Material.rock, "smooth" + (theMeta == 1 ? "_red" : "") + "_sandstone");
        meta = theMeta;
        setResistance(6);
        setHardness(2.0F);
        setBlockName(Utils.getUnlocalisedName("smooth" + (meta == 1 ? "_red" : "") + "_sandstone_slab"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        IIcon[] icon = new IIcon[getTypes().length];
        blockIcon = icon[0] = this.meta == 1 ? ModBlocks.SMOOTH_RED_SANDSTONE.get()
            .getIcon(2, 2)
            : ModBlocks.SMOOTH_SANDSTONE.get()
                .getIcon(2, 2);
        setIcons(icon);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.meta == 1 ? ModBlocks.SMOOTH_RED_SANDSTONE.get()
            .getIcon(side, 2)
            : ModBlocks.SMOOTH_SANDSTONE.get()
                .getIcon(side, 2);
    }
}
