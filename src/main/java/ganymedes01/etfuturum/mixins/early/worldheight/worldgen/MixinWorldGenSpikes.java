package ganymedes01.etfuturum.mixins.early.worldheight.worldgen;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenSpikes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.Random;

@Mixin(WorldGenSpikes.class)
public class MixinWorldGenSpikes {

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 256))
    private int getIncreasedWorldHeight(int original, World world, Random random, int x, int y, int z) {

        return world.provider.getHeight();
    }
}
