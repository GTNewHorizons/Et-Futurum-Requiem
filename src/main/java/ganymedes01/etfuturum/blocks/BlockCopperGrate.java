package ganymedes01.etfuturum.blocks;

import net.minecraft.world.IBlockAccess;

import ganymedes01.etfuturum.client.sound.ModSounds;

public class BlockCopperGrate extends BlockCopper {

    public BlockCopperGrate() {
        super(
            "copper_grate",
            "exposed_copper_grate",
            "weathered_copper_grate",
            "oxidized_copper_grate",
            "waxed_copper_grate",
            "waxed_exposed_copper_grate",
            "waxed_weathered_copper_grate",
            "waxed_oxidized_copper_grate");
        setBlockSound(ModSounds.soundCopperGrate);
    }

    @Override
    public int getCopperMeta(int meta) {
        return meta + (meta > 3 ? 4 : 0);
    }

    @Override
    public int getFinalCopperMeta(IBlockAccess world, int x, int y, int z, int meta, int worldMeta) {
        return meta - (meta > 3 ? 4 : 0);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
}
