package ganymedes01.etfuturum.mixins.late.backhand;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.renderer.item.ItemLanternRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xonin.backhand.api.core.BackhandUtils;

/**
 * When a player carries a lantern in the Backhand offhand, extend the left arm nearly horizontal so
 * it mirrors the main-hand pose (carrying the lantern out in front). Late mixin: only applied when
 * Backhand is present, so the BackhandUtils reference is safe.
 */
@Mixin(ModelBiped.class)
public class MixinModelBipedOffhand {

	@Shadow
	public ModelRenderer bipedLeftArm;

	@Inject(method = "setRotationAngles", at = @At("RETURN"))
	private void etfu$holdOffhandLanternOut(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn, CallbackInfo ci) {
		// Leave the first person arm alone (handled separately) and only pose players.
		if (ItemLanternRenderer.renderingFirstPersonArm || !(entityIn instanceof EntityPlayer)) {
			return;
		}
		ItemStack offhand = BackhandUtils.getOffhandItem((EntityPlayer) entityIn);
		if (offhand == null) {
			return;
		}
		Block block = Block.getBlockFromItem(offhand.getItem());
		if (block == ModBlocks.LANTERN.get() || block == ModBlocks.SOUL_LANTERN.get()) {
			// Mirror of the main-hand pose and sway in the early MixinModelBiped. The sideways (Z)
			// component is negated so the left arm sways symmetrically to the right one.
			float sway = ItemLanternRenderer.armSwaySpeed * ageInTicks;
			this.bipedLeftArm.rotateAngleX = ItemLanternRenderer.LANTERN_ARM_PITCH
					+ MathHelper.sin(sway) * ItemLanternRenderer.armSwayAmount
					+ MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount * ItemLanternRenderer.armWalkSwayAmount;
			this.bipedLeftArm.rotateAngleZ = -MathHelper.cos(sway) * ItemLanternRenderer.armSwayAmount;
		}
	}
}
