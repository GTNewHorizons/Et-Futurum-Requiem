package ganymedes01.etfuturum.mixins.early.swimming.client;

import ganymedes01.etfuturum.elytra.IElytraPlayer;
import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public abstract class MixinRenderPlayer extends RendererLivingEntity {

	protected MixinRenderPlayer(ModelBase model, float shadowSize) {
		super(model, shadowSize);
	}

	@Inject(method = "rotateCorpse(Lnet/minecraft/client/entity/AbstractClientPlayer;FFF)V", at = @At("TAIL"))
	private void etfu$rotateSwimmingPlayer(AbstractClientPlayer player, float ageInTicks, float rotationYaw,
			float partialTicks, CallbackInfo ci) {
		if (!(player instanceof IPlayerSwimming)
				|| player instanceof IElytraPlayer && ((IElytraPlayer) player).etfu$isElytraFlying()) {
			return;
		}

		IPlayerSwimming swimming = (IPlayerSwimming) player;
		float animation = swimming.etfu$getSwimAnimation(partialTicks);
		float targetRotation = player.isInWater() ? -90.0F - player.rotationPitch : -90.0F;
		GL11.glRotatef(animation * targetRotation, 1.0F, 0.0F, 0.0F);
		if (swimming.etfu$isActuallySwimming()) {
			GL11.glTranslatef(0.0F, -1.0F, 0.3F);
		}
	}
}
