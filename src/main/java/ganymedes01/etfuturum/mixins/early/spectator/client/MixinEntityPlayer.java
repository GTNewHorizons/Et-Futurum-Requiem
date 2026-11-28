package ganymedes01.etfuturum.mixins.early.spectator.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import ganymedes01.etfuturum.spectator.SpectatorMode;
import ganymedes01.etfuturum.spectator.SpectatorModeClient;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer {
	@ModifyExpressionValue(method = "moveEntityWithHeading", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/player/PlayerCapabilities;getFlySpeed()F"))
	private float etfuturum$useSpectatorFlySpeed(float speed) {
		return SpectatorModeClient.INSTANCE.getSpectatorFlySpeed((EntityPlayer) (Object) this, speed);
	}

	@Inject(method = "isInvisibleToPlayer", at = @At("HEAD"), cancellable = true)
	public void isInvisibleToPlayer(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
		if (SpectatorMode.isSpectator(player)) {
			cir.setReturnValue(false); //TODO: Make it so spectators can see each other
		}
	}
}
