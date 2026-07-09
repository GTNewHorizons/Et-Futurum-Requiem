package ganymedes01.etfuturum.mixins.late.backhand;

import ganymedes01.etfuturum.client.renderer.item.ItemLanternRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xonin.backhand.client.utils.BackhandRenderHelper;

/**
 * Marks the third person offhand render so EFR's lantern renderer knows to place the lantern for
 * the left hand instead of assuming the posed main-hand arm. Late mixin: only applied when Backhand
 * is present.
 */
@Mixin(BackhandRenderHelper.class)
public class MixinBackhandRenderHelper {

	@Inject(method = "renderOffhandItemIn3rdPerson", at = @At("HEAD"), remap = false)
	private static void etfu$markOffhandStart(EntityPlayer player, ModelBiped modelBipedMain, float frame, CallbackInfo ci) {
		ItemLanternRenderer.renderingOffhand = true;
	}

	@Inject(method = "renderOffhandItemIn3rdPerson", at = @At("RETURN"), remap = false)
	private static void etfu$markOffhandEnd(EntityPlayer player, ModelBiped modelBipedMain, float frame, CallbackInfo ci) {
		ItemLanternRenderer.renderingOffhand = false;
	}
}
