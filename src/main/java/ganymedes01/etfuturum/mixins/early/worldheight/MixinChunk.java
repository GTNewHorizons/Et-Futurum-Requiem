package ganymedes01.etfuturum.mixins.early.worldheight;

import ganymedes01.etfuturum.world.WorldHeightHandler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Chunk.class)
public abstract class MixinChunk {

    @Shadow public World worldObj;

    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 16))
    private int getIncreasedChunkSections(int original) {

        return WorldHeightHandler.getChunkSections();
    }

    @ModifyConstant(method = "getAreLevelsEmpty", constant = @Constant(intValue = 256))
    private int getIncreasedWorldHeight(int original) {

        return worldObj.provider.getHeight();
    }

    @ModifyConstant(method = { "getAreLevelsEmpty",
                               "relightBlock" }, constant = @Constant(intValue = 255))
    private int getIncreasedWorldHeightMinusOne(int original) {

        return worldObj.provider.getHeight() - 1;
    }
}
