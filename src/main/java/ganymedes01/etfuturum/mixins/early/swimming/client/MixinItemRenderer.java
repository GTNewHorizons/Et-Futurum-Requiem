package ganymedes01.etfuturum.mixins.early.swimming.client;

import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import ganymedes01.etfuturum.swimming.PlayerPose;
import ganymedes01.etfuturum.swimming.SwimmingHooks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

	@Shadow
	private Minecraft mc;

	@Redirect(
			method = "renderOverlays",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;isInsideOfMaterial(Lnet/minecraft/block/material/Material;)Z"))
	private boolean etfu$sampleWaterAtRenderedCamera(EntityClientPlayerMP player, Material material, float partialTicks) {
		if (SwimmingHooks.isEnabled() && player instanceof IPlayerSwimming) {
			PlayerPose pose = ((IPlayerSwimming) player).etfu$getPose();
			if (pose.usesModernEyeHeight()) {
				return ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, player, partialTicks)
						.getMaterial() == material;
			}
		}
		return player.isInsideOfMaterial(material);
	}
}
