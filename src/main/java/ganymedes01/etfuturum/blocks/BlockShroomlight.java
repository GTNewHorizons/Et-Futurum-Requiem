package ganymedes01.etfuturum.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

import ganymedes01.etfuturum.client.sound.ModSounds;

public class BlockShroomlight extends BaseBlock {

    public BlockShroomlight() {
        super(Material.gourd);
        setNames("shroomlight");
        setBlockSound(ModSounds.soundShroomlight);
        setResistance(1);
        setHardness(1);
        setLightLevel(1);
    }

    @Override
    public boolean isLeaves(IBlockAccess world, int x, int y, int z) {
        return true;
    }
}
