package ganymedes01.etfuturum.mixins.early.spectator.client;

import ganymedes01.etfuturum.spectator.SpectatorMode;
import ganymedes01.etfuturum.spectator.SpectatorModeClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MixinMinecraft {

	@Shadow
	public EntityClientPlayerMP thePlayer;

	@Redirect(
			method = "runTick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/player/InventoryPlayer;changeCurrentItem(I)V"))
	private void etfuturum$spectatorScrollFlySpeed(InventoryPlayer inventory, int delta) {
		if (SpectatorMode.isSpectator(this.thePlayer)) {
			SpectatorModeClient.INSTANCE.handleScrollFlySpeed(delta);
		} else {
			inventory.changeCurrentItem(delta);
		}
	}
}
