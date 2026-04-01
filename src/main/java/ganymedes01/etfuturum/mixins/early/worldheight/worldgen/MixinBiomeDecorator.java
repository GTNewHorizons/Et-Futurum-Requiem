package ganymedes01.etfuturum.mixins.early.worldheight.worldgen;

import ganymedes01.etfuturum.world.WorldHeightHandler;
import net.minecraft.world.biome.BiomeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BiomeDecorator.class)
public class MixinBiomeDecorator {

    // Ore gen height needs some adjustment
    @ModifyConstant(method = "generateOres", constant = { @Constant(intValue = 0), @Constant(intValue = 256), @Constant(intValue = 128), @Constant(intValue = 64), @Constant(intValue = 32), @Constant(intValue = 16) })
    private int increaseByWorldHeightOffset(int original) {

        return original + WorldHeightHandler.getWorldHeightOffset();
    }
}
