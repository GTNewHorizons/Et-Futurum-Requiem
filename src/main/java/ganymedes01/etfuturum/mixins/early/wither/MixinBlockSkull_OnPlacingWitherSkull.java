package ganymedes01.etfuturum.mixins.early.wither;

import static ganymedes01.etfuturum.wither.WitherSpawnHelper.checkWitherPattern;

import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.world.World;

import net.minecraft.block.BlockSkull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockSkull.class, remap = false)
public abstract class MixinBlockSkull_OnPlacingWitherSkull {
    @Inject(method = "func_149965_a", at = @At(value = "HEAD"), cancellable = true, remap = true)
    private void etfuturum$checkWitherPattern(World w, int x, int y, int z, TileEntitySkull skull, CallbackInfo ci) {
        if (!checkWitherPattern(w, x, y, z, skull)) return;
        ci.cancel();
    }
}
