package ganymedes01.etfuturum.mixins.early.worldthumbnail;

import ganymedes01.etfuturum.client.SpawnChunkProgress;
import ganymedes01.etfuturum.client.loading.LoadingScreenChunkStage;
import ganymedes01.etfuturum.client.loading.LoadingScreenWorldGenTracker;
import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkProviderGenerate.class)
public class MixinChunkProviderGenerate {

    @Inject(method = "provideChunk", at = @At("HEAD"))
    private void etfu$markBiomes(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        LoadingScreenWorldGenTracker.markGenerated(chunkX, chunkZ);
    }

    @Inject(method = "func_147424_a", at = @At("RETURN"))
    private void etfu$markNoise(int chunkX, int chunkZ, Block[] blocks, CallbackInfo ci) {
        LoadingScreenWorldGenTracker.markStage(chunkX, chunkZ, LoadingScreenChunkStage.NOISE);
    }

    @Inject(method = "replaceBlocksForBiome", at = @At("RETURN"))
    private void etfu$markSurface(int chunkX, int chunkZ, Block[] blocks, byte[] metadata, BiomeGenBase[] biomes, CallbackInfo ci) {
        LoadingScreenWorldGenTracker.markStage(chunkX, chunkZ, LoadingScreenChunkStage.SURFACE);
    }

    @Inject(
            method = "provideChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/MapGenBase;func_151539_a(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/World;II[Lnet/minecraft/block/Block;)V",
                    ordinal = 0
            )
    )
    private void etfu$markCarvers(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        LoadingScreenWorldGenTracker.markStage(chunkX, chunkZ, LoadingScreenChunkStage.CARVERS);
    }

    @Inject(
            method = "provideChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/structure/MapGenMineshaft;func_151539_a(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/World;II[Lnet/minecraft/block/Block;)V"
            )
    )
    private void etfu$markStructureStarts(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        LoadingScreenWorldGenTracker.markStage(chunkX, chunkZ, LoadingScreenChunkStage.STRUCTURE_STARTS);
    }

    @Inject(
            method = "provideChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/structure/MapGenScatteredFeature;func_151539_a(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/World;II[Lnet/minecraft/block/Block;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void etfu$markStructureReferences(int chunkX, int chunkZ, CallbackInfoReturnable<Chunk> cir) {
        LoadingScreenWorldGenTracker.markStage(chunkX, chunkZ, LoadingScreenChunkStage.STRUCTURE_REFERENCES);
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

    @Inject(
            method = "populate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/SpawnerAnimals;performWorldGenSpawning(Lnet/minecraft/world/World;Lnet/minecraft/world/biome/BiomeGenBase;IIIILjava/util/Random;)V",
                    remap = false
            )
    )
    private void etfu$markSpawn(IChunkProvider provider, int chunkX, int chunkZ, CallbackInfo ci) {
        LoadingScreenWorldGenTracker.markStage(chunkX, chunkZ, LoadingScreenChunkStage.SPAWN);
    }
}
