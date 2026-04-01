package ganymedes01.etfuturum.mixins.early.worldheight;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(C07PacketPlayerDigging.class)
public class MixinC07PacketPlayerDigging {

    @Redirect(method = "readPacketData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;readUnsignedByte()S", ordinal = 1))
    private short readPacketDataForExtendedHeight(PacketBuffer data) {

        return data.readShort();
    }

    @Redirect(method = "writePacketData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;writeByte(I)Lio/netty/buffer/ByteBuf;", ordinal = 1))
    private ByteBuf writePacketDataForExtendedHeight(PacketBuffer data, int value) {

        return data.writeShort(value);
    }
}
