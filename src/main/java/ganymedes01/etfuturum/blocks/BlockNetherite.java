package ganymedes01.etfuturum.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockNetherite extends Block {

    public BlockNetherite() {
        super(Material.iron);
        setHarvestLevel("pickaxe", 3);
        setHardness(50F);
        setResistance(1200F);
        Utils.setBlockSound(this, ModSounds.soundNetherite);
        setBlockTextureName("netherite_block");
        setBlockName(Utils.getUnlocalisedName("netherite_block"));
        setCreativeTab(EtFuturum.creativeTabBlocks);
    }

    @Override
    public boolean isBeaconBase(IBlockAccess world, int x, int y, int z, int bX, int bY, int bZ) {
        return true;
    }
}
