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

		float sitting = MathHelper.clamp_float(panda.getSittingAnimationProgress(partialTicks), 0.0F, 1.0F);
		if (sitting <= 0.0F) {
			return;
		}

		GL11.glTranslatef(0.0F, 0.8F * sitting, 0.0F);
		float pitch = panda.prevRotationPitch
				+ partialTicks * (panda.rotationPitch - panda.prevRotationPitch);
		GL11.glRotatef(pitch + 90.0F * sitting, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0.0F, -1.0F * sitting, 0.0F);
	}

	@Override
	protected void renderEquippedItems(EntityLivingBase entity, float partialTicks) {
		renderEquippedItems((EntityPanda) entity, partialTicks);
	}

	protected void renderEquippedItems(EntityPanda panda, float partialTicks) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.renderEquippedItems(panda, partialTicks);

		ItemStack stack = panda.getHeldItem();
		float sitting = MathHelper.clamp_float(panda.getSittingAnimationProgress(partialTicks), 0.0F, 1.0F);
		if (stack == null || !panda.isSitting() || sitting <= 0.0F) {
			return;
		}

		float eating = panda.isEating()
				? MathHelper.clamp_float(panda.getEatingAnimationProgress(partialTicks), 0.0F, 1.0F)
				: 0.0F;
		float chew = MathHelper.sin((panda.ticksExisted + partialTicks) * 0.6F) * eating;

		GL11.glPushMatrix();
		if (mainModel.isChild) {
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			GL11.glTranslatef(0.0F, 1.5F, 0.0F);
		}

		float eatingOffset = eating > 0.0F ? eating * (0.2F + 0.2F * chew) : 0.0F;
		GL11.glTranslatef(0.1F, 1.4F - 0.09F * chew, -0.6F - eatingOffset);
		GL11.glRotatef(4.0F * chew, 0.0F, 0.0F, 1.0F);
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
