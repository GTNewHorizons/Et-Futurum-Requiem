package ganymedes01.etfuturum.client.renderer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.potion.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class GlowingEffectRenderer {

	private static final int GL_COMBINE = 34160;
	private static final int GL_COMBINE_RGB = 34161;
	private static final int GL_COMBINE_ALPHA = 34162;
	private static final int GL_SOURCE0_RGB = 34176;
	private static final int GL_SOURCE0_ALPHA = 34184;
	private static final int GL_OPERAND0_RGB = 34192;
	private static final int GL_OPERAND0_ALPHA = 34200;
	private static final int GL_CONSTANT = 34166;
	private static final int GL_REPLACE = 7681;
	private static final int GL_SRC_COLOR = 768;
	private static final int GL_TEXTURE = 5890;
	private static final int GL_SRC_ALPHA = 770;
	private static final int ATTRIB_MASK = GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_TEXTURE_BIT | GL11.GL_VIEWPORT_BIT | GL11.GL_TRANSFORM_BIT | GL11.GL_CURRENT_BIT;
	private static final FloatBuffer OUTLINE_COLOR = BufferUtils.createFloatBuffer(4);

	private Framebuffer outlineFramebuffer;
	private Framebuffer expandedFramebuffer;

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		if (!ConfigBlocksItems.enableSpectralArrows || ModPotions.glowing == null || !OpenGlHelper.isFramebufferEnabled())
			return;

		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.thePlayer;
		if (player == null || player.worldObj == null)
			return;

		@SuppressWarnings("unchecked")
		List<Entity> allEntities = player.worldObj.loadedEntityList;
		List<Entity> glowingEntities = new ArrayList<>();

		for (Entity entity : allEntities) {
			if (entity == player || !(entity instanceof EntityLivingBase))
				continue;
			EntityLivingBase living = (EntityLivingBase) entity;
			if (living.getActivePotionEffect(ModPotions.glowing) == null)
				continue;
			if (entity.isDead || entity.isInvisible())
				continue;
			glowingEntities.add(entity);
		}

		if (glowingEntities.isEmpty())
			return;

		ensureFramebuffer(mc);

		double viewerX = player.prevPosX + (player.posX - player.prevPosX) * event.partialTicks;
		double viewerY = player.prevPosY + (player.posY - player.prevPosY) * event.partialTicks;
		double viewerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * event.partialTicks;

		double savedRenderPosX = RenderManager.renderPosX;
		double savedRenderPosY = RenderManager.renderPosY;
		double savedRenderPosZ = RenderManager.renderPosZ;
		int previousMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);

		try {
			RenderManager.renderPosX = viewerX;
			RenderManager.renderPosY = viewerY;
			RenderManager.renderPosZ = viewerZ;

			outlineFramebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
			outlineFramebuffer.framebufferClear();
			outlineFramebuffer.bindFramebuffer(true);
			renderOutlineMask(glowingEntities, viewerX, viewerY, viewerZ, event.partialTicks);
			outlineFramebuffer.unbindFramebuffer();

			buildExpandedOutline(mc);
			mc.getFramebuffer().bindFramebuffer(false);
			compositeOutline(mc);
		} finally {
			expandedFramebuffer.unbindFramebufferTexture();
			expandedFramebuffer.unbindFramebuffer();
			outlineFramebuffer.unbindFramebufferTexture();
			outlineFramebuffer.unbindFramebuffer();
			mc.getFramebuffer().bindFramebuffer(false);
			RenderManager.renderPosX = savedRenderPosX;
			RenderManager.renderPosY = savedRenderPosY;
			RenderManager.renderPosZ = savedRenderPosZ;
			restoreRenderState(mc, previousMatrixMode);
		}
	}

	private void ensureFramebuffer(Minecraft mc) {
		if (outlineFramebuffer == null || outlineFramebuffer.framebufferWidth != mc.displayWidth || outlineFramebuffer.framebufferHeight != mc.displayHeight) {
			if (outlineFramebuffer != null) {
				outlineFramebuffer.deleteFramebuffer();
			}
			if (expandedFramebuffer != null) {
				expandedFramebuffer.deleteFramebuffer();
			}
			outlineFramebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
			outlineFramebuffer.setFramebufferFilter(GL11.GL_NEAREST);
			expandedFramebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
			expandedFramebuffer.setFramebufferFilter(GL11.GL_NEAREST);
		}
	}

	private void renderOutlineMask(List<Entity> entities, double viewerX, double viewerY, double viewerZ, float partialTicks) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		for (Entity entity : entities) {
			Render renderer = RenderManager.instance.getEntityRenderObject(entity);
			if (renderer == null)
				continue;

			double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - viewerX;
			double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - viewerY;
			double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - viewerZ;
			float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;

			GL11.glPushAttrib(ATTRIB_MASK);
			try {
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_COLOR_MATERIAL);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				enableOutlineMode(GlowingRenderHelper.getEntityTeamColor(entity) | 0xFF000000);
				renderer.doRender(entity, x, y, z, yaw, partialTicks);
			} finally {
				disableOutlineMode();
				GL11.glPopAttrib();
			}
		}
	}

	private void restoreRenderState(Minecraft mc, int previousMatrixMode) {
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glMatrixMode(previousMatrixMode);
		GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void buildExpandedOutline(Minecraft mc) {
		expandedFramebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
		expandedFramebuffer.framebufferClear();
		expandedFramebuffer.bindFramebuffer(true);
		outlineFramebuffer.bindFramebufferTexture();

		int previousMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		try {
			setupScreenQuadState(mc);

			Tessellator tessellator = Tessellator.instance;
			float w = mc.displayWidth;
			float h = mc.displayHeight;
			float texW = outlineFramebuffer.framebufferTextureWidth;
			float texH = outlineFramebuffer.framebufferTextureHeight;
			float uMax = outlineFramebuffer.framebufferWidth / texW;
			float vMax = outlineFramebuffer.framebufferHeight / texH;
			float pixelU = 1.0F / texW;
			float pixelV = 1.0F / texH;

			for (int i = 0; i < 4; i++) {
				float offU = 0.0F;
				float offV = 0.0F;
				switch (i) {
					case 0: offU = -pixelU; break;
					case 1: offU = pixelU; break;
					case 2: offV = -pixelV; break;
					case 3: offV = pixelV; break;
				}
				drawFramebufferQuad(tessellator, w, h, offU, offV, uMax + offU, vMax + offV);
			}

			GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ONE_MINUS_SRC_ALPHA);
			drawFramebufferQuad(tessellator, w, h, 0.0F, 0.0F, uMax, vMax);
		} finally {
			outlineFramebuffer.unbindFramebufferTexture();
			expandedFramebuffer.unbindFramebuffer();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glPopMatrix();
			GL11.glMatrixMode(previousMatrixMode);
		}
	}

	private void compositeOutline(Minecraft mc) {
		mc.getFramebuffer().bindFramebuffer(false);
		expandedFramebuffer.bindFramebufferTexture();

		int previousMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		try {
			setupScreenQuadState(mc);

			Tessellator tessellator = Tessellator.instance;
			float w = mc.displayWidth;
			float h = mc.displayHeight;
			float uMax = (float) expandedFramebuffer.framebufferWidth / (float) expandedFramebuffer.framebufferTextureWidth;
			float vMax = (float) expandedFramebuffer.framebufferHeight / (float) expandedFramebuffer.framebufferTextureHeight;
			drawFramebufferQuad(tessellator, w, h, 0.0F, 0.0F, uMax, vMax);
		} finally {
			expandedFramebuffer.unbindFramebufferTexture();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glPopMatrix();
			GL11.glMatrixMode(previousMatrixMode);
		}
	}

	private static void setupScreenQuadState(Minecraft mc) {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, mc.displayWidth, mc.displayHeight, 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);

		GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private static void drawFramebufferQuad(Tessellator tessellator, float w, float h, float minU, float minV, float maxU, float maxV) {
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(0.0D, h, 0.0D, minU, minV);
		tessellator.addVertexWithUV(w, h, 0.0D, maxU, minV);
		tessellator.addVertexWithUV(w, 0.0D, 0.0D, maxU, maxV);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, minU, maxV);
		tessellator.draw();
	}

	private static void enableOutlineMode(int color) {
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		OUTLINE_COLOR.clear();
		OUTLINE_COLOR.put((color >> 16 & 255) / 255.0F);
		OUTLINE_COLOR.put((color >> 8 & 255) / 255.0F);
		OUTLINE_COLOR.put((color & 255) / 255.0F);
		OUTLINE_COLOR.put((color >> 24 & 255) / 255.0F);
		OUTLINE_COLOR.flip();

		GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, OUTLINE_COLOR);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL_COMBINE);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL_COMBINE_RGB, GL_REPLACE);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL_SOURCE0_RGB, GL_CONSTANT);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL_OPERAND0_RGB, GL_SRC_COLOR);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL_COMBINE_ALPHA, GL_REPLACE);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL_SOURCE0_ALPHA, GL_TEXTURE);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL_OPERAND0_ALPHA, GL_SRC_ALPHA);
	}

	private static void disableOutlineMode() {
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL_COMBINE_RGB, GL11.GL_MODULATE);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL_COMBINE_ALPHA, GL11.GL_MODULATE);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL_SOURCE0_RGB, GL_TEXTURE);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL_SOURCE0_ALPHA, GL_TEXTURE);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL_OPERAND0_RGB, GL_SRC_COLOR);
		GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL_OPERAND0_ALPHA, GL_SRC_ALPHA);
	}

}
