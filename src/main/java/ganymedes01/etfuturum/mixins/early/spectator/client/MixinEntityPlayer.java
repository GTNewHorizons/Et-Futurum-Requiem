package ganymedes01.etfuturum.mixins.early.spectator.client;

import cpw.mods.fml.client.FMLClientHandler;
import ganymedes01.etfuturum.api.spectator.ISpectatorInfo;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer {
	@Inject(method = "getBreakSpeed(Lnet/minecraft/block/Block;ZIIII)F", remap = false, at = @At(value = "HEAD"))
	private void cancelBreakSpeed(Block p_146096_1_, boolean p_146096_2_, int meta, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
		if(this instanceof ISpectatorInfo info && info.etfu$isSpectator()) {
			FMLClientHandler.instance().getClient().playerController.stepSoundTickCounter = -5;
			FMLClientHandler.instance().getClient().playerController.isHittingBlock = false;
		}
	}
}
