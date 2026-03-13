package ganymedes01.etfuturum.blocks;

import net.minecraft.block.BlockOldLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockStrippedOldLog extends BlockOldLog implements ISubBlocksBlock {

    public static final String[] icon_names = new String[] { "stripped_oak_log", "stripped_spruce_log",
        "stripped_birch_log", "stripped_jungle_log" };

    public BlockStrippedOldLog() {
        setBlockName(Utils.getUnlocalisedName("log2_stripped"));
        setCreativeTab(EtFuturum.creativeTabBlocks);
    }

    @Override
    public IIcon[] getIcons() {
        return field_150167_a;
    }

    @Override
    public String[] getTypes() {
        return icon_names;
    }

    @Override
    public String getNameFor(ItemStack stack) {
        return getTypes()[stack.getItemDamage() % getTypes().length];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.field_150167_a = new IIcon[icon_names.length];
        this.field_150166_b = new IIcon[icon_names.length];

        for (int i = 0; i < this.field_150167_a.length; ++i) {
            this.field_150167_a[i] = iconRegister.registerIcon(icon_names[i]);
            this.field_150166_b[i] = iconRegister.registerIcon(icon_names[i] + "_top");
        }
    }

    @Override
    public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return true;
    }

    @Override
    public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return 5;
    }
}
