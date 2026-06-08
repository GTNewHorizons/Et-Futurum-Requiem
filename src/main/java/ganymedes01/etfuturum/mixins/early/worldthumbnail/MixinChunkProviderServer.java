package ganymedes01.etfuturum.mixins.early.worldthumbnail;

import ganymedes01.etfuturum.client.SpawnChunkProgress;
import ganymedes01.etfuturum.client.loading.LoadingScreenWorldGenTracker;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer {

    @Inject(method = "loadChunk(II)Lnet/minecraft/world/chunk/Chunk;", at = @At("RETURN"))
    private void etfu$trackDiskLoaded(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        Chunk chunk = cir.getReturnValue();
        if (chunk != null && chunk.isTerrainPopulated) {
            LoadingScreenWorldGenTracker.markFull(chunkX, chunkZ);
        }
    }

    @Inject(
            method = "populate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/Chunk;setChunkModified()V"
            )
    )
    private void etfu$markPopulated(IChunkProvider provider, int chunkX, int chunkZ, CallbackInfo ci) {
        LoadingScreenWorldGenTracker.markFull(chunkX, chunkZ);
    }
}
