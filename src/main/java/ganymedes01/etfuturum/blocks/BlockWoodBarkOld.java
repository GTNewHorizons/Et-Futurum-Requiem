package ganymedes01.etfuturum.blocks;

import net.minecraft.block.BlockOldLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockWoodBarkOld extends BlockOldLog implements ISubBlocksBlock {

    public static final String[] types = new String[] { "oak_wood", "spruce_wood", "birch_wood", "jungle_wood" };

    public BlockWoodBarkOld() {
        setBlockName(Utils.getUnlocalisedName("wood_stripped"));
        setCreativeTab(EtFuturum.creativeTabBlocks);
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
        return getTypes()[stack.getItemDamage() % getTypes().length];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.field_150167_a = new IIcon[field_150168_M.length];
        this.field_150166_b = new IIcon[field_150168_M.length];
        for (int i = 0; i < field_150168_M.length; i++) {
            field_150167_a[i] = field_150166_b[i] = Blocks.log.getIcon(2, i);
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
