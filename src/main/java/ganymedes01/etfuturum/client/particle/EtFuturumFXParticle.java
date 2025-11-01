package ganymedes01.etfuturum.client.particle;

import ganymedes01.etfuturum.client.OpenGLHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import roadhog360.hogutils.api.utils.FastRandom;

import java.util.Random;

public class EtFuturumFXParticle extends EntityFX {

	protected int color;
	protected int textures;
	protected int currentTexture = 0;
	protected float entityBrightness;
	private boolean fadingColor;
	protected boolean fadeAway;
	private float fadeTargetRed;
	private float fadeTargetGreen;
	private float fadeTargetBlue;
	float particleAngle;
	float prevParticleAngle;
	protected boolean usesSheet;
	protected final ResourceLocation[] resourceLocations;

	protected static final Random particleRand = new FastRandom();

	public EtFuturumFXParticle(World world, double x, double y, double z, double mx, double my, double mz, int maxAge,
							   float scale, int color, String texture, int textures) {
		super(world, x, y, z, 0, 0, 0);
		rand = particleRand;
		this.textures = textures;
		resourceLocations = loadTextures(world, x, y, z, texture == null ? null : new ResourceLocation(texture));
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.motionX = this.motionX * 0.009999999776482582D + mx;
		this.motionY = this.motionY * 0.009999999776482582D + my;
		this.motionZ = this.motionZ * 0.009999999776482582D + mz;
		this.particleMaxAge = maxAge;
		this.noClip = true;
		this.color = color;
		this.particleScale = scale;
		fadingColor = false;
		particleAlpha = (this.color >> 24 & 0xff) / 255F;
		particleRed = (this.color >> 16 & 0xff) / 255F;
		particleGreen = (this.color >> 8 & 0xff) / 255F;
		particleBlue = (this.color & 0xff) / 255F;
	}

	protected ResourceLocation[] loadTextures(World world, double x, double y, double z, ResourceLocation texture) {
		this.usesSheet = texture != null && texture.toString().equals("minecraft:textures/particle/particles.png");
		ResourceLocation[] newRS = new ResourceLocation[usesSheet ? 1 : textures];
		if (textures == 1 || usesSheet) {
			newRS[0] = texture;
		} else for (int i = 0; i < textures; i++) {
			String textureName = texture.toString();
			int length = textureName.length();
			newRS[i] = new ResourceLocation(textureName.substring(0, length - 4) + "_" + i + textureName.substring(length - 4));
		}
		return newRS;
	}

	@Override
	public void renderParticle(Tessellator tessellator, float partialTicks, float rx, float rxz, float rz, float ryz, float rxy) {
		int prevTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

		Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocations[usesSheet ? 0 : currentTexture % textures]);

		OpenGLHelper.enableBlend();
		OpenGLHelper.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPushMatrix();
		float ipx = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - EntityFX.interpPosX);
		float ipy = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - EntityFX.interpPosY);
		float ipz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - EntityFX.interpPosZ);
		if (particleAngle != 0) {
			Vec3 ipNormVec = Vec3.createVectorHelper(ipx, ipy, ipz).normalize();
			double angle = Math.toDegrees(this.prevParticleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks);

			GL11.glTranslatef(ipx, ipy, ipz);
			GL11.glRotated(angle, ipNormVec.xCoord, ipNormVec.yCoord, ipNormVec.zCoord);
			GL11.glTranslatef(-ipx, -ipy, -ipz);
		}

		double f6;
		double f7;
		double f8;
		double f9;
		double f10 = 0.1D * particleScale;

		if (usesSheet) {
			f6 = this.particleTextureIndexX / 16.0D;
			f7 = f6 + 0.0624375D;
			f8 = this.particleTextureIndexY / 16.0D;
			f9 = f8 + 0.0624375D;
		} else {
			f7 = f9 = 1.0D;
			f6 = f8 = 0.0D;
		}

		tessellator.startDrawingQuads();
		tessellator.setBrightness(getBrightnessForRender(entityBrightness));
		tessellator.setColorRGBA_F(particleRed, particleGreen, particleBlue, particleAlpha);
		tessellator.addVertexWithUV(ipx - rx * f10 - ryz * f10, ipy - rxz * f10, ipz - rz * f10 - rxy * f10, f7, f9);
		tessellator.addVertexWithUV(ipx - rx * f10 + ryz * f10, ipy + rxz * f10, ipz - rz * f10 + rxy * f10, f7, f8);
		tessellator.addVertexWithUV(ipx + rx * f10 + ryz * f10, ipy + rxz * f10, ipz + rz * f10 + rxy * f10, f6, f8);
		tessellator.addVertexWithUV(ipx + rx * f10 - ryz * f10, ipy - rxz * f10, ipz + rz * f10 - rxy * f10, f6, f9);
		tessellator.draw();

		GL11.glPopMatrix();

		OpenGLHelper.disableBlend();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTex);
	}

	@Override
	public void setParticleTextureIndex(int p_70536_1_) {
		if (!usesSheet) {
			throw new RuntimeException("Invalid call to Particle.setMiscTex");
		} else {
			this.particleTextureIndexX = p_70536_1_ % 16;
			this.particleTextureIndexY = p_70536_1_ / 16;
		}
	}

	@Override
	public int getFXLayer() {
		return 3;
	}

	public void setColorFade(int rgb) {
		this.fadeTargetRed = (float) ((rgb & 16711680) >> 16) / 255.0F;
		this.fadeTargetGreen = (float) ((rgb & 65280) >> 8) / 255.0F;
		this.fadeTargetBlue = (float) (rgb & 255) / 255.0F;
		this.fadingColor = true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.prevParticleAngle = this.particleAngle;
		entityBrightness = worldObj.getLightBrightness((int) posX, (int) posY, (int) posZ);

		if (particleAge > particleMaxAge / 2) {
			if (fadeAway) {
				setAlphaF(1.0F - ((float) particleAge - (float) (particleMaxAge / 2)) / (float) particleMaxAge);
			}

			if (fadingColor) {
				particleRed += (fadeTargetRed - particleRed) * 0.2F;
				particleGreen += (fadeTargetGreen - particleGreen) * 0.2F;
				particleBlue += (fadeTargetBlue - particleBlue) * 0.2F;
			}
		}
	}
}
