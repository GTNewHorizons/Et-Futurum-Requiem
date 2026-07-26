package ganymedes01.etfuturum.mixins.early.soulspeed;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import ganymedes01.etfuturum.ModEnchantments;
import ganymedes01.etfuturum.blocks.BlockSoulSoil;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.entities.attributes.EtFuturumEntityAttributes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.management.Attribute;
import java.util.UUID;

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
