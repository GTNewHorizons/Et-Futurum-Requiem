package ganymedes01.etfuturum.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockInfestedDeepslate extends BlockSilverfish {

    public static final String[] icon_names = new String[] { "deepslate" };

    public BlockInfestedDeepslate() {
        super();
        setBlockName(Utils.getUnlocalisedName("infested_deepslate"));
        setCreativeTab(EtFuturum.creativeTabBlocks);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return ModBlocks.DEEPSLATE.get()
            .getBlockTextureFromSide(side);
    }

    public static boolean func_150196_a(Block p_150196_0_) {
        return p_150196_0_ == ModBlocks.DEEPSLATE.get();
    }

    public static int func_150195_a(Block p_150195_0_, int p_150195_1_) {
        return 0;
    }

    @Override
    protected ItemStack createStackedBlock(int meta) {
        return ModBlocks.DEEPSLATE.newItemStack();
    }

    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        return 0;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < icon_names.length; ++i) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }
}
