package ganymedes01.etfuturum.blocks;

import net.minecraft.block.BlockSandStone;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockRedSandstone extends BlockSandStone {

    public BlockRedSandstone() {
        setHardness(0.8F);
        setBlockTextureName("red_sandstone");
        setBlockName(Utils.getUnlocalisedName("red_sandstone"));
        setCreativeTab(EtFuturum.creativeTabBlocks);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.field_150158_M = new IIcon[3];

        this.field_150158_M[0] = reg.registerIcon(getTextureName());
        this.field_150158_M[1] = reg.registerIcon("chiseled_" + getTextureName());
        this.field_150158_M[2] = reg.registerIcon("cut_" + getTextureName());

        this.field_150159_N = reg.registerIcon(getTextureName() + "_top");
        this.field_150160_O = reg.registerIcon(getTextureName() + "_bottom");
    }
}
