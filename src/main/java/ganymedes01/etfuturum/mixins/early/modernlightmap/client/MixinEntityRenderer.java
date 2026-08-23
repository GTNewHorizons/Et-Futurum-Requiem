package ganymedes01.etfuturum.mixins.early.modernlightmap.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import ganymedes01.etfuturum.client.ModernLightmap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces the per-channel brightness-slider curve with one from modern Minecraft.
 */
@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

	@Shadow
	private Minecraft mc;

	@Unique
	private boolean etfu$hasModernCounterpart;

	@Unique
	private static float etfu$clamp(float value) {
		return value < 0.0F ? 0.0F : Math.min(value, 1.0F);
	}

	@Inject(method = "updateLightmap", at = @At("HEAD"))
	private void etfu$resolveModernGamma(float p_78472_1_, CallbackInfo ci) {
		WorldClient world = this.mc.theWorld;
		this.etfu$hasModernCounterpart = world != null && ModernLightmap.hasModernCounterpart(world.provider);
	}

	@ModifyExpressionValue(method = "updateLightmap", at = @At(value = "FIELD",
			target = "Lnet/minecraft/client/settings/GameSettings;gammaSetting:F",
			opcode = Opcodes.GETFIELD))
	private float etfu$modernGamma(float gamma,
								   @Local(name = "f8") LocalFloatRef red,
								   @Local(name = "f9") LocalFloatRef green,
								   @Local(name = "f10") LocalFloatRef blue) {
		if (!this.etfu$hasModernCounterpart) {
			return gamma;
		}
		if (gamma <= 0.0F) {
			return 0.0F;
		}
		float r = etfu$clamp(red.get());
		float g = etfu$clamp(green.get());
		float b = etfu$clamp(blue.get());

		float max = Math.max(r, Math.max(g, b));
		if (max > 0.0F) {
			float inverted = 1.0F - max;
			float scaled = (float) (1.0F - (inverted*inverted*inverted*inverted));
			float mix = 1.0F + gamma * (scaled / max - 1.0F);
			r *= mix;
			g *= mix;
			b *= mix;
		}

		red.set(r);
		green.set(g);
		blue.set(b);
		return 0.0F;
	}
}
