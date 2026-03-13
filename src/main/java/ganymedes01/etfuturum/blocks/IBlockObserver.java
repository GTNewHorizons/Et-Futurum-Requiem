package ganymedes01.etfuturum.blocks;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public interface IBlockObserver {

    default void observedNeighborChange(World world, int observerX, int observerY, int observerZ, Block changedBlock,
        int changedX, int changedY, int changedZ) {}
}
