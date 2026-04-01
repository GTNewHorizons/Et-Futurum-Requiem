package ganymedes01.etfuturum.mixins.early.worldheight.client;

import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChunkCache.class)
public class MixinChunkCacheClient {

    @Shadow private World worldObj;

    @ModifyConstant(method = { "getSkyBlockTypeBrightness",
                               "getSpecialBlockBrightness" }, constant = @Constant(intValue = 256))
    private int getIncreasedWorldHeight(int original) {

        return worldObj.provider.getHeight();
    }

    @ModifyConstant(method = { "getSkyBlockTypeBrightness",
                               "getSpecialBlockBrightness" }, constant = @Constant(intValue = 255))
    private int getIncreasedWorldHeightMinusOne(int original) {

        return worldObj.provider.getHeight() - 1;
    }
}
