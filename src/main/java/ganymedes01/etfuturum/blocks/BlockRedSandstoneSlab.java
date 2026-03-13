package ganymedes01.etfuturum.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockRedSandstoneSlab extends BaseSlab {

    public BlockRedSandstoneSlab(boolean isDouble) {
        super(isDouble, Material.rock, "red_sandstone", "cut_red_sandstone");
        setResistance(6);
        setHardness(2.0F);
        setBlockName(Utils.getUnlocalisedName("red_sandstone_slab"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        IIcon[] icon = new IIcon[getTypes().length];
        icon[0] = ModBlocks.RED_SANDSTONE.get()
            .getIcon(2, 0);
        icon[1] = ModBlocks.RED_SANDSTONE.get()
            .getIcon(2, 2);
        setIcons(icon);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return ModBlocks.RED_SANDSTONE.get()
            .getIcon(side, (meta % 8) % getIcons().length == 0 ? 0 : 2);
    }
}
