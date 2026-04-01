package ganymedes01.etfuturum.mixins.early.worldheight;

import ganymedes01.etfuturum.world.WorldHeightHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

@Mixin(S26PacketMapChunkBulk.class)
public abstract class MixinS26PacketMapChunkBulk {

    @Shadow private int[] field_149266_a;           // x position of chunks
    @Shadow private int[] field_149264_b;           // z position of chunks
    @Shadow private int[] field_149265_c;           // sections masks
    @Shadow private int[] field_149262_d;           // additional masks
    @Shadow private byte[] field_149263_e;          // uncompressed write chunk data for all chunks
    @Shadow private byte[][] field_149260_f;        // uncompressed read chunk data per chunk
    @Shadow private int field_149261_g;             // compressed chunk data bytes for all chunks
    @Shadow private boolean field_149267_h;         // world has sky
    @Shadow private static byte[] field_149268_i;   // compressed chunk data for all chunks
    @Shadow(remap = false) private Semaphore deflateGate;

    @Shadow(remap = false) public abstract void deflate();

    @ModifyArg(method = "<init>(Ljava/util/List;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/server/S21PacketChunkData;func_149269_a(Lnet/minecraft/world/chunk/Chunk;ZI)Lnet/minecraft/network/play/server/S21PacketChunkData$Extracted;"), index = 2)
    private int extendedSectionMaskForS21Packet(int original) {

        int sections = WorldHeightHandler.getChunkSections();

        if(sections >= 32) return -1;

        return (1 << sections) - 1;
    }

    /**
     * @author timb8g
     * @reason extended world height
     */
    @Overwrite
    public void readPacketData(PacketBuffer data) throws IOException {

        short chunkCount = data.readShort();

        this.field_149261_g = data.readInt();           // compressed chunk data bytes for all chunks
        this.field_149267_h = data.readBoolean();       // world has sky
        this.field_149266_a = new int[chunkCount];      // x position of chunks
        this.field_149264_b = new int[chunkCount];      // z position of chunks
        this.field_149265_c = new int[chunkCount];      // sections masks
        this.field_149262_d = new int[chunkCount];      // additional masks
        this.field_149260_f = new byte[chunkCount][];   // chunk data per chunk

        if (field_149268_i.length < this.field_149261_g) {

            field_149268_i = new byte[this.field_149261_g];
        }

        data.readBytes(field_149268_i, 0, this.field_149261_g);

        byte[] abyte = new byte[S21PacketChunkData.func_149275_c() * chunkCount];

        Inflater inflater = new Inflater();
        inflater.setInput(field_149268_i, 0, this.field_149261_g);

        try {

            inflater.inflate(abyte);

        } catch (DataFormatException dataformatexception) {

            throw new IOException("Bad compressed data format");

        } finally {

            inflater.end();
        }

        int i = 0;

        for (int j = 0; j < chunkCount; ++j) {

            this.field_149266_a[j] = data.readInt();
            this.field_149264_b[j] = data.readInt();
            this.field_149265_c[j] = data.readInt();
            this.field_149262_d[j] = data.readInt();
            int k = 0;
            int l = 0;
            int i1;

            for (i1 = 0; i1 < WorldHeightHandler.getChunkSections(); ++i1) {

                k += this.field_149265_c[j] >> i1 & 1;
                l += this.field_149262_d[j] >> i1 & 1;
            }

            i1 = 2048 * 4 * k + 256;
            i1 += 2048 * l;

            if (this.field_149267_h) {

                i1 += 2048 * k;
            }

            this.field_149260_f[j] = new byte[i1];
            System.arraycopy(abyte, i, this.field_149260_f[j], 0, i1);
            i += i1;
        }
    }

    /**
     * @author timb8g
     * @reason extended world height
     */
    @Overwrite
    public void writePacketData(PacketBuffer data) throws IOException {

        if (this.field_149263_e == null) {

            deflateGate.acquireUninterruptibly();

            if (this.field_149263_e == null) {

                deflate();
            }

            deflateGate.release();
        }

        data.writeShort(this.field_149266_a.length);
        data.writeInt(this.field_149261_g);
        data.writeBoolean(this.field_149267_h);
        data.writeBytes(this.field_149263_e, 0, this.field_149261_g);

        for (int i = 0; i < this.field_149266_a.length; ++i) {

            data.writeInt(this.field_149266_a[i]);
            data.writeInt(this.field_149264_b[i]);
            data.writeInt(this.field_149265_c[i]);
            data.writeInt(this.field_149262_d[i]);
        }
    }
}