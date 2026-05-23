package ganymedes01.etfuturum.mixins.early.worldthumbnail;

import ganymedes01.etfuturum.client.SpawnChunkProgress;
import ganymedes01.etfuturum.client.loading.LoadingScreenChunkStage;
import ganymedes01.etfuturum.client.loading.LoadingScreenWorldGenTracker;
import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkProviderEnd.class)
public class MixinChunkProviderEnd {

    @Inject(method = "provideChunk", at = @At("HEAD"))
    private void etfu$markBiomes(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        LoadingScreenWorldGenTracker.markGenerated(chunkX, chunkZ);
    }

    @Inject(method = "func_147420_a", at = @At("RETURN"))
    private void etfu$markNoise(int chunkX, int chunkZ, Block[] blocks, BiomeGenBase[] biomes, CallbackInfo ci) {
        LoadingScreenWorldGenTracker.markStage(chunkX, chunkZ, LoadingScreenChunkStage.NOISE);
    }

    @Inject(method = "replaceBiomeBlocks", at = @At("RETURN"), remap = false)
    private void etfu$markSurface(int chunkX, int chunkZ, Block[] blocks, BiomeGenBase[] biomes, byte[] metadata, CallbackInfo ci) {
        LoadingScreenWorldGenTracker.markStage(chunkX, chunkZ, LoadingScreenChunkStage.SURFACE);
    }

    @Inject(
            method = "provideChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/Chunk;generateSkylightMap()V"
            )
    )
    private void etfu$markInitializeLight(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        LoadingScreenWorldGenTracker.markStage(chunkX, chunkZ, LoadingScreenChunkStage.INITIALIZE_LIGHT);
    }

    @Inject(
            method = "provideChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/Chunk;generateSkylightMap()V",
                    shift = At.Shift.AFTER
            )
    )
    private void etfu$markLight(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        LoadingScreenWorldGenTracker.markStage(chunkX, chunkZ, LoadingScreenChunkStage.LIGHT);
    }

    @Inject(method = "populate", at = @At("HEAD"))
    private void etfu$markFeatures(IChunkProvider provider, int chunkX, int chunkZ, CallbackInfo ci) {
        LoadingScreenWorldGenTracker.markStage(chunkX, chunkZ, LoadingScreenChunkStage.FEATURES);
    }
}
