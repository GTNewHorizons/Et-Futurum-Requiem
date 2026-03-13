package ganymedes01.etfuturum.blocks;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockBambooBlock extends BaseLog {

    public BlockBambooBlock(String type) {
        super(type);
        types = new String[] { type + "_block", "stripped_" + type + "_block" };
        setBlockName(Utils.getUnlocalisedName(type + "_block"));
        setBlockSound(ModSounds.soundBambooWood);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0));
        if (ConfigBlocksItems.enableStrippedLogs) {
            list.add(new ItemStack(itemIn, 1, 1));
        }
    }

    @Override
    public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        field_150167_a = new IIcon[types.length];
        field_150166_b = new IIcon[types.length];

        blockIcon = field_150167_a[0] = iconRegister.registerIcon(types[0]);
        field_150166_b[0] = iconRegister.registerIcon(types[0] + "_top");

        field_150167_a[1] = iconRegister.registerIcon(types[1]);
        field_150166_b[1] = iconRegister.registerIcon(types[1] + "_top");
    }
}
