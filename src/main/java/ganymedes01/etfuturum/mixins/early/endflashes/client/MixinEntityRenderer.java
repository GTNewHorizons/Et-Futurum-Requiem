package ganymedes01.etfuturum.mixins.early.endflashes.client;

import ganymedes01.etfuturum.client.ModernLightmap;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.core.handlers.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Brightens The End's lightmap while a flash is active and/or moves The End's ambient floor onto
 * the one modern Minecraft uses.
 */
@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
	@Unique
	private static final float FLASH_R = 172.0F / 255.0F;
	@Unique
	private static final float FLASH_G = 96.0F / 255.0F;
	@Unique
	private static final float FLASH_B = 205.0F / 255.0F;

	@Unique
	private static final float MODERN_BLOCK_SCALE = 1.0F;
	@Unique
	private static final float LEGACY_BLOCK_SCALE = 1.4F / (1.5F * 0.96F);

	@Unique
	private static final float RESTORED_BLOCK_SCALE = 1.5F * 0.96F / 1.4F;

	@Shadow
	private Minecraft mc;

	@Shadow
	private float bossColorModifier;

	@Shadow
	private float bossColorModifierPrev;

	@Shadow
	private float getNightVisionBrightness(EntityPlayer p_82830_1_, float p_82830_2_) {
		return 0.0F;
	}

	@Unique
	private float etfu$flashRed;

	@Unique
	private float etfu$flashGreen;

	@Unique
	private float etfu$flashBlue;

	@Unique
	private float etfu$darkeningRed;

	@Unique
	private float etfu$darkeningGreenBlue;

	@Unique
	private float etfu$floorRed;

	@Unique
	private float etfu$floorGreen;

	@Unique
	private float etfu$floorBlue;

	@Inject(method = "updateLightmap", at = @At("HEAD"))
	private void etfu$resolveEndLightmap(float p_78472_1_, CallbackInfo ci) {
		WorldClient world = this.mc.theWorld;
		if (world == null || world.provider.dimensionId != 1) {
			return;
		}

		float intensity = ClientEventHandler.getEndFlashIntensity(p_78472_1_);
		if (this.bossColorModifier > 0.0F) {
			intensity /= 3.0F;
			float blend = this.bossColorModifierPrev
					+ (this.bossColorModifier - this.bossColorModifierPrev) * p_78472_1_;
			this.etfu$darkeningRed = 1.0F - (1.0F - 0.7F) * blend;
			this.etfu$darkeningGreenBlue = 1.0F - (1.0F - 0.6F) * blend;
		} else {
			this.etfu$darkeningRed = 1.0F;
			this.etfu$darkeningGreenBlue = 1.0F;
		}
		this.etfu$flashRed = intensity * FLASH_R;
		this.etfu$flashGreen = intensity * FLASH_G;
		this.etfu$flashBlue = intensity * FLASH_B;

		float nightVision = ConfigWorld.modernNightVision
				&& ModernLightmap.hasNightVision(this.mc.thePlayer)
						? this.getNightVisionBrightness(this.mc.thePlayer, p_78472_1_)
						: 0.0F;
		this.etfu$floorRed = ModernLightmap.floor(ModernLightmap.endAmbient(0), 0, nightVision);
		this.etfu$floorGreen = ModernLightmap.floor(ModernLightmap.endAmbient(1), 1, nightVision);
		this.etfu$floorBlue = ModernLightmap.floor(ModernLightmap.endAmbient(2), 2, nightVision);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.22F))
	private float etfu$endFloorRed(float original) {
		return etfu$endFloor(original, this.etfu$floorRed, this.etfu$flashRed, this.etfu$darkeningRed);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.28F))
	private float etfu$endFloorGreen(float original) {
		return etfu$endFloor(original, this.etfu$floorGreen, this.etfu$flashGreen, this.etfu$darkeningGreenBlue);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.25F))
	private float etfu$endFloorBlue(float original) {
		return etfu$endFloor(original, this.etfu$floorBlue, this.etfu$flashBlue, this.etfu$darkeningGreenBlue);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.75F, ordinal = 0))
	private float etfu$endBlockScaleRed(float original) {
		return etfu$endBlockScale(original, this.etfu$darkeningRed);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.75F, ordinal = 1))
	private float etfu$endBlockScaleGreen(float original) {
		return etfu$endBlockScale(original, this.etfu$darkeningGreenBlue);
	}

	@ModifyConstant(method = "updateLightmap", constant = @Constant(floatValue = 0.75F, ordinal = 2))
	private float etfu$endBlockScaleBlue(float original) {
		return etfu$endBlockScale(original, this.etfu$darkeningGreenBlue);
	}

	@Unique
	private float etfu$endFloor(float vanilla, float modern, float flash, float darkening) {
		if (!ConfigWorld.modernEndAmbientColor) {
			float floor = vanilla + flash;
			return ConfigWorld.modernBlockLightTint ? floor * ModernLightmap.BIAS + 0.03F : floor;
		}
		float target = (modern + flash) * darkening;
		return ConfigWorld.modernBlockLightTint ? target : (target - 0.03F) / ModernLightmap.BIAS;
	}

	@Unique
	private float etfu$endBlockScale(float vanilla, float darkening) {
		if (!ConfigWorld.modernEndAmbientColor) {
			return ConfigWorld.modernBlockLightTint ? vanilla * RESTORED_BLOCK_SCALE : vanilla;
		}
		return (ConfigWorld.modernBlockLightTint ? MODERN_BLOCK_SCALE : LEGACY_BLOCK_SCALE) * darkening;
	}
}
