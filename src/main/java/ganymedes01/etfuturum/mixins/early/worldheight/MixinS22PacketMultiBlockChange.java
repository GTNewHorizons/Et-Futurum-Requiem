package ganymedes01.etfuturum.mixins.early.worldheight;

import ganymedes01.etfuturum.ducks.IS22PacketExtended;
import net.minecraft.block.Block;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Mixin(S22PacketMultiBlockChange.class)
public abstract class MixinS22PacketMultiBlockChange implements IS22PacketExtended {

    private static final Logger logger = LogManager.getLogger();

    @Shadow private ChunkCoordIntPair field_148925_b;
    @Shadow private int field_148924_d;
    @Shadow private byte[] field_148926_c;

    @Override
    public void initExtendedS22Packet(int numberOfTiles, int[] locations, Chunk chunk) {

        this.field_148925_b = new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition);
        this.field_148924_d = numberOfTiles;

        int j = 6 * numberOfTiles;  // now packet of 6 size because we need two bytes more for positioning

            try {

            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(j);
            DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);

            for (int k = 0; k < numberOfTiles; ++k) {

                int locationX = locations[k] >> 16 & 15;
                int locationZ = locations[k] >> 12 & 15;
                int locationY = locations[k] & (chunk.worldObj.getHeight() - 1);

                dataoutputstream.writeInt(locations[k]);
                dataoutputstream.writeShort((short)((Block.getIdFromBlock(chunk.getBlock(locationX, locationY, locationZ)) & 4095) << 4 | chunk.getBlockMetadata(locationX, locationY, locationZ) & 15));
            }

            this.field_148926_c = bytearrayoutputstream.toByteArray();

            if (this.field_148926_c.length != j) {

                throw new RuntimeException("Expected length " + j + " doesn\'t match received length " + this.field_148926_c.length);
            }
        } catch (IOException ioexception) {

            logger.error("Couldn\'t create bulk block update packet", ioexception);

            this.field_148926_c = null;
        }
    }
}
