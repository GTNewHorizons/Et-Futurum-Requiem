package ganymedes01.etfuturum.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockStoneSlab1 extends BaseSlab {

    public BlockStoneSlab1(boolean p_i45410_1_) {
        super(p_i45410_1_, Material.rock, "stone", "mossy_cobblestone", "mossy_stone_brick", "cut_sandstone");
        setHardness(2F);
        setResistance(6F);
        setBlockName(Utils.getUnlocalisedName("stone_slab"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        IIcon[] icon = new IIcon[getTypes().length];
        icon[0] = Blocks.stone.getIcon(2, 0);
        icon[1] = Blocks.mossy_cobblestone.getIcon(2, 0);
        icon[2] = Blocks.stonebrick.getIcon(2, 1);
        icon[3] = Blocks.sandstone.getIcon(2, 2);
        setIcons(icon);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if ((meta % 8) % getIcons().length == 3) {
            return Blocks.sandstone.getIcon(side, 2);
        }
        return super.getIcon(side, meta);
    }
}
