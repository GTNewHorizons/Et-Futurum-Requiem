package ganymedes01.etfuturum.mixins.early.sounds;

import ganymedes01.etfuturum.Tags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnderEye;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemEnderEye.class)
public class MixinItemEnderEye extends Item {

	@Redirect(method = "onItemRightClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSoundAtEntity(Lnet/minecraft/entity/Entity;Ljava/lang/String;FF)V"))
	private void replaceThrowSound(World world, Entity entity, String string, float volume, float pitch) {
		world.playSoundAtEntity(entity, Tags.MC_ASSET_VER + ":entity.ender_eye.launch", volume, pitch);
	}

	//Because there's actually two different throw sounds, I'll just remove one. This redirect cancels the second sound.
	@Redirect(method = "onItemRightClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playAuxSFXAtEntity(Lnet/minecraft/entity/player/EntityPlayer;IIIII)V"))
	private void removeOtherThrowSound(World world, EntityPlayer player, int int1, int int2, int int3, int int4, int int5) {
	}
}
