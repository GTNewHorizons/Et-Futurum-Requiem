package ganymedes01.etfuturum.mixins.early.observer;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ganymedes01.etfuturum.blocks.BlockObserver;

@Mixin(Chunk.class)
public abstract class MixinChunk {

    @Inject(method = "populateChunk", at = @At(value = "HEAD"))
    private void disableObserverNotifications(IChunkProvider provider1, IChunkProvider provider2, int x, int z,
        CallbackInfo ci) {
        BlockObserver.disableNotifications();
    }

    @Inject(method = "populateChunk", at = @At(value = "RETURN"))
    private void enableObserverNotifications(IChunkProvider provider1, IChunkProvider provider2, int x, int z,
        CallbackInfo ci) {
        BlockObserver.enableNotifications();
    }
}
