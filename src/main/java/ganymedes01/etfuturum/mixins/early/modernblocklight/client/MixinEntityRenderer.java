package ganymedes01.etfuturum.mixins.early.modernblocklight.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import ganymedes01.etfuturum.client.ModernLightmap;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.potion.Potion;
import net.minecraft.world.WorldProvider;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Rebuilds block light the way modern Minecraft's lightmap shader does: modern's block factor,
 * modern's flat tint with a parabolic white mix in place of vanilla's per-channel polynomials, and
 * modern's ambient light color in place of vanilla's flat black floor.
 */
@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

	@Shadow
	float torchFlickerX;

	@Shadow
	private Minecraft mc;

	@Unique
	private boolean etfu$counterpart;

	@Unique
	private boolean etfu$modernBlockLight;

	@Unique
	private boolean etfu$modernNightVision;

	@Unique
	private float[] etfu$ambientColor;

	@Unique
	private float etfu$flickerFactor;

	@Unique
	private float etfu$blockFactor;

	@Unique
	private float etfu$ambientTerm;

	@Unique
	private float etfu$nightVisionBrightness;

	@Unique
	private float etfu$legacyGreen;

	@Unique
	private float etfu$legacyBlue;

	@Unique
	private float etfu$floorRed;

	@Unique
	private float etfu$floorGreen;

	@Unique
	private float etfu$floorBlue;

	@Inject(method = "updateLightmap", at = @At("HEAD"))
	private void etfu$resolveModernBlockLight(float p_78472_1_, CallbackInfo ci) {
		WorldClient world = this.mc.theWorld;
		if (world == null) {
			return;
		}
		WorldProvider provider = world.provider;
		boolean counterpart = ModernLightmap.hasModernCounterpart(provider);
		this.etfu$counterpart = counterpart;
		this.etfu$ambientColor = ModernLightmap.ambientColor(provider);
		this.etfu$modernBlockLight = ConfigWorld.modernBlockLightTint && counterpart;
		this.etfu$modernNightVision = ConfigWorld.modernNightVision && counterpart
				&& (provider.dimensionId != 1 || ConfigWorld.modernEndAmbientColor);

		float offset = provider.lightBrightnessTable[0];
		float scale = ModernLightmap.ambientScale(offset);
		this.etfu$flickerFactor = ModernLightmap.FLICKER_FACTOR * scale;
		this.etfu$blockFactor = ModernLightmap.BLOCK_FACTOR * scale;
		this.etfu$ambientTerm = offset
				* (this.torchFlickerX * ModernLightmap.FLICKER_FACTOR + ModernLightmap.BLOCK_FACTOR) * scale;
		this.etfu$legacyGreen = etfu$vanillaGreen(this.etfu$ambientTerm);
		this.etfu$legacyBlue = etfu$vanillaBlue(this.etfu$ambientTerm);

		this.etfu$nightVisionBrightness = ConfigWorld.modernNightVision
				? ModernLightmap.nightVisionBrightness(this.mc.thePlayer, p_78472_1_)
				: 0.0F;
	}

	@Inject(method = "updateLightmap", at = @At(value = "CONSTANT", args = "floatValue=0.96F", ordinal = 0))
	private void etfu$resolveAmbientFloors(float p_78472_1_, CallbackInfo ci,
										   @Local(name = "i") int i,
										   @Local(name = "f2") float f2,
										   @Local(name = "f5") float f5) {
		if (i != 0) {
			return;
		}
		float nightVision = this.etfu$nightVisionBrightness;
		this.etfu$floorRed = etfu$ambientFloor(0, nightVision, f5 + this.etfu$ambientTerm);
		this.etfu$floorGreen = etfu$ambientFloor(1, nightVision, f5);
		this.etfu$floorBlue = etfu$ambientFloor(2, nightVision, f2);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.1F))
	private float etfu$modernFlickerFactor(float original) {
		return this.etfu$modernBlockLight ? this.etfu$flickerFactor : original;
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 1.5F))
	private float etfu$modernBlockFactor(float original) {
		return this.etfu$modernBlockLight ? this.etfu$blockFactor : original;
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.03F, ordinal = 0))
	private float etfu$ambientFloorRed(float original) {
		return this.etfu$floorRed;
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.03F, ordinal = 1))
	private float etfu$ambientFloorGreen(float original) {
		return this.etfu$floorGreen;
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.03F, ordinal = 2))
	private float etfu$ambientFloorBlue(float original) {
		return this.etfu$floorBlue;
	}

	@Redirect(method = "updateLightmap", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;isPotionActive(Lnet/minecraft/potion/Potion;)Z"))
	private boolean etfu$skipVanillaNightVision(EntityClientPlayerMP player, Potion potion) {
		return !this.etfu$modernNightVision && player.isPotionActive(potion);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.96F, ordinal = 0))
	private float etfu$dropAmbientScaleRed(float original) {
		return etfu$dropBiasScale(original);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.96F, ordinal = 1))
	private float etfu$dropAmbientScaleGreen(float original) {
		return etfu$dropBiasScale(original);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.96F, ordinal = 2))
	private float etfu$dropAmbientScaleBlue(float original) {
		return etfu$dropBiasScale(original);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.96F, ordinal = 3))
	private float etfu$dropTrailingScaleRed(float original) {
		return etfu$dropBiasScale(original);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.96F, ordinal = 4))
	private float etfu$dropTrailingScaleGreen(float original) {
		return etfu$dropBiasScale(original);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.96F, ordinal = 5))
	private float etfu$dropTrailingScaleBlue(float original) {
		return etfu$dropBiasScale(original);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.03F, ordinal = 3))
	private float etfu$dropTrailingOffsetRed(float original) {
		return etfu$dropTrailingOffset(original);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.03F, ordinal = 4))
	private float etfu$dropTrailingOffsetGreen(float original) {
		return etfu$dropTrailingOffset(original);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.03F, ordinal = 5))
	private float etfu$dropTrailingOffsetBlue(float original) {
		return etfu$dropTrailingOffset(original);
	}

	@Inject(method = "updateLightmap", at = @At(value = "CONSTANT", args = "floatValue=0.96F", ordinal = 0))
	private void etfu$modernBlockLightTint(float p_78472_1_, CallbackInfo ci,
										   @Local(name = "f2") float skyTerm,
										   @Local(name = "f3") float blockTerm,
										   @Local(name = "f5") float sunTerm,
										   @Local(name = "i") int index,
										   @Local(name = "f6") LocalFloatRef blockGreen,
										   @Local(name = "f7") LocalFloatRef blockBlue,
										   @Local(name = "f9") LocalFloatRef green,
										   @Local(name = "f10") LocalFloatRef blue) {
		if (!this.etfu$modernBlockLight) {
			return;
		}

		int level = index % 16;
		float levelTerm = blockTerm - this.etfu$ambientTerm;
		float g = levelTerm * ModernLightmap.BLOCK_TINT_CURVE[1][level];
		float b = levelTerm * ModernLightmap.BLOCK_TINT_CURVE[2][level];

		if (this.etfu$ambientColor == null) {
			g += this.etfu$legacyGreen;
			b += this.etfu$legacyBlue;
		}

		blockGreen.set(g);
		blockBlue.set(b);
		green.set(sunTerm + g);
		blue.set(skyTerm + b);
	}

	@Unique
	private float etfu$dropBiasScale(float original) {
		return this.etfu$modernBlockLight ? 1.0F : original;
	}

	@Unique
	private float etfu$dropTrailingOffset(float original) {
		return this.etfu$modernBlockLight ? 0.0F : original;
	}

	@Final
    @Shadow
	private int[] lightmapColors;

	@Inject(method = "updateLightmap", at = @At(value = "FIELD",
			opcode = Opcodes.GETFIELD,
			target = "Lnet/minecraft/client/renderer/EntityRenderer;lightmapTexture:Lnet/minecraft/client/renderer/texture/DynamicTexture;"))
	private void etfu$applyNightVisionBoostLast(float p_78472_1_, CallbackInfo ci) {
		if (!this.etfu$modernNightVision || this.etfu$nightVisionBrightness <= 0.0F) {
			return;
		}
		float nvRed = ModernLightmap.NIGHT_VISION[0] * this.etfu$nightVisionBrightness;
		float nvGreen = ModernLightmap.NIGHT_VISION[1] * this.etfu$nightVisionBrightness;
		float nvBlue = ModernLightmap.NIGHT_VISION[2] * this.etfu$nightVisionBrightness;

		for (int i = 0; i < this.lightmapColors.length; i++) {
			int color = this.lightmapColors[i];
			int r = (color >> 16) & 0xFF;
			int g = (color >> 8) & 0xFF;
			int b = color & 0xFF;

			r = Math.min(0xFF, (int) Math.max(r, nvRed * 255.0F));
			g = Math.min(0xFF, (int) Math.max(g, nvGreen * 255.0F));
			b = Math.min(0xFF, (int) Math.max(b, nvBlue * 255.0F));

			this.lightmapColors[i] = (color & 0xFF000000) | (r << 16) | (g << 8) | b;
		}
	}

	@Unique
	private float etfu$ambientFloor(int channel, float nightVisionBrightness, float leak) {
		if (!this.etfu$counterpart) {
			return 0.03F;
		}
		float nightVision = this.etfu$modernNightVision
				? ModernLightmap.NIGHT_VISION[channel] * nightVisionBrightness
				: 0.0F;
		if (ConfigWorld.modernBlockLightTint) {
			return this.etfu$ambientColor == null
					? 0.03F
					: Math.max(this.etfu$ambientColor[channel], nightVision) - leak;
		}
		return Math.max(0.03F, (nightVision - 0.03F) / ModernLightmap.BIAS);
	}

	@Unique
	private static float etfu$vanillaGreen(float term) {
		return term * ((term * 0.6F + 0.4F) * 0.6F + 0.4F);
	}

	@Unique
	private static float etfu$vanillaBlue(float term) {
		return term * (term * term * 0.6F + 0.4F);
	}
}
