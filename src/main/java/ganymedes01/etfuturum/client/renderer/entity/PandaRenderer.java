package ganymedes01.etfuturum.client.renderer.entity;

import ganymedes01.etfuturum.client.model.ModelPanda;
import ganymedes01.etfuturum.entities.EntityPanda;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class PandaRenderer extends RenderLiving {

	private static final ResourceLocation NORMAL_TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/panda/panda.png");
	private static final ResourceLocation AGGRESSIVE_TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/panda/aggressive_panda.png");
	private static final ResourceLocation LAZY_TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/panda/lazy_panda.png");
	private static final ResourceLocation PLAYFUL_TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/panda/playful_panda.png");
	private static final ResourceLocation WORRIED_TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/panda/worried_panda.png");
	private static final ResourceLocation BROWN_TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/panda/brown_panda.png");
	private static final ResourceLocation WEAK_TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/panda/weak_panda.png");

	public PandaRenderer() {
		super(new ModelPanda(), 0.9F);
	}

	@Override
	protected void rotateCorpse(EntityLivingBase entity, float animationProgress, float bodyYaw, float partialTicks) {
		rotateCorpse((EntityPanda) entity, animationProgress, bodyYaw, partialTicks);
	}

	protected void rotateCorpse(EntityPanda panda, float animationProgress, float bodyYaw, float partialTicks) {
		super.rotateCorpse(panda, animationProgress, bodyYaw, partialTicks);

		applyRollTransform(panda, partialTicks);

		float pitch = panda.prevRotationPitch
				+ partialTicks * (panda.rotationPitch - panda.prevRotationPitch);
		float sitting = MathHelper.clamp_float(panda.getSittingAnimationProgress(partialTicks), 0.0F, 1.0F);
		if (sitting > 0.0F) {
			GL11.glTranslatef(0.0F, 0.8F * sitting, 0.0F);
			GL11.glRotatef(pitch + 90.0F * sitting, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, -1.0F * sitting, 0.0F);

			if (panda.isScaredByThunderstorm()) {
				float shake = (float) (Math.cos(panda.ticksExisted * 1.25D)
						* Math.PI
						* 0.05000000074505806D);
				GL11.glRotatef(shake, 0.0F, 1.0F, 0.0F);
				if (panda.isChild()) {
					GL11.glTranslatef(0.0F, 0.8F, 0.55F);
				}
			}
		}

		float onBack = MathHelper.clamp_float(panda.getOnBackAnimationProgress(partialTicks), 0.0F, 1.0F);
		if (onBack > 0.0F) {
			float height = panda.isChild() ? 0.5F : 1.3F;
			GL11.glTranslatef(0.0F, height * onBack, 0.0F);
			GL11.glRotatef(pitch + 180.0F * onBack, 1.0F, 0.0F, 0.0F);
		}
	}

	private static void applyRollTransform(EntityPanda panda, float partialTicks) {
		int rollTicks = panda.getRollTicks();
		if (rollTicks <= 0) {
			return;
		}

		float height = panda.isChild() ? 0.3F : 0.8F;
		float currentAngle = getRollAngle(rollTicks);
		float nextAngle = getRollAngle(rollTicks + 1);
		float angle = currentAngle + partialTicks * (nextAngle - currentAngle);
		float y;

		if (rollTicks < 8) {
			y = (height + 0.2F) * angle / 90.0F;
		} else if (rollTicks < 16) {
			y = height + 0.2F + (height - 0.2F) * (angle - 90.0F) / 90.0F;
		} else if (rollTicks < 24) {
			y = height + height * (270.0F - angle) / 90.0F;
		} else {
			y = height * (360.0F - angle) / 90.0F;
		}

		GL11.glTranslatef(0.0F, y, 0.0F);
		GL11.glRotatef(-angle, 1.0F, 0.0F, 0.0F);
	}

	private static float getRollAngle(int rollTicks) {
		if (rollTicks < 8) {
			return 90.0F * rollTicks / 7.0F;
		}
		if (rollTicks < 16) {
			return 90.0F + 90.0F * (rollTicks - 8) / 7.0F;
		}
		if (rollTicks < 24) {
			return 180.0F + 90.0F * (rollTicks - 16) / 7.0F;
		}
		if (rollTicks < 32) {
			return 270.0F + 90.0F * (rollTicks - 24) / 7.0F;
		}
		return 360.0F;
	}

	@Override
	protected void renderEquippedItems(EntityLivingBase entity, float partialTicks) {
		renderEquippedItems((EntityPanda) entity, partialTicks);
	}

	protected void renderEquippedItems(EntityPanda panda, float partialTicks) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.renderEquippedItems(panda, partialTicks);

		ItemStack stack = panda.getHeldItem();
		if (stack == null || !panda.isSitting() || panda.isScaredByThunderstorm()) {
			return;
		}

		float y = 1.4F;
		float z = -0.6F;
		if (panda.isEating()) {
			float chew = MathHelper.sin((panda.ticksExisted + partialTicks) * 0.6F);
			z -= 0.2F * chew + 0.2F;
			y -= 0.09F * chew;
		}

		GL11.glPushMatrix();
		GL11.glTranslatef(0.1F, y, z);
		transformHeldItem(stack);
		renderHeldItem(panda, stack);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void transformHeldItem(ItemStack stack) {
		if (stack.getItem() instanceof ItemBlock
				&& RenderBlocks.renderItemIn3d(Block.getBlockFromItem(stack.getItem()).getRenderType())) {
			float itemScale = 0.225F;
			GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
			GL11.glScalef(itemScale, -itemScale, itemScale);
			GL11.glTranslatef(0.3F, 1.0F, 1.5F);
		} else {
			float itemScale = 0.42F;
			GL11.glScalef(itemScale, itemScale, itemScale);
			GL11.glTranslatef(0.35F, -0.15F, -0.45F);
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(25.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
		}
	}

	private void renderHeldItem(EntityPanda panda, ItemStack stack) {
		if (stack.getItem().requiresMultipleRenderPasses()) {
			for (int pass = 0; pass < stack.getItem().getRenderPasses(stack.getItemDamage()); ++pass) {
				setItemColor(stack, pass);
				renderManager.itemRenderer.renderItem(panda, stack, pass);
			}
		} else {
			setItemColor(stack, 0);
			renderManager.itemRenderer.renderItem(panda, stack, 0);
		}
	}

	private static void setItemColor(ItemStack stack, int pass) {
		int color = stack.getItem().getColorFromItemStack(stack, pass);
		float red = (float) (color >> 16 & 255) / 255.0F;
		float green = (float) (color >> 8 & 255) / 255.0F;
		float blue = (float) (color & 255) / 255.0F;
		GL11.glColor4f(red, green, blue, 1.0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		if (!(entity instanceof EntityPanda)) {
			return NORMAL_TEXTURE;
		}

		EntityPanda.Gene variant = ((EntityPanda) entity).getVariant();
		if (variant == null) {
			return NORMAL_TEXTURE;
		}

		switch (variant) {
			case AGGRESSIVE:
				return AGGRESSIVE_TEXTURE;
			case LAZY:
				return LAZY_TEXTURE;
			case PLAYFUL:
				return PLAYFUL_TEXTURE;
			case WORRIED:
				return WORRIED_TEXTURE;
			case BROWN:
				return BROWN_TEXTURE;
			case WEAK:
				return WEAK_TEXTURE;
			case NORMAL:
			default:
				return NORMAL_TEXTURE;
		}
	}
}
