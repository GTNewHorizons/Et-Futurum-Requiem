package ganymedes01.etfuturum.mixins.early.worldheight;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S23PacketBlockChange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(S23PacketBlockChange.class)
public class MixinS23PacketBlockChange {

    @Redirect(method = "readPacketData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;readUnsignedByte()S", ordinal = 0))
    private short readPacketDataForExtendedHeight(PacketBuffer data) {

        return data.readShort();
    }

    @Redirect(method = "writePacketData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;writeByte(I)Lio/netty/buffer/ByteBuf;", ordinal = 0))
    private ByteBuf writePacketDataForExtendedHeight(PacketBuffer data, int value) {

        return data.writeShort(value);
    }
}
