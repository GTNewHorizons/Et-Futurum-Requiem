package ganymedes01.etfuturum.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;

public class BaseSubtypesSand extends BlockFalling implements ISubBlocksBlock {

    private IIcon[] icons;
    private final String[] types;

    private final int startMeta;

    public BaseSubtypesSand(Material material, String... types) {
        this(material, 0, types);
    }

    public BaseSubtypesSand(Material material, int startMeta, String... types) {
        super(material);
        this.startMeta = startMeta;
        this.types = types;
        this.setCreativeTab(EtFuturum.creativeTabBlocks);
    }

    public BaseSubtypesSand setUnlocalizedNameWithPrefix(String name) {
        setBlockName(Utils.getUnlocalisedName(name));
        return this;
    }

    public BaseSubtypesSand setNames(String name) {
        setUnlocalizedNameWithPrefix(name);
        setBlockTextureName(name);
        return this;
    }

    @Override
    public IIcon[] getIcons() {
        return icons;
    }

    protected void setIcons(IIcon[] icons) {
        this.icons = icons;
    }

    @Override
    public String[] getTypes() {
        return types;
    }

    @Override
    public String getNameFor(ItemStack stack) {
        String type = getTypes()[Math.max(startMeta, stack.getItemDamage() % getTypes().length)];
        return "".equals(type) ? getUnlocalizedName().replace("tile.", "")
            .replace("etfuturum.", "") : type;
    }

    @Override
    public int damageDropped(int meta) {
        return meta % getTypes().length;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (int i = startMeta; i < getTypes().length; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return getIcons()[Math.max(startMeta, meta % icons.length)];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        setIcons(new IIcon[getTypes().length]);
        for (int i = 0; i < getIcons().length; i++) {
            getIcons()[i] = "".equals(getTypes()[i]) ? reg.registerIcon(getTextureName())
                : reg.registerIcon((getTextureDomain().isEmpty() ? "" : getTextureDomain() + ":") + getTypes()[i]);
        }
        blockIcon = getIcons()[0];
    }

    /**
     * Ticks the block if it's been scheduled
     */
    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote) {
            this.fallIfAble(worldIn, x, y, z);
        }
    }

    public EntityFallingBlock getFallingBlock(World world, double x, double y, double z, BlockFalling block, int meta) {
        return new EntityFallingBlock(world, x, y, z, block, meta);
    }

    private void fallIfAble(World p_149830_1_, int p_149830_2_, int p_149830_3_, int p_149830_4_) {
        if (func_149831_e/* canFallBelow */(p_149830_1_, p_149830_2_, p_149830_3_ - 1, p_149830_4_)
            && p_149830_3_ >= 0) {
            byte b0 = 32;

            if (!fallInstantly && p_149830_1_.checkChunksExist(
                p_149830_2_ - b0,
                p_149830_3_ - b0,
                p_149830_4_ - b0,
                p_149830_2_ + b0,
                p_149830_3_ + b0,
                p_149830_4_ + b0)) {
                if (!p_149830_1_.isRemote) {
                    EntityFallingBlock entityfallingblock = getFallingBlock(
                        p_149830_1_,
                        (float) p_149830_2_ + 0.5F,
                        (float) p_149830_3_ + 0.5F,
                        (float) p_149830_4_ + 0.5F,
                        this,
                        p_149830_1_.getBlockMetadata(p_149830_2_, p_149830_3_, p_149830_4_));
                    this.func_149829_a(entityfallingblock); // onStartFalling
                    p_149830_1_.spawnEntityInWorld(entityfallingblock);
                }
            } else {
                p_149830_1_.setBlockToAir(p_149830_2_, p_149830_3_, p_149830_4_);

                while (func_149831_e/* canFallBelow */(p_149830_1_, p_149830_2_, p_149830_3_ - 1, p_149830_4_)
                    && p_149830_3_ > 0) {
                    --p_149830_3_;
                }

                if (p_149830_3_ > 0) {
                    p_149830_1_.setBlock(p_149830_2_, p_149830_3_, p_149830_4_, this);
                }
            }
        }
    }
}
