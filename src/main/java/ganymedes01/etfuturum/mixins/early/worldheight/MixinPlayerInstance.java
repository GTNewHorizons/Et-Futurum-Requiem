package ganymedes01.etfuturum.mixins.early.worldheight;

import ganymedes01.etfuturum.ducks.IS22PacketExtended;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;

@Mixin(targets = "net.minecraft.server.management.PlayerManager$PlayerInstance")
public abstract class MixinPlayerInstance {

    @Shadow private int numberOfTilesToUpdate;
    @Shadow private ChunkCoordIntPair chunkLocation;
    @Shadow private int flagsYAreasToUpdate;

    @Shadow public abstract void sendToAllPlayersWatchingChunk(Packet packet);
    @Shadow public abstract void sendTileToAllPlayersWatchingChunk(TileEntity tileEntity);

    @Shadow(remap = false) PlayerManager this$0;

    @Unique private int[] locationsOfBlockChangeForExtendedHeight = new int[64];

    /**
     * @author timb8g
     * @reason Change locationOfBlockChange to int[]
     */
    @Overwrite
    public void flagChunkForUpdate(int x, int y, int z) {

        if (this.numberOfTilesToUpdate == 0) {

            ((AccessorPlayerManager)this.this$0).getChunkWatcherWithPlayers().add(this);
        }

        this.flagsYAreasToUpdate |= 1 << (y >> 4);

        //if (this.numberOfTilesToUpdate < 64) //Forge; Cache everything, so always run
        {
            int locationOfBlock = (x << 16 | z << 12 | y);

            for (int l = 0; l < this.numberOfTilesToUpdate; ++l) {

                if (this.locationsOfBlockChangeForExtendedHeight[l] == locationOfBlock) {

                    return;
                }
            }

            if (this.numberOfTilesToUpdate == locationsOfBlockChangeForExtendedHeight.length) {

                locationsOfBlockChangeForExtendedHeight = Arrays.copyOf(locationsOfBlockChangeForExtendedHeight, locationsOfBlockChangeForExtendedHeight.length << 1);
            }

            this.locationsOfBlockChangeForExtendedHeight[this.numberOfTilesToUpdate++] = locationOfBlock;
        }
    }

    /**
     * @author timb8g
     * @reason Change locationOfBlockChange to int[] and adjust max world height
     */
    @Overwrite
    public void sendChunkUpdate() {

        if (this.numberOfTilesToUpdate != 0) {

            int i;
            int j;
            int k;

            if (this.numberOfTilesToUpdate == 1) {

                i = this.chunkLocation.chunkXPos * 16 + (this.locationsOfBlockChangeForExtendedHeight[0] >> 16 & 15);
                j = this.locationsOfBlockChangeForExtendedHeight[0] & (this.this$0.getWorldServer().getHeight() - 1);
                k = this.chunkLocation.chunkZPos * 16 + (this.locationsOfBlockChangeForExtendedHeight[0] >> 12 & 15);
                this.sendToAllPlayersWatchingChunk(new S23PacketBlockChange(i, j, k, this.this$0.getWorldServer()));

                if (this.this$0.getWorldServer().getBlock(i, j, k).hasTileEntity(this.this$0.getWorldServer().getBlockMetadata(i, j, k))) {

                    this.sendTileToAllPlayersWatchingChunk(this.this$0.getWorldServer().getTileEntity(i, j, k));
                }
            } else {

                int l;

                if (this.numberOfTilesToUpdate >= ForgeModContainer.clumpingThreshold) {

                    i = this.chunkLocation.chunkXPos * 16;
                    j = this.chunkLocation.chunkZPos * 16;

                    this.sendToAllPlayersWatchingChunk(new S21PacketChunkData(this.this$0.getWorldServer().getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos), false, this.flagsYAreasToUpdate));

                    // Forge: Grabs ALL tile entities is costly on a modded server, only send needed ones
                    /*for (k = 0; false && k < 16; ++k) {

                        if ((this.flagsYAreasToUpdate & 1 << k) != 0) {

                            l = k << 4;
                            List list = this.this$0.getWorldServer().func_147486_a(i, l, j, i + 16, l + 16, j + 16);

                            for (int i1 = 0; i1 < list.size(); ++i1) {

                                this.sendTileToAllPlayersWatchingChunk((TileEntity)list.get(i1));
                            }
                        }
                    }*/
                } else {

                    S22PacketMultiBlockChange packet = new S22PacketMultiBlockChange();
                    ((IS22PacketExtended)packet).initExtendedS22Packet(this.numberOfTilesToUpdate, this.locationsOfBlockChangeForExtendedHeight, this.this$0.getWorldServer().getChunkFromChunkCoords(this.chunkLocation.chunkXPos, this.chunkLocation.chunkZPos));

                    this.sendToAllPlayersWatchingChunk(packet);
                }

                { //Forge: Send only the tile entities that are updated, Adding this brace lets us keep the indent and the patch small
                    WorldServer world = this.this$0.getWorldServer();

                    for (i = 0; i < this.numberOfTilesToUpdate; ++i) {

                        j = this.chunkLocation.chunkXPos * 16 + (this.locationsOfBlockChangeForExtendedHeight[i] >> 16 & 15);
                        k = this.locationsOfBlockChangeForExtendedHeight[i] & (this.this$0.getWorldServer().getHeight() - 1);
                        l = this.chunkLocation.chunkZPos * 16 + (this.locationsOfBlockChangeForExtendedHeight[i] >> 12 & 15);

                        if (world.getBlock(j, k, l).hasTileEntity(world.getBlockMetadata(j, k, l))) {

                            this.sendTileToAllPlayersWatchingChunk(this.this$0.getWorldServer().getTileEntity(j, k, l));
                        }
                    }
                }
            }

            this.numberOfTilesToUpdate = 0;
            this.flagsYAreasToUpdate = 0;
        }
    }
}
