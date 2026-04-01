package ganymedes01.etfuturum.mixins.early.worldheight;

import ganymedes01.etfuturum.world.WorldHeightHandler;
import ganymedes01.etfuturum.world.generate.ChunkProviderIncreasedHeight;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.WorldType.FLAT;

@Mixin(WorldType.class)
public class MixinWorldType {

    @ModifyConstant(method = "getCloudHeight", constant = @Constant(floatValue = 128.0F), remap = false)
    private float getIncreasedCloudHeight(float original) {

        return WorldHeightHandler.getWorldHeightOffset() != 0 ? original + WorldHeightHandler.getWorldHeightOffset() : original;
    }

    @Inject(method = "getChunkGenerator", at = @At("RETURN"), cancellable = true, remap = false)
    private void getIncreasedHeightChunkGenerator(World world, String generatorOptions, CallbackInfoReturnable<IChunkProvider> cir) {

        cir.setReturnValue(((WorldType)(Object)this) == FLAT ? new ChunkProviderFlat(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), generatorOptions) : new ChunkProviderIncreasedHeight(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled()));
    }
}
