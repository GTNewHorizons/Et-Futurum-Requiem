package ganymedes01.etfuturum.blocks;

import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockAzaleaLeaves extends BaseLeaves {

    public BlockAzaleaLeaves() {
        super("azalea", "flowering_azalea");
        Utils.setBlockSound(this, ModSounds.soundAzaleaLeaves);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return ModBlocks.AZALEA.getItem();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return 0xFFFFFF;
    }

    @Override
    public int getRenderColor(int meta) {
        return 0xFFFFFF;
    }
}
