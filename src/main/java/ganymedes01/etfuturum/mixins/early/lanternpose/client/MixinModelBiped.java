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
 * Poses the right arm when the entity is holding a lantern. In third person the arm extends to
 * nearly horizontal so it reads as carrying the lantern out in front. When the first person arm is
 * being drawn (flagged by ItemLanternRenderer.renderingFirstPersonArm) it instead applies a small
 * tilt to the arm pose, so the first person hand tilts without affecting the lantern.
 */
@Mixin(ModelBiped.class)
public class MixinModelBiped {

	@Shadow
	public ModelRenderer bipedRightArm;

	@Inject(method = "setRotationAngles", at = @At("RETURN"))
	private void etfu$holdLanternOut(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo ci) {
		// First person arm: renderFirstPersonArm reuses this same setRotationAngles, so the third
		// person horizontal pose must not run here. Instead apply the first person tilt to the arm
		// pose at the shoulder joint - a real pose change, unlike a GL rotation of the whole frame,
		// and it does not move the lantern.
		if (ItemLanternRenderer.renderingFirstPersonArm) {
			this.bipedRightArm.rotateAngleZ += ItemLanternRenderer.firstPersonArmTilt;
			return;
		}
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
