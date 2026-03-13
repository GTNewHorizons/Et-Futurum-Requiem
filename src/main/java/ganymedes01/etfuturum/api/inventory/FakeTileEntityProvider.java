package ganymedes01.etfuturum.api.inventory;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface FakeTileEntityProvider {

    /**
     * Allows blocks to provide a tile entity to legacy code that doesn't support functionality on blocks
     * without TEs (e.g. hoppers). The returned TE is NOT actually part of a world and should not be
     * treated as such. It may also be a repurposed object to save on allocations.
     *
     * @param world the world the block is in
     * @param x     x coordinate of block
     * @param y     y coordinate of block
     * @param z     z coordinate of block
     * @return the tile entity
     */
    TileEntity getFakeTileEntity(World world, int x, int y, int z);
}
