package ganymedes01.etfuturum.blocks;

import net.minecraft.world.IBlockAccess;

public interface IEmissiveLayerBlock extends IDoubleLayerBlock {

    default int getEmissiveMinBrightness(IBlockAccess world, int x, int y, int z) {
        return getEmissiveMinBrightness(world.getBlockMetadata(x, y, z));
    }

    /**
     * For the ItemBlock
     */
    int getEmissiveMinBrightness(int meta);

    default boolean doesEmissiveLayerHaveDirShading(int meta) {
        return true;
    }

    default int getEmissiveLayerColor(int meta) {
        return 0xFFFFFF;
    }

    default boolean itemBlockGlows(int meta) {
        return false;
    }
}
