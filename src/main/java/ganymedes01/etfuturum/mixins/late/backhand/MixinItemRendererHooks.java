package ganymedes01.etfuturum.mixins.late.backhand;

import ganymedes01.etfuturum.client.renderer.item.ItemLanternRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xonin.backhand.client.hooks.ItemRendererHooks;

/**
 * Marks the first person offhand render so EFR's first person lantern mixin knows the arm being
 * drawn is the offhand (Backhand mirrors the frame), instead of hijacking it as the main hand. Late
 * mixin: only applied when Backhand is present.
 */
@Mixin(ItemRendererHooks.class)
public class MixinItemRendererHooks {

	@Inject(method = "renderOffhandReturn", at = @At("HEAD"), remap = false)
	private static void etfu$markOffhandStart(float frame, CallbackInfo ci) {
		ItemLanternRenderer.renderingOffhand = true;
	}

	@Inject(method = "renderOffhandReturn", at = @At("RETURN"), remap = false)
	private static void etfu$markOffhandEnd(float frame, CallbackInfo ci) {
		ItemLanternRenderer.renderingOffhand = false;
	}
}
