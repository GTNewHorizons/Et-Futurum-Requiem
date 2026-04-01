package ganymedes01.etfuturum.ducks;

import net.minecraft.world.chunk.Chunk;

public interface IS22PacketExtended {

    void initExtendedS22Packet(int numberOfTiles, int[] locations, Chunk chunk);
}
