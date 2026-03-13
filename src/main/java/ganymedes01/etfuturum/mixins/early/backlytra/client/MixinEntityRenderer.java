package ganymedes01.etfuturum.mixins.early.backlytra.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ganymedes01.etfuturum.elytra.IElytraPlayer;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Shadow
    private Minecraft mc;

    @Inject(method = "orientCamera", at = @At("TAIL"))
    private void adjustThirdPersonForElytra(float partialTicks, CallbackInfo ci) {
        if (this.mc.gameSettings.thirdPersonView > 0 && this.mc.renderViewEntity instanceof IElytraPlayer) {
            if (((IElytraPlayer) this.mc.renderViewEntity).etfu$isElytraFlying()) {
                /*
                 * Move the camera down 1.62 blocks to sit at the player's feet and then up by 0.4 blocks, like 1.12
                 * does
                 */
                GL11.glTranslatef(0, 1.22f, 0f);
            }
        }
    }
}
