package ganymedes01.etfuturum.mixins.early.modernchestconnections;

import net.minecraft.tileentity.TileEntityChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TileEntityChest.class)
public interface AccessorTileEntityChest {
    @Invoker("func_145978_a")
    void etfu$updateAdjacentChest(TileEntityChest tileEntityChest, int dir);
}
