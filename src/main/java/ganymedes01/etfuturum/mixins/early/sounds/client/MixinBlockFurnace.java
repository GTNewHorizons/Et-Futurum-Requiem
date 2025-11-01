package ganymedes01.etfuturum.mixins.early.sounds.client;

import ganymedes01.etfuturum.Tags;
import net.minecraft.block.BlockFurnace;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = BlockFurnace.class)
public class MixinBlockFurnace {

	@Inject(method = "randomDisplayTick(Lnet/minecraft/world/World;IIILjava/util/Random;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockMetadata(III)I", shift = At.Shift.BEFORE))
	private void randomDisplayTickMixin(World worldIn, int x, int y, int z, Random random, CallbackInfo ci) {
		if (random.nextDouble() < 0.1D) {
			worldIn.playSound(x + .5D, y + .5D, z + .5D, Tags.MC_ASSET_VER + ":block.furnace.fire_crackle", 1, 1, false);
		}
	}
}
