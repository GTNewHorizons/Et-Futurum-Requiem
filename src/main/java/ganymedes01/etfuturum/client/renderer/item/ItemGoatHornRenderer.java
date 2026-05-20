package ganymedes01.etfuturum.client.renderer.item;

import ganymedes01.etfuturum.client.OpenGLHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemGoatHornRenderer implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
		return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
		EntityPlayer player = getPlayer(data);
		OpenGLHelper.pushMatrix();
		if (player != null && player.getItemInUse() == stack) {
			applyUseTransform(type, stack, player);
		}
		renderIcon(stack);
		OpenGLHelper.popMatrix();
	}

	private EntityPlayer getPlayer(Object... data) {
		if (data.length > 1 && data[1] instanceof EntityPlayer) {
			return (EntityPlayer) data[1];
		}
		return Minecraft.getMinecraft().thePlayer;
	}

	private void applyUseTransform(ItemRenderType type, ItemStack stack, EntityPlayer player) {
		float progress = MathHelper.clamp_float((stack.getMaxItemUseDuration() - player.getItemInUseCount()) / 10.0F, 0.0F, 1.0F);
		float settle = MathHelper.sin(progress * (float) Math.PI) * 0.03F;

		if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			OpenGLHelper.translate(-0.28F * progress, 0.18F * progress + settle, -0.08F * progress);
			OpenGLHelper.rotate(-38.0F * progress, 0.0F, 1.0F, 0.0F);
			OpenGLHelper.rotate(22.0F * progress, 1.0F, 0.0F, 0.0F);
			OpenGLHelper.rotate(-24.0F * progress, 0.0F, 0.0F, 1.0F);
		} else {
			OpenGLHelper.translate(-0.1F * progress, 0.1F * progress, 0.03F * progress);
			OpenGLHelper.rotate(-55.0F * progress, 0.0F, 1.0F, 0.0F);
			OpenGLHelper.rotate(25.0F * progress, 1.0F, 0.0F, 0.0F);
			OpenGLHelper.rotate(18.0F * progress, 0.0F, 0.0F, 1.0F);
		}
	}

	private void renderIcon(ItemStack stack) {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		ResourceLocation resource = textureManager.getResourceLocation(stack.getItemSpriteNumber());
		IIcon icon = stack.getItem().getIcon(stack, 0);
		if (icon == null) {
			icon = ((TextureMap) textureManager.getTexture(resource)).getAtlasSprite("missingno");
		}

		textureManager.bindTexture(resource);
		TextureUtil.func_152777_a(false, false, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ItemRenderer.renderItemIn2D(Tessellator.instance, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		TextureUtil.func_147945_b();
	}
}
