package ganymedes01.etfuturum.mixins.early.soulspeed;

import ganymedes01.etfuturum.ModEnchantments;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BlockSoulSand.class)
public class BlockSoulSandMixin {
    @ModifyConstant(method = "onEntityCollidedWithBlock", constant = @Constant(doubleValue = 0.4D))
    private double handleSoulSpeed(double constant, World worldIn, int x, int y, int z, Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase) entityIn;
            ItemStack boots = entity.getEquipmentInSlot(1);
            int ssLevel = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.soulSpeed.effectId, boots);
            if(ssLevel > 0) {
                return 1.0D;
            }
        }
        return constant;
    }
}
