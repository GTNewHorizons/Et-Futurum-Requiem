package ganymedes01.etfuturum.mixins.early.backlytra;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import ganymedes01.etfuturum.elytra.IElytraPlayer;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer {

    @Shadow
    public EntityPlayerMP playerEntity;

    @ModifyVariable(method = "processPlayer", at = @At("STORE"), ordinal = 10, name = "d10")
    private double adjustMovementDelta(double d) {
        if (this.playerEntity instanceof IElytraPlayer && ((IElytraPlayer) this.playerEntity).etfu$isElytraFlying()) {
            if (playerEntity.worldObj.getGameRules()
                .getGameRuleBooleanValue("disableElytraMovementCheck")) {
                return 0;
            }
            return d / 3;
        }
        return d;
    }
}
