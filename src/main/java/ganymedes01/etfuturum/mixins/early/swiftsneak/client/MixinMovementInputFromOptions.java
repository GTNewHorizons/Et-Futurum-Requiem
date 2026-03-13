package ganymedes01.etfuturum.mixins.early.swiftsneak.client;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInputFromOptions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import ganymedes01.etfuturum.ModEnchantments;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions {

    @ModifyConstant(method = "updatePlayerMoveState", constant = @Constant(doubleValue = 0.3D))
    private double applySwiftSneakModifier(double constant) {
        ItemStack leggings = Minecraft.getMinecraft().thePlayer.getEquipmentInSlot(2);
        int ssLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.swiftSneak.effectId, leggings);
        return constant + (ssLevel * 0.15);
    }
}
