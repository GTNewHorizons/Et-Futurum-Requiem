package ganymedes01.etfuturum.mixins.early.worldthumbnail;

import ganymedes01.etfuturum.client.SpawnChunkProgress;
import ganymedes01.etfuturum.client.loading.LoadingScreenHooks;
import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderHell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkProviderHell.class)
public class MixinChunkProviderHell {

    @Inject(method = "provideChunk", at = @At("HEAD"))
    private void etfu$markBiomes(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        SpawnChunkProgress.markGenerated(chunkX, chunkZ);
        LoadingScreenHooks.updateServerChunkColor(chunkX, chunkZ, LoadingScreenHooks.CHUNK_COLOR_BIOMES);
        LoadingScreenHooks.updateServerChunkProgress();
    }

    @Inject(method = "func_147419_a", at = @At("RETURN"))
    private void etfu$markNoise(int chunkX, int chunkZ, Block[] blocks, CallbackInfo ci) {
        LoadingScreenHooks.updateServerChunkColor(chunkX, chunkZ, LoadingScreenHooks.CHUNK_COLOR_NOISE);
    }

    @Inject(method = "replaceBiomeBlocks", at = @At("RETURN"), remap = false)
    private void etfu$markSurface(int chunkX, int chunkZ, Block[] blocks, byte[] metadata, BiomeGenBase[] biomes, CallbackInfo ci) {
        LoadingScreenHooks.updateServerChunkColor(chunkX, chunkZ, LoadingScreenHooks.CHUNK_COLOR_SURFACE);
    }

    @Inject(
            method = "provideChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/MapGenBase;func_151539_a(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/World;II[Lnet/minecraft/block/Block;)V"
            )
    )
    private void etfu$markCarvers(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        LoadingScreenHooks.updateServerChunkColor(chunkX, chunkZ, LoadingScreenHooks.CHUNK_COLOR_CARVERS);
    }

    @Inject(
            method = "provideChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/structure/MapGenNetherBridge;func_151539_a(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/World;II[Lnet/minecraft/block/Block;)V"
            )
    )
    private void etfu$markStructureStarts(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        LoadingScreenHooks.updateServerChunkColor(chunkX, chunkZ, LoadingScreenHooks.CHUNK_COLOR_STRUCTURE_STARTS);
    }

    @Inject(
            method = "provideChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/structure/MapGenNetherBridge;func_151539_a(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/World;II[Lnet/minecraft/block/Block;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void etfu$markStructureReferences(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        LoadingScreenHooks.updateServerChunkColor(chunkX, chunkZ, LoadingScreenHooks.CHUNK_COLOR_STRUCTURE_REFERENCES);
    }

    @Inject(
            method = "provideChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/Chunk;resetRelightChecks()V"
            )
    )
    private void etfu$markInitializeLight(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        LoadingScreenHooks.updateServerChunkColor(chunkX, chunkZ, LoadingScreenHooks.CHUNK_COLOR_INITIALIZE_LIGHT);
    }

    @Inject(
            method = "provideChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/Chunk;resetRelightChecks()V",
                    shift = At.Shift.AFTER
            )
    )
    private void etfu$markLight(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        LoadingScreenHooks.updateServerChunkColor(chunkX, chunkZ, LoadingScreenHooks.CHUNK_COLOR_LIGHT);
    }

    @Inject(method = "populate", at = @At("HEAD"))
    private void etfu$markFeatures(IChunkProvider provider, int chunkX, int chunkZ, CallbackInfo ci) {
        LoadingScreenHooks.updateServerChunkColor(chunkX, chunkZ, LoadingScreenHooks.CHUNK_COLOR_FEATURES);
    }
}
