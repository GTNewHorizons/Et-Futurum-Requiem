package ganymedes01.etfuturum.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockLodestone extends Block {

    private IIcon topIcon;

    public BlockLodestone() {
        super(Material.rock);
        setHardness(3.5F);
        setResistance(3.5F);
        setHarvestLevel("pickaxe", 1);
        setBlockName(Utils.getUnlocalisedName("lodestone"));
        setBlockTextureName("lodestone");
        setCreativeTab(EtFuturum.creativeTabBlocks);
        Utils.setBlockSound(this, ModSounds.soundLodestone);
        setTickRandomly(true);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side > 1 ? blockIcon : topIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
        this.topIcon = reg.registerIcon(this.getTextureName() + "_top");
    }

}
