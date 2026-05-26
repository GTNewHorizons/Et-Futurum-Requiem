package ganymedes01.etfuturum.api.spectator;

import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.core.utils.helpers.SafeEnumHelperClient;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.world.WorldSettings;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import roadhog360.hogutils.api.hogtags.helpers.BlockTags;

@ApiStatus.AvailableSince("3.0.0")
public class SpectatorUtils {
    @ApiStatus.AvailableSince("3.0.0")
    public static final IEntitySelector EXCEPT_SPECTATING;
    @ApiStatus.AvailableSince("3.0.0")
    public static final WorldSettings.GameType SPECTATOR_GAMETYPE;

    static {
        EXCEPT_SPECTATING = p_82704_1_ -> !isSpectator(p_82704_1_);
        SPECTATOR_GAMETYPE = SafeEnumHelperClient.addGameType("spectator", 3, "Spectator");
    }

    @ApiStatus.AvailableSince("3.0.0")
    public static boolean isSpectator(Entity entity) {
        return entity instanceof ISpectatorInfo info && info.etfu$isSpectator();
    }

    @ApiStatus.AvailableSince("3.0.0")
    @Nullable
    public static Entity getSpectatingEntity(Entity entity) {
        return entity instanceof ISpectatorInfo info ? info.etfu$spectatingEntity() : null;
    }

    @ApiStatus.AvailableSince("3.0.0")
    public static boolean canSpectatorSelect(TileEntity te) {
        return (te instanceof IInventory || te instanceof TileEntityEnderChest)
                && !BlockTags.hasTag(te.getBlockType(), te.getBlockMetadata(), Tags.MOD_ID + ":spectators_cannot_interact");
    }
}
