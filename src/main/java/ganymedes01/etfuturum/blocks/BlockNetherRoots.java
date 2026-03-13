package ganymedes01.etfuturum.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockNetherRoots extends BlockBush implements ISubBlocksBlock {

    private IIcon[] icons;
    private IIcon[] pottedIcons;
    private final String[] types = new String[] { "crimson_roots", "warped_roots" };
    private static final String name = "roots"; // Bypass stupid MC dev warning for no translation key

    public BlockNetherRoots() {
        Utils.setBlockSound(this, ModSounds.soundNetherRoots);
        setBlockName(name);
        setBlockTextureName(name);
        setCreativeTab(EtFuturum.creativeTabBlocks);
        this.setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.8F, 0.9F);
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
        return EnumPlantType.Nether;
    }

    @Override
    public boolean canBlockStay(World worldIn, int x, int y, int z) {
        Block block = worldIn.getBlock(x, y - 1, z);
        return block == Blocks.mycelium || block.canSustainPlant(worldIn, x, y - 1, z, ForgeDirection.UP, this)
            || block.canSustainPlant(worldIn, x, y - 1, z, ForgeDirection.UP, Blocks.tallgrass);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return icons[meta % icons.length];
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityFlowerPot && world.getBlock(x, y, z)
            .getRenderType() == 33) {
            return pottedIcons[((TileEntityFlowerPot) te).getFlowerPotData() % icons.length];
        }
        return super.getIcon(world, x, y, z, side);
    }

    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        return damageDropped(worldIn.getBlockMetadata(x, y, z));
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        if (ConfigExperiments.enableCrimsonBlocks) {
            list.add(new ItemStack(item, 1, 0));
        }
        if (ConfigExperiments.enableWarpedBlocks) {
            list.add(new ItemStack(item, 1, 1));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        icons = new IIcon[2];
        pottedIcons = new IIcon[2];

        blockIcon = icons[0] = reg.registerIcon("crimson_roots");
        icons[1] = reg.registerIcon("warped_roots");
        pottedIcons[0] = reg.registerIcon("crimson_roots_pot");
        pottedIcons[1] = reg.registerIcon("warped_roots_pot");
    }

    @Override
    public IIcon[] getIcons() {
        return icons;
    }

    @Override
    public String[] getTypes() {
        return types;
    }

    @Override
    public String getNameFor(ItemStack stack) {
        return getTypes()[stack.getItemDamage() % types.length];
    }
}
