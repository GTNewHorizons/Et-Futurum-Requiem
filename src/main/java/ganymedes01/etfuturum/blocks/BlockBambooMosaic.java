package ganymedes01.etfuturum.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;

public class BlockBambooMosaic extends BaseBlock {

    public BlockBambooMosaic() {
        super(Material.wood);
        setHardness(2.0F);
        setResistance(5.0F);
        setNames("bamboo_mosaic");
        setCreativeTab(EtFuturum.creativeTabBlocks);
        setBlockSound(ModSounds.soundBambooWood);
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
