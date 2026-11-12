package ganymedes01.etfuturum.mixins.early.swimming.client;

import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP {

	@Redirect(
			method = "onPlayerRightClick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;isSneaking()Z"))
	private boolean etfu$useActualSneakInputForInteraction(EntityPlayer player) {
		return player instanceof IPlayerSwimming
				? ((IPlayerSwimming) player).etfu$isActuallySneaking() : player.isSneaking();
	}
}
