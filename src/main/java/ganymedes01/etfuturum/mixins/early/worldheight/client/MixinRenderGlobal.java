package ganymedes01.etfuturum.mixins.early.worldheight.client;

import ganymedes01.etfuturum.world.WorldHeightHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {

    @Shadow private Minecraft mc;

    @ModifyConstant(method = "<init>" , constant = @Constant(intValue = 16, ordinal = 0))
    private int getIncreasedChunkSectionsConstructor(int original, Minecraft minecraft) {

        return WorldHeightHandler.getChunkSections();
    }

    @ModifyConstant(method = "loadRenderers" , constant = @Constant(intValue = 16, ordinal = 0))
    private int getIncreasedChunkSections(int original) {

        return WorldHeightHandler.getChunkSections();
    }
}
