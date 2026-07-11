package ganymedes01.etfuturum.mixins.early.swimming.client;

import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import net.minecraft.client.entity.EntityClientPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityClientPlayerMP.class)
public abstract class MixinEntityClientPlayerMP {

	@Redirect(
			method = "sendMotionUpdates",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;isSneaking()Z"))
	private boolean etfu$sendActualSneakInput(EntityClientPlayerMP player) {
		return player instanceof IPlayerSwimming
				? ((IPlayerSwimming) player).etfu$isActuallySneaking() : player.isSneaking();
	}
}
