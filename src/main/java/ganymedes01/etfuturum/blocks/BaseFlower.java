package ganymedes01.etfuturum.blocks;

import java.util.List;

import net.minecraft.block.BlockFlower;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;

public class BaseFlower extends BlockFlower {

    public BaseFlower() {
        super(1);
        setStepSound(soundTypeGrass);
        setCreativeTab(EtFuturum.creativeTabBlocks);
    }

    public BaseFlower setUnlocalizedNameWithPrefix(String name) {
        setBlockName(Utils.getUnlocalisedName(name));
        return this;
    }

    public BaseFlower setNames(String name) {
        setUnlocalizedNameWithPrefix(name);
        setBlockTextureName(name);
        return this;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return blockIcon;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(item));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        blockIcon = reg.registerIcon(getTextureName());
    }
}
