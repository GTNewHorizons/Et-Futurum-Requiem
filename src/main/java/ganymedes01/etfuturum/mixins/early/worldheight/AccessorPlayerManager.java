package ganymedes01.etfuturum.mixins.early.worldheight;

import net.minecraft.server.management.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PlayerManager.class)
public interface AccessorPlayerManager {

    @Accessor("chunkWatcherWithPlayers")
    List getChunkWatcherWithPlayers();
}
