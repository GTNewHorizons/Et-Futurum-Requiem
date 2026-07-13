package ganymedes01.etfuturum.mixins.early.swimming.client;

import ganymedes01.etfuturum.pose.IPoseablePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(C03PacketPlayer.C06PacketPlayerPosLook.class)
public class C06PacketPlayerPosLookMixin {

    @ModifyVariable(method = "<init>(DDDDFFZ)V", at = @At("HEAD"), argsOnly = true, name = "p_i45254_5_")
    private static double applyYOffset(double p_i45254_5_) {
        if (p_i45254_5_ == -999) return p_i45254_5_;
        return p_i45254_5_ + ((IPoseablePlayer) Minecraft.getMinecraft().thePlayer).etfu$getCurrentYOffset();
    }
}
