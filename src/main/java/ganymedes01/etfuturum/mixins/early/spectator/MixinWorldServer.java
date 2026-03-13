package ganymedes01.etfuturum.mixins.early.spectator;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import ganymedes01.etfuturum.core.utils.Utils;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World {

    public MixinWorldServer(ISaveHandler p_i45368_1_, String p_i45368_2_, WorldProvider p_i45368_3_,
        WorldSettings p_i45368_4_, Profiler p_i45368_5_) {
        super(p_i45368_1_, p_i45368_2_, p_i45368_3_, p_i45368_4_, p_i45368_5_);
    }

    /**
     * Filters spectators out of the playerEntities list before it is checked by areAllPlayersAsleep.
     * Uses original.call to create the iterator to maintain compatibility with any other mixins
     *
     * @param instance
     * @param original
     * @return
     */
    @WrapOperation(
        method = "areAllPlayersAsleep",
        at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private Iterator<EntityPlayer> dontCountSpectatorsForSleepListCheck(List<EntityPlayer> instance,
        Operation<Iterator<EntityPlayer>> original) {
        return original.call(Utils.getListWithoutSpectators(instance));
    }

    /**
     * Filters spectators out of the playerEntities list before it is checked by updateAllPlayersSleepingFlag.
     * Uses original.call to check if empty to maintain compatibility with any other mixins
     *
     * @param instance
     * @param original
     * @return
     */
    @WrapOperation(
        method = "updateAllPlayersSleepingFlag",
        at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private boolean filterSleepList(List<EntityPlayer> instance, Operation<Boolean> original,
        @Share("nonSpectatingPlayers") LocalRef<List<EntityPlayer>> nonSpectatingPlayers) {
        List<EntityPlayer> list = Utils.getListWithoutSpectators(instance);
        nonSpectatingPlayers.set(list);
        return original.call(list);
    }

    /**
     * Use the filtered list to create the iterator instead of the main one.
     *
     * @param instance
     * @param original
     * @return
     */
    @WrapOperation(
        method = "updateAllPlayersSleepingFlag",
        at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private Iterator<EntityPlayer> dontCountSpectatorsForSleepListFlag(List<EntityPlayer> instance,
        Operation<Iterator<EntityPlayer>> original,
        @Share("nonSpectatingPlayers") LocalRef<List<EntityPlayer>> nonSpectatingPlayers) {
        return original.call(nonSpectatingPlayers.get());
    }
}
