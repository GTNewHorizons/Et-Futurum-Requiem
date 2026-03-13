package ganymedes01.etfuturum.mixins.late.spectator;

import net.minecraft.entity.player.EntityPlayerMP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import appeng.container.AEBaseContainer;
import appeng.core.sync.packets.PacketInventoryAction;
import appeng.helpers.InventoryAction;
import ganymedes01.etfuturum.spectator.SpectatorMode;

@Mixin(PacketInventoryAction.class)
public class MixinPacketInventoryAction {

    @WrapOperation(
        method = "serverPacketData",
        remap = false,
        at = @At(
            value = "INVOKE",
            target = "Lappeng/container/AEBaseContainer;doAction(Lnet/minecraft/entity/player/EntityPlayerMP;Lappeng/helpers/InventoryAction;IJ)V"))
    private void cancelInventoryActions(AEBaseContainer instance, EntityPlayerMP player, InventoryAction action,
        int slot, long id, Operation<Void> original) {
        if (!SpectatorMode.isSpectator(player)) {
            original.call(instance, player, action, slot, id);
        }
    }
}
