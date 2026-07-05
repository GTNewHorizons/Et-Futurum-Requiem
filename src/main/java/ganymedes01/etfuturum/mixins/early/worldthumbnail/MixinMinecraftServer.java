package ganymedes01.etfuturum.mixins.early.worldthumbnail;

import ganymedes01.etfuturum.client.SpawnChunkProgress;
import ganymedes01.etfuturum.client.loading.LoadingScreenStateTracker;
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
        // Hard reset on every world's spawn load so a new/loaded world never inherits the
        // previous world's chunk map or stuck progress. This runs on the integrated server
        // regardless of client-side mods, so it works even where the launch bridge does not.
        LoadingScreenStateTracker.begin();
        LoadingScreenStateTracker.updateChunkRadius(SpawnChunkProgress.SPAWN_CHUNK_RADIUS);
        SpawnChunkProgress.begin(spawn.posX, spawn.posZ);
    }

    @Inject(method = "initialWorldChunkLoad", at = @At("RETURN"))
    private void etfu$endSpawnTracking(CallbackInfo ci) {
        SpawnChunkProgress.end();
        LoadingScreenStateTracker.markDone();
    }
}
