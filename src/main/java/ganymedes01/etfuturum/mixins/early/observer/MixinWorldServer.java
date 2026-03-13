package ganymedes01.etfuturum.mixins.early.observer;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import ganymedes01.etfuturum.ducks.IObserverWorldExtension;

@Mixin(WorldServer.class)
public class MixinWorldServer implements IObserverWorldExtension {

    @Shadow
    private Set<NextTickListEntry> pendingTickListEntriesHashSet;

    @Override
    public boolean etfu$hasScheduledUpdate(int x, int y, int z, Block block) {
        return this.pendingTickListEntriesHashSet.contains(new NextTickListEntry(x, y, z, block));
    }
}
