package ganymedes01.etfuturum.mixins.early.backlytra.client;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ganymedes01.etfuturum.elytra.IElytraPlayer;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {

    @Inject(
        method = "rotateCorpse(Lnet/minecraft/client/entity/AbstractClientPlayer;FFF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/RendererLivingEntity;rotateCorpse(Lnet/minecraft/entity/EntityLivingBase;FFF)V",
            shift = At.Shift.AFTER))
    private void rotateForElytra(AbstractClientPlayer player, float p_77043_2_, float p_77043_3_, float partialTicks,
        CallbackInfo ci) {
        if (player instanceof IElytraPlayer) {
            if (((IElytraPlayer) player).etfu$isElytraFlying()) {
                float f = ((IElytraPlayer) player).etfu$getTicksElytraFlying() + partialTicks;
                float f1 = MathHelper.clamp_float(f * f / 100.0F, 0.0F, 1.0F);
                GL11.glRotatef(f1 * (-90.0F - player.rotationPitch), 1.0F, 0.0F, 0.0F);
                Vec3 vec3d = player.getLook(partialTicks);
                double d0 = player.motionX * player.motionX + player.motionZ * player.motionZ;
                double d1 = vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord;

                if (d0 > 0.0D && d1 > 0.0D) {
                    double d2 = (player.motionX * vec3d.xCoord + player.motionZ * vec3d.zCoord)
                        / (Math.sqrt(d0) * Math.sqrt(d1));
                    double d3 = player.motionX * vec3d.zCoord - player.motionZ * vec3d.xCoord;
                    GL11.glRotatef(
                        (float) (Math.signum(d3) * Math.acos(Math.min(d2, 1))) * 180.0F / (float) Math.PI,
                        0.0F,
                        1.0F,
                        0.0F);
                }
            }
        }
    }
}
