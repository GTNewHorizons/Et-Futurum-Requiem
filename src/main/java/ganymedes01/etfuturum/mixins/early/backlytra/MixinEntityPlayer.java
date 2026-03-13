package ganymedes01.etfuturum.mixins.early.backlytra;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.elytra.IElytraPlayer;
import ganymedes01.etfuturum.items.equipment.ItemArmorElytra;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase implements IElytraPlayer {

    @Override
    @Shadow
    public abstract boolean isPlayerSleeping();

    @Shadow
    public PlayerCapabilities capabilities;

    @Shadow
    protected abstract void setHideCape(int par1, boolean par2);

    @Shadow
    public InventoryPlayer inventory;

    public MixinEntityPlayer(World p_i1594_1_) {
        super(p_i1594_1_);
    }

    public void tickElytra() {
        boolean flag = etfu$isElytraFlying();

        ItemStack itemstack = ItemArmorElytra.getElytra(this);
        this.setHideCape(1, itemstack != null);
        if (itemstack != null && !capabilities.isFlying && !ItemArmorElytra.isBroken(itemstack)) {
            if (flag && !this.onGround && !this.isRiding() && !this.isInWater()) {
                if (!this.worldObj.isRemote && (this.etfu$ticksElytraFlying + 1) % 20 == 0) {
                    itemstack.damageItem(1, this);
                }
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }

        if (!this.worldObj.isRemote) {
            this.etfu$setElytraFlying(flag);
        }

        if (this.etfu$isElytraFlying()) {
            this.etfu$ticksElytraFlying += 1;
        } else {
            this.etfu$ticksElytraFlying = 0;
        }
    }

    @Inject(method = "getEyeHeight", at = @At("HEAD"), cancellable = true)
    private void getElytraEyeHeight(CallbackInfoReturnable<Float> cir) {
        if (this.etfu$isElytraFlying() && !this.isPlayerSleeping()) {
            cir.setReturnValue(0.4f);
        }
    }

    private float etfu$ticksElytraFlying = 0;
    private boolean etfu$lastElytraFlying = false;

    @Override
    public boolean etfu$isElytraFlying() {
        return getFlag(ConfigFunctions.elytraDataWatcherFlag);
    }

    @Override
    public void etfu$setElytraFlying(boolean flag) {
        setFlag(ConfigFunctions.elytraDataWatcherFlag, flag);
    }

    @Override
    public float etfu$getTicksElytraFlying() {
        return etfu$ticksElytraFlying;
    }

    @Override
    public boolean etfu$lastElytraFlying() {
        return etfu$lastElytraFlying;
    }

    @Override
    public void etfu$setLastElytraFlying(boolean flag) {
        etfu$lastElytraFlying = flag;
    }

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void writeElytra(NBTTagCompound tagCompound, CallbackInfo ci) {
        tagCompound.setBoolean("FallFlying", etfu$isElytraFlying());
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void readElytra(NBTTagCompound tagCompound, CallbackInfo ci) {
        if (tagCompound.getBoolean("FallFlying")) etfu$setElytraFlying(true);
    }
}
