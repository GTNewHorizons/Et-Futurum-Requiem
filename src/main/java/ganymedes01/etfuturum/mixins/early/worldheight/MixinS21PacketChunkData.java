package ganymedes01.etfuturum.mixins.early.worldheight;

import ganymedes01.etfuturum.configuration.configs.ConfigExperiments;
import ganymedes01.etfuturum.world.WorldHeightHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S21PacketChunkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

@Mixin(S21PacketChunkData.class)
public abstract class MixinS21PacketChunkData {

    @Shadow private int field_149284_a;     // x position of chunk
    @Shadow private int field_149282_b;     // z position of chunk
    @Shadow private int field_149283_c;     // section mask
    @Shadow private int field_149280_d;     // additional mask
    @Shadow private byte[] field_149281_e;  // uncompressed write chunk data
    @Shadow private byte[] field_149278_f;  // uncompressed read chunk data
    @Shadow private boolean field_149279_g; // include biome data
    @Shadow private int field_149285_h;     // chunk data bytes
    @Shadow
    @Mutable
    private static byte[] field_149286_i; // compressed chunk data
    static {
        field_149286_i = new byte[(ConfigExperiments.maxWorldHeight >> 4) * 12288 + 256]; // (BlockIDs (4096 Bytes) + Metadata (2048 Bytes) + Blocklight (2048 Bytes) + Skylight (2048 Bytes) + MSB-IDs (2048 Bytes)) per Section (20) + Biome-Data (256 Bytes)
    }
    @Shadow(remap = false) Semaphore deflateGate;

    @Shadow(remap = false) public abstract void deflate();

    @Inject(method = "func_149275_c", at = @At("HEAD"), cancellable = true)
    private static void getMaxPacketSizeForExtendedHeight(CallbackInfoReturnable<Integer> cir) {

        cir.setReturnValue(WorldHeightHandler.getChunkSections() * 12288 + 256);  // (BlockIDs (4096 Bytes) + Metadata (2048 Bytes) + Blocklight (2048 Bytes) + Skylight (2048 Bytes) + MSB-IDs (2048 Bytes)) per Section (20) + Biome-Data (256 Bytes)
    }

    /**
     * @author timb8g
     * @reason extend world height
     */
    @Overwrite
    public void readPacketData(PacketBuffer data) throws IOException {

        this.field_149284_a = data.readInt();       // x position of chunk
        this.field_149282_b = data.readInt();       // z position of chunk
        this.field_149279_g = data.readBoolean();   // include biome data
        this.field_149283_c = data.readInt();       // section mask
        this.field_149280_d = data.readInt();       // additional mask
        this.field_149285_h = data.readInt();       // chunk data length

        if (field_149286_i.length < this.field_149285_h) {

            field_149286_i = new byte[this.field_149285_h];
        }

        data.readBytes(field_149286_i, 0, this.field_149285_h);

        int sections = 0;
        int msbsections = 0; //BugFix: MC does not read the MSB array from the packet properly, causing issues for servers that use blocks > 256
        int j;

        for (j = 0; j < WorldHeightHandler.getChunkSections(); ++j) {

            sections += this.field_149283_c >> j & 1;
            msbsections += this.field_149280_d >> j & 1;
        }

        j = 10240 * sections;       // in 12288 the msb bytes already included
        j += 2048 * msbsections;

        if (this.field_149279_g) {

            j += 256;
        }

        this.field_149278_f = new byte[j];

        Inflater inflater = new Inflater();
        inflater.setInput(field_149286_i, 0, this.field_149285_h);

        try {

            inflater.inflate(this.field_149278_f);

        } catch (DataFormatException dataformatexception) {

            throw new IOException("Bad compressed data format");

        } finally {

            inflater.end();
        }
    }

    /**
     * @author timb8g
     * @reason extended world height
     */
    @Overwrite
    public void writePacketData(PacketBuffer data) throws IOException {

        if (this.field_149281_e == null) {

            deflateGate.acquireUninterruptibly();

            if (this.field_149281_e == null) {

                deflate();
            }

            deflateGate.release();
        }
        System.out.println("Write Section: " + this.field_149283_c);
        data.writeInt(this.field_149284_a);
        data.writeInt(this.field_149282_b);
        data.writeBoolean(this.field_149279_g);
        data.writeInt(this.field_149283_c);
        data.writeInt(this.field_149280_d);
        data.writeInt(this.field_149285_h);
        data.writeBytes(this.field_149281_e, 0, this.field_149285_h);
    }
}