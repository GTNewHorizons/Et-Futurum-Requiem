package ganymedes01.etfuturum.mixins.early.worldheight.client;

import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    private static final Logger logger = LogManager.getLogger();

    @Shadow(aliases = "field_147300_g", remap = false) private WorldClient clientWorldController;

    @ModifyConstant(method = { "handleChunkData",
                               "handleMapChunkBulk" } , constant = @Constant(intValue = 256))
    private int getIncreasedWorldHeight(int original) {

        return clientWorldController.getHeight();
    }

    /**
     * @author timb8g
     * @reason change locationOfBlocks processing to int
     */
    @Overwrite
    public void handleMultiBlockChange(S22PacketMultiBlockChange packetIn) {

        int i = packetIn.func_148920_c().chunkXPos * 16;
        int j = packetIn.func_148920_c().chunkZPos * 16;

        if (packetIn.func_148921_d() != null) {

            DataInputStream datainputstream = new DataInputStream(new ByteArrayInputStream(packetIn.func_148921_d()));

            try {

                for (int k = 0; k < packetIn.func_148922_e(); ++k) {

                    int locationOfBlock = datainputstream.readInt();
                    short blockData = datainputstream.readShort();

                    int blockID = blockData >> 4 & 4095;
                    int blockMeta = blockData & 15;

                    int locationX = locationOfBlock >> 16 & 15;
                    int locationZ = locationOfBlock >> 12 & 15;
                    int locationY = locationOfBlock & (ConfigExperiments.maxWorldHeight - 1);

                    this.clientWorldController.func_147492_c(locationX + i, locationY, locationZ + j, Block.getBlockById(blockID), blockMeta);
                }
            } catch (IOException ioexception) {

                logger.error("Couldn\'t handle bulk block update packet", ioexception);
            }
        }
    }
}
