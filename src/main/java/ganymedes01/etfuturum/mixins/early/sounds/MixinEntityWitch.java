package ganymedes01.etfuturum.mixins.early.sounds;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ganymedes01.etfuturum.lib.Reference;

@Mixin(EntityWitch.class)
public class MixinEntityWitch extends EntityMob {

    public MixinEntityWitch(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    @Inject(
        method = "onLivingUpdate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemPotion;getEffects(Lnet/minecraft/item/ItemStack;)Ljava/util/List;"))
    public void playDrinkSound(CallbackInfo ci) {
        this.playSound(Reference.MCAssetVer + ":entity.witch.drink", getSoundVolume(), getSoundPitch());
    }

    @Inject(method = "getHurtSound", at = @At(value = "HEAD"), cancellable = true)
    protected void getNewHurtSound(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(Reference.MCAssetVer + ":entity.witch.hurt");
    }

    @Inject(method = "getDeathSound", at = @At(value = "HEAD"), cancellable = true)
    protected void getNewDeathSound(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(Reference.MCAssetVer + ":entity.witch.death");
    }

    @Inject(method = "getLivingSound", at = @At(value = "HEAD"), cancellable = true)
    protected void getNewLivingSound(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(Reference.MCAssetVer + ":entity.witch.ambient");
    }

}
