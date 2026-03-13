package ganymedes01.etfuturum.mixins.early.sounds;

import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ganymedes01.etfuturum.lib.Reference;

@Mixin(ItemHoe.class)
public abstract class MixinItemHoe extends Item {

    @Redirect(
        method = "onItemUse",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSoundEffect(DDDLjava/lang/String;FF)V"))
    private void overwriteSound(World world, double x, double y, double z, String string, float volume, float pitch) {
        world.playSoundEffect(x, y, z, Reference.MCAssetVer + ":item.hoe.till", 1.0F, 1.0F);
    }
}
