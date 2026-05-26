package ganymedes01.etfuturum.mixins.early.spectator.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import ganymedes01.etfuturum.api.spectator.ISpectatorInfo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer implements ISpectatorInfo {
	@Inject(method = "getBreakSpeed(Lnet/minecraft/block/Block;ZIIII)F", remap = false, at = @At(value = "HEAD"))
	private void cancelBreakSpeed(Block p_146096_1_, boolean p_146096_2_, int meta, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
		if(etfu$isSpectator()) {
			FMLClientHandler.instance().getClient().playerController.stepSoundTickCounter = -5;
			FMLClientHandler.instance().getClient().playerController.isHittingBlock = false;
		}
	}


	@Inject(method = "attackTargetEntityWithCurrentItem", at = @At(value = "HEAD"))
	private void setFollowing(Entity targetEntity, CallbackInfo ci) {
		if (etfu$isSpectator()) {
			if(etfu$spectatingEntity() == null) {
				if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
					Minecraft.getMinecraft().ingameGUI.func_110326_a/*setRecordPlaying*/
							(I18n.format("mount.onboard", GameSettings.getKeyDisplayString(
									Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())), false);
				}
			}
		}
	}

	@ModifyReturnValue(method = "isInvisibleToPlayer", at = @At("RETURN"))
	private boolean invisibleToSpectators(boolean original, @Local(argsOnly = true) EntityPlayer player) {
		return original || !(etfu$isSpectator() && player instanceof ISpectatorInfo info && info.etfu$isSpectator());
	}
}
