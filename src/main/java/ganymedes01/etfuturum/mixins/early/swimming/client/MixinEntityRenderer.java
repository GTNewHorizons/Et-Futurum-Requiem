package ganymedes01.etfuturum.mixins.early.swimming.client;

import ganymedes01.etfuturum.pose.IPoseablePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
	@Shadow
	private Minecraft mc;

	@ModifyVariable(method = "orientCamera", at = @At(value = "STORE", ordinal = 0), name = "f1")
	private float applyOffset(float origin) {
		if (this.mc.renderViewEntity instanceof EntityPlayer player) {
			if (!player.isPlayerSleeping() && !player.isRiding()) {
				return origin + ((IPoseablePlayer) player).etfu$getCurrentYOffset();
			}
		}
		return origin;
	}
}
