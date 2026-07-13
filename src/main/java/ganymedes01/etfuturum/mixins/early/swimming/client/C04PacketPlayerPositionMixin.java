package ganymedes01.etfuturum.mixins.early.swimming.client;

import ganymedes01.etfuturum.pose.IPoseablePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(C03PacketPlayer.C04PacketPlayerPosition.class)
public class C04PacketPlayerPositionMixin {

    @ModifyVariable(method = "<init>(DDDDZ)V", at = @At("HEAD"), argsOnly = true, name = "p_i45253_5_")
    private static double applyYOffset(double origin) {
        if (origin == -999) return origin;
        return origin + ((IPoseablePlayer) Minecraft.getMinecraft().thePlayer).etfu$getCurrentYOffset();
    }
}
