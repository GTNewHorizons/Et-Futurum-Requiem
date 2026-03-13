package ganymedes01.etfuturum.mixins.early.sounds;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ganymedes01.etfuturum.lib.Reference;

@Mixin(ContainerEnchantment.class)
public abstract class MixinContainerEnchantment extends Container {

    @Shadow
    private World worldPointer;
    @Shadow
    private int posX, posY, posZ;

    @Inject(
        method = "enchantItem",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;addExperienceLevel(I)V"))
    private void playEnchantSound(EntityPlayer player, int id, CallbackInfoReturnable<Boolean> cir) {
        worldPointer.playSoundEffect(
            posX + 0.5F,
            posY + 0.5F,
            posZ + 0.5F,
            Reference.MCAssetVer + ":block.enchantment_table.use",
            1.0F,
            worldPointer.rand.nextFloat() * 0.1F + 0.9F);
    }
}
