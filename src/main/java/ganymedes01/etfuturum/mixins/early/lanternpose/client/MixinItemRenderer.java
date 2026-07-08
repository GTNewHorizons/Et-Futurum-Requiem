package ganymedes01.etfuturum.mixins.early.lanternpose.client;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.client.renderer.item.ItemLanternRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * First person: when holding a lantern, render the player's empty-hand arm instead of the
 * floating item, then draw the 3D lantern gripped in that hand. Achieved by temporarily
 * nulling the held item so vanilla renders the arm exactly as for an empty hand, then
 * restoring it. Only active when the held-lantern pose is enabled.
 */
@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

	@Shadow
	private ItemStack itemToRender;

	@Unique
	private ItemStack etfu$heldLantern;

	@Unique
	private final RenderBlocks etfu$renderBlocks = new RenderBlocks();

	@Inject(method = "renderItemInFirstPerson", at = @At("HEAD"))
	private void etfu$armInsteadOfLantern(float partialTicks, CallbackInfo ci) {
		ItemStack held = this.itemToRender;
		if (held == null) {
			return;
		}
		Block block = Block.getBlockFromItem(held.getItem());
		if (block != ModBlocks.LANTERN.get() && block != ModBlocks.SOUL_LANTERN.get()) {
			return;
		}
		// Make vanilla render the empty-hand arm; keep the stack to restore afterwards.
		this.etfu$heldLantern = held;
		this.itemToRender = null;
		// Tell MixinModelBiped to leave the first person arm alone while it renders below.
		ItemLanternRenderer.renderingFirstPersonArm = true;
	}

	/**
	 * Replaces the empty-hand arm draw (ordinal 1; ordinal 0 is the map branch, which never runs
	 * with a null item). When holding a lantern: raise and extend the arm, draw it, then hang the
	 * 3D lantern in the same frame so both move together. One redirect keeps the ordering explicit
	 * instead of stacking a BEFORE and an AFTER inject on the same call, where the BEFORE one did
	 * not take effect. With no lantern held it just draws the arm as vanilla would.
	 */
	@Redirect(
			method = "renderItemInFirstPerson",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/entity/RenderPlayer;renderFirstPersonArm(Lnet/minecraft/entity/player/EntityPlayer;)V",
					ordinal = 1))
	private void etfu$armAndLantern(RenderPlayer renderPlayer, EntityPlayer player) {
		if (this.etfu$heldLantern == null) {
			renderPlayer.renderFirstPersonArm(player);
			return;
		}
		// Raise/extend the arm; this offset moves the lantern too so they stay together.
		ItemLanternRenderer.applyFirstPersonArmOffset();
		// The arm tilt itself is applied to the model pose in MixinModelBiped, so it changes the arm
		// without touching the lantern.
		renderPlayer.renderFirstPersonArm(player);
		Block block = Block.getBlockFromItem(this.etfu$heldLantern.getItem());
		if (block != null) {
			ItemLanternRenderer.drawFirstPersonHangingLantern(this.etfu$renderBlocks, block);
		}
	}

	@Inject(method = "renderItemInFirstPerson", at = @At("RETURN"))
	private void etfu$restoreLantern(float partialTicks, CallbackInfo ci) {
		if (this.etfu$heldLantern != null) {
			this.itemToRender = this.etfu$heldLantern;
			this.etfu$heldLantern = null;
		}
		ItemLanternRenderer.renderingFirstPersonArm = false;
	}
}
