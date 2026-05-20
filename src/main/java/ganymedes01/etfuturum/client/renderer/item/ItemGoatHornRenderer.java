package ganymedes01.etfuturum.client.renderer.item;

import ganymedes01.etfuturum.client.OpenGLHelper;
import ganymedes01.etfuturum.items.ItemGoatHorn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemGoatHornRenderer implements IItemRenderer {

	private static final float LEGACY_RENDER_SCALE = 1.5F;

	@Override
	public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
		return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
		return helper == ItemRendererHelper.EQUIPPED_BLOCK && (type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON);
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
		EntityPlayer player = getPlayer(data);
		OpenGLHelper.pushMatrix();
		if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			applyIdleTransform(type);
		} else {
			applyThirdPersonTransform(player != null && isUsingHorn(player, stack));
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

	private boolean isUsingHorn(EntityPlayer player, ItemStack stack) {
		return ItemGoatHorn.isGoatHorn(stack) && player.getItemInUseCount() > 0 && ItemGoatHorn.isGoatHorn(player.getItemInUse());
	}

	private void applyIdleTransform(ItemRenderType type) {
		if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
			applyDisplayTransform(1.13F, 3.2F, 1.13F, 0.0F, -90.0F, 25.0F, 0.68F);
		} else {
			applyThirdPersonTransform(false);
		}
	}

	private void applyThirdPersonTransform(boolean using) {
		if (using) {
			applyDisplayTransform(-1.0F, 2.0F, 2.0F, 0.0F, -125.0F, 0.0F, 0.5F);
		} else {
			applyDisplayTransform(4.0F, 12.0F, -2.0F, 0.0F, 0.0F, 35.0F, 0.85F);
		}
	}

	private void applyDisplayTransform(float x, float y, float z, float xRot, float yRot, float zRot, float scale) {
		OpenGLHelper.translate(x * 0.0625F, y * 0.0625F, z * 0.0625F);
		OpenGLHelper.rotate(xRot, 1.0F, 0.0F, 0.0F);
		OpenGLHelper.rotate(yRot, 0.0F, 1.0F, 0.0F);
		OpenGLHelper.rotate(zRot, 0.0F, 0.0F, 1.0F);
		scale *= LEGACY_RENDER_SCALE;
		OpenGLHelper.scale(scale, scale, scale);
	}

	public static void renderIcon(ItemStack stack) {
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
