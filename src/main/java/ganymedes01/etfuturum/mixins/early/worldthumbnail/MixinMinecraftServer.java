package ganymedes01.etfuturum.mixins.early.worldthumbnail;

import ganymedes01.etfuturum.client.SpawnChunkProgress;
import ganymedes01.etfuturum.client.loading.LoadingScreenStateTracker;
import ganymedes01.etfuturum.client.loading.LoadingScreenWorldGenTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    @Shadow
    public WorldServer[] worldServers;

    @Inject(method = "initialWorldChunkLoad", at = @At("HEAD"))
    private void etfu$beginSpawnTracking(CallbackInfo ci) {
        WorldServer world = worldServers[0];
        ChunkCoordinates spawn = world.getSpawnPoint();
        LoadingScreenStateTracker.beginIfNeeded();
        LoadingScreenStateTracker.updateChunkRadius(SpawnChunkProgress.SPAWN_CHUNK_RADIUS, false);
        LoadingScreenWorldGenTracker.beginVanillaSpawn(
                spawn.posX >> 4,
                spawn.posZ >> 4,
                SpawnChunkProgress.SPAWN_CHUNK_RADIUS
        );
        SpawnChunkProgress.begin(spawn.posX, spawn.posZ);
    }

    @Inject(method = "initialWorldChunkLoad", at = @At("RETURN"))
    private void etfu$endSpawnTracking(CallbackInfo ci) {
        SpawnChunkProgress.end();
        LoadingScreenWorldGenTracker.finishVanillaSpawn();
        LoadingScreenStateTracker.markDone();
    }
}
