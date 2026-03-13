package ganymedes01.etfuturum.blocks;

import java.util.List;

import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.core.utils.Utils;

public class BaseLog extends BlockLog implements ISubBlocksBlock {

    protected final String type;
    protected String[] types;

    public BaseLog(String type) {
        super();
        this.type = type;
        types = new String[] { type + "_log", type + "_wood", "stripped_" + type + "_log",
            "stripped_" + type + "_wood" };
        setBlockName(Utils.getUnlocalisedName(type + "_log"));
        Utils.setBlockSound(this, soundTypeWood);
        setCreativeTab(EtFuturum.creativeTabBlocks);
    }

    public BaseLog setBlockSound(SoundType type) {
        Utils.setBlockSound(this, type);
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        field_150167_a = new IIcon[types.length];
        field_150166_b = new IIcon[types.length];

        blockIcon = field_150166_b[1] = field_150167_a[0] = field_150167_a[1] = iconRegister.registerIcon(types[0]);
        field_150166_b[3] = field_150167_a[2] = field_150167_a[3] = iconRegister.registerIcon(types[2]);
        field_150166_b[0] = iconRegister.registerIcon(types[0] + "_top");
        field_150166_b[2] = iconRegister.registerIcon(types[2] + "_top");
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0));
        if (ConfigBlocksItems.enableBarkLogs) {
            list.add(new ItemStack(itemIn, 1, 1));
        }
        if (ConfigBlocksItems.enableStrippedLogs) {
            list.add(new ItemStack(itemIn, 1, 2));
            if (ConfigBlocksItems.enableBarkLogs) {
                list.add(new ItemStack(itemIn, 1, 3));
            }
        }
    }

    @Override
    public IIcon[] getIcons() {
        return field_150167_a;
    }

    @Override
    public String[] getTypes() {
        return types;
    }

    @Override
    public String getNameFor(ItemStack stack) {
        return types[stack.getItemDamage() % types.length];
    }

    @Override
    public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return true;
    }

    @Override
    public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return 20;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        return 5;
    }
}
