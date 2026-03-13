package ganymedes01.etfuturum.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockCutCopperSlab extends BaseSlab implements IDegradable {

    public BlockCutCopperSlab(boolean isDouble) {
        this(
            isDouble,
            "cut_copper",
            "exposed_cut_copper",
            "weathered_cut_copper",
            "oxidized_cut_copper",
            "waxed_cut_copper",
            "waxed_exposed_cut_copper",
            "waxed_weathered_cut_copper",
            "waxed_oxidized_cut_copper");
    }

    public BlockCutCopperSlab(boolean isDouble, String... types) {
        super(isDouble, Material.iron, types);
        setHardness(3);
        setResistance(6);
        setHarvestLevel("pickaxe", 1);
        setBlockName(Utils.getUnlocalisedName("cut_copper_slab"));
        setBlockTextureName("cut_copper");
        setBlockSound(ModSounds.soundCopper);
        setTickRandomly(true);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        tickDegradation(world, x, y, z, rand);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float subX,
        float subY, float subZ) {
        return tryWaxOnWaxOff(world, x, y, z, entityPlayer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        setIcons(new IIcon[4]);
        for (int i = 0; i < getIcons().length; i++) {
            getIcons()[i] = reg
                .registerIcon((getTextureDomain().isEmpty() ? "" : getTextureDomain() + ":") + getTypes()[i]);
        }
    }

    @Override
    public int getFinalCopperMeta(IBlockAccess world, int x, int y, int z, int copperMeta, int worldMeta) {
        return (copperMeta - (copperMeta > 11 ? 8 : 4)) + (worldMeta > 7 ? 8 : 0);
    }

    public int getCopperMeta(int meta) {
        return meta % 8 + (meta % 8 < 4 ? 4 : 8);
    }

    public Block getCopperBlockFromMeta(int meta) {
        return this;
    }

}
