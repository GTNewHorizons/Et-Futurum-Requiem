package ganymedes01.etfuturum.mixins.early.worldheight.client;

import ganymedes01.etfuturum.world.WorldHeightHandler;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldClient.class)
public class MixinWorldClient {

    @ModifyConstant(method = "doPreChunk", constant = @Constant(intValue = 256))
    private int getIncreasedWorldHeight(int original) {

        return WorldHeightHandler.getMaxWorldHeight();
    }
}
