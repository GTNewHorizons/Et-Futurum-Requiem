package ganymedes01.etfuturum.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;

public class BlockPackedMud extends BaseSubtypesBlock {

    public BlockPackedMud() {
        super(Material.rock, "packed_mud", "mud_bricks");
        setHardness(1);
        setResistance(3);
        setHarvestLevel("pickaxe", 0);
        setCreativeTab(EtFuturum.creativeTabBlocks);
        setBlockSound(ModSounds.soundPackedMud);
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        if (world.getBlockMetadata(x, y, z) == 1) {
            return 1.5F;
        }
        return blockHardness;
    }
}
