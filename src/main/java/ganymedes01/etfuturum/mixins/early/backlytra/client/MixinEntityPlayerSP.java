package ganymedes01.etfuturum.mixins.early.backlytra.client;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.elytra.IElytraPlayer;
import ganymedes01.etfuturum.items.equipment.ItemArmorElytra;
import ganymedes01.etfuturum.network.StartElytraFlyingMessage;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    @Shadow
    public MovementInput movementInput;
    private boolean etfu$lastIsJumping = false;
    private boolean etfu$lastIsFlying = false;

    public MixinEntityPlayerSP(World p_i45074_1_, GameProfile p_i45074_2_) {
        super(p_i45074_1_, p_i45074_2_);
    }

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void startElytraFlying(CallbackInfo ci) {
        if (!Utils.badBetterFPSAlgorithm()) {
            if (this.movementInput.jump && !etfu$lastIsJumping
                && !this.onGround
                && (ConfigMixins.enableNewElytraTakeoffLogic || this.motionY < 0.0D)
                && !((IElytraPlayer) this).etfu$isElytraFlying()
                && !etfu$lastIsFlying) {
                ItemStack itemstack = ItemArmorElytra.getElytra(this);
                if (itemstack != null && !ItemArmorElytra.isBroken(itemstack)) {
                    EtFuturum.networkWrapper.sendToServer(new StartElytraFlyingMessage());
                    // Minecraft.getMinecraft().getSoundHandler().playSound(new ElytraSound(e));
                }
            }
            etfu$lastIsJumping = this.movementInput.jump;
            etfu$lastIsFlying = this.capabilities.isFlying;
        }
    }
}
