package ganymedes01.etfuturum.blocks;

import java.util.List;
import java.util.Map;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BaseSubtypesBlock extends BaseBlock implements ISubBlocksBlock {

    private IIcon[] icons;
    private final String[] types;

    private final int startMeta;

    public BaseSubtypesBlock(Material material, String... types) {
        this(material, 0, types);
    }

    public BaseSubtypesBlock(Material material, int startMeta, String... types) {
        super(material);
        this.startMeta = startMeta;
        this.types = types;
        if (!"".equals(types[0])) {
            setNames(types[0]);
        }
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
                : reg.registerIcon(
                    (getTextureDomain().isEmpty() ? "" : getTextureDomain() + ":")
                        + (getTextureSubfolder().isEmpty() ? "" : getTextureSubfolder() + "/")
                        + getTypes()[i]);
        }
    }

    private final Map<Integer, Float> hardnesses = Maps.newHashMap();
    private final Map<Integer, Float> resistances = Maps.newHashMap();

    @Override
    public float getBlockHardness(World worldIn, int x, int y, int z) {
        return hardnesses.getOrDefault(worldIn.getBlockMetadata(x, y, z), super.getBlockHardness(worldIn, x, y, z));
    }

    public BaseSubtypesBlock setHardnessValues(float hardness, int... metas) {
        if (metas.length == 0) {
            setHardness(hardness);
        } else for (int meta : metas) {
            hardnesses.put(meta, hardness);
        }
        return this;
    }

    @Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX,
        double explosionY, double explosionZ) {
        Float resistance = resistances.get(world.getBlockMetadata(x, y, z));
        if (resistance != null) {
            return resistance / 5.0F;
        }
        return super.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
    }

    public BaseSubtypesBlock setResistanceValues(float resistance, int... metas) {
        if (metas.length == 0) {
            setResistance(resistance);
        } else for (int meta : metas) {
            resistances.put(meta, resistance);
        }
        return this;
    }
}
