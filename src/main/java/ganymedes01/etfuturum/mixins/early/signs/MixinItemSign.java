package ganymedes01.etfuturum.mixins.early.signs;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSign;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Allow replacing replaceable blocks matching modded signs.
 * Janky implementation of ItemBlockSign logic
 * 
 * @author mosesyu1028
 */
@Mixin(ItemSign.class)
public class MixinItemSign {

	// Skip solid check if replaceable. The result is shared per call (not a field) since the
	// client and integrated server threads use the same Items.sign instance in singleplayer.
	@ModifyExpressionValue(method = "onItemUse", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/block/material/Material;isSolid()Z"))
	private boolean treatReplaceableAsSolid(boolean original, ItemStack stack, EntityPlayer player,
		World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
		@Share("blockIsReplaceable") LocalBooleanRef blockIsReplaceable) {
		if (original) {
			blockIsReplaceable.set(false);
			return true;
		}
		blockIsReplaceable.set(world.getBlock(x, y, z).isReplaceable(world, x, y, z));
		return blockIsReplaceable.get();
	}

	// Skip offset if replaceable
	@Definition(id = "side", local = @Local(type = int.class, ordinal = 3))
	@Expression("side == ?")
	@ModifyExpressionValue(method = "onItemUse", at = @At(value = "MIXINEXTRAS:EXPRESSION"),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/block/material/Material;isSolid()Z"),
			to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;canPlayerEdit(IIIILnet/minecraft/item/ItemStack;)Z")
		))
	private boolean skipOffsetReplaceable(boolean original, ItemStack stack, EntityPlayer player,
		World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
		@Share("blockIsReplaceable") LocalBooleanRef blockIsReplaceable) {
		return original && !blockIsReplaceable.get();
	}

	// If replaceable, force standing sign (side = 1)
	@ModifyVariable(method = "onItemUse", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/entity/player/EntityPlayer;canPlayerEdit(IIIILnet/minecraft/item/ItemStack;)Z"),
		ordinal = 3, argsOnly = true)
	private int forceStandingReplaceable(int currentSide, ItemStack stack, EntityPlayer player,
		World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
		@Share("blockIsReplaceable") LocalBooleanRef blockIsReplaceable) {
		return blockIsReplaceable.get() ? 1 : currentSide;
	}

	@Inject(method = "onItemUse", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;II)Z",
		shift = At.Shift.AFTER))
	private void fixSilentPlacing(ItemStack stack, EntityPlayer player, World world,
		int x, int y, int z, int side, float hitX, float hitY, float hitZ, CallbackInfoReturnable<Boolean> cir) {
		if (ConfigSounds.fixSilentPlacing) {
			Block.SoundType blockSound = Blocks.standing_sign.stepSound;
			world.playSoundEffect((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F, blockSound.func_150496_b(), (blockSound.getVolume() + 1.0F) / 2.0F, blockSound.getPitch() * 0.8F);
		}
	}
}
