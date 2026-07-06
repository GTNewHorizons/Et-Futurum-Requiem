package ganymedes01.etfuturum.mixins.early.lanternpose.client;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.renderer.item.ItemLanternRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Extends the right arm to nearly horizontal when the entity is holding a lantern,
 * so it reads as carrying the lantern out in front, hanging from the hand by its chain.
 * Third person only; the first person hand is rendered through a separate path.
 */
@Mixin(ModelBiped.class)
public class MixinModelBiped {

	@Shadow
	public ModelRenderer bipedRightArm;

	@Inject(method = "setRotationAngles", at = @At("RETURN"))
	private void etfu$holdLanternOut(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo ci) {
		if (!(entityIn instanceof EntityLivingBase)) {
			return;
		}
		ItemStack held = ((EntityLivingBase) entityIn).getHeldItem();
		if (held == null) {
			return;
		}
		Block block = Block.getBlockFromItem(held.getItem());
		if (block == ModBlocks.LANTERN.get() || block == ModBlocks.SOUL_LANTERN.get()) {
			// Arm nearly horizontal, pointing forward; drop the idle sideways sway.
			this.bipedRightArm.rotateAngleX = ItemLanternRenderer.LANTERN_ARM_PITCH;
			this.bipedRightArm.rotateAngleZ = 0.0F;
		}
	}
}
