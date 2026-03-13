package ganymedes01.etfuturum.client.renderer.entity;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ganymedes01.etfuturum.client.model.ModelShulker;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.entities.EntityShulker;

public class ShulkerRenderer extends RenderLiving {

    public static final ResourceLocation[] SHULKER_ENDERGOLEM_TEXTURES = new ResourceLocation[] {
        new ResourceLocation("textures/entity/shulker/shulker.png"),
        new ResourceLocation("textures/entity/shulker/shulker_white.png"),
        new ResourceLocation("textures/entity/shulker/shulker_orange.png"),
        new ResourceLocation("textures/entity/shulker/shulker_magenta.png"),
        new ResourceLocation("textures/entity/shulker/shulker_light_blue.png"),
        new ResourceLocation("textures/entity/shulker/shulker_yellow.png"),
        new ResourceLocation("textures/entity/shulker/shulker_lime.png"),
        new ResourceLocation("textures/entity/shulker/shulker_pink.png"),
        new ResourceLocation("textures/entity/shulker/shulker_gray.png"),
        new ResourceLocation("textures/entity/shulker/shulker_light_gray.png"),
        new ResourceLocation("textures/entity/shulker/shulker_cyan.png"),
        new ResourceLocation("textures/entity/shulker/shulker_purple.png"),
        new ResourceLocation("textures/entity/shulker/shulker_blue.png"),
        new ResourceLocation("textures/entity/shulker/shulker_brown.png"),
        new ResourceLocation("textures/entity/shulker/shulker_green.png"),
        new ResourceLocation("textures/entity/shulker/shulker_red.png"),
        new ResourceLocation("textures/entity/shulker/shulker_black.png"),

        new ResourceLocation("ironshulkerbox:textures/model/vanilla/shulker_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/white/shulker_white_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/orange/shulker_orange_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/magenta/shulker_magenta_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/light_blue/shulker_light_blue_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/yellow/shulker_yellow_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/lime/shulker_lime_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/pink/shulker_pink_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/gray/shulker_gray_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/light_gray/shulker_light_gray_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/cyan/shulker_cyan_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/purple/shulker_purple_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/blue/shulker_blue_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/brown/shulker_brown_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/green/shulker_green_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/red/shulker_red_iron.png"),
        new ResourceLocation("ironshulkerbox:textures/model/black/shulker_black_iron.png"),

        new ResourceLocation("ironshulkerbox:textures/model/vanilla/shulker_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/white/shulker_white_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/orange/shulker_orange_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/magenta/shulker_magenta_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/light_blue/shulker_light_blue_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/yellow/shulker_yellow_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/lime/shulker_lime_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/pink/shulker_pink_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/gray/shulker_gray_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/light_gray/shulker_light_gray_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/cyan/shulker_cyan_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/purple/shulker_purple_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/blue/shulker_blue_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/brown/shulker_brown_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/green/shulker_green_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/red/shulker_red_gold.png"),
        new ResourceLocation("ironshulkerbox:textures/model/black/shulker_black_gold.png"),

        new ResourceLocation("ironshulkerbox:textures/model/vanilla/shulker_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/white/shulker_white_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/orange/shulker_orange_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/magenta/shulker_magenta_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/light_blue/shulker_light_blue_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/yellow/shulker_yellow_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/lime/shulker_lime_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/pink/shulker_pink_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/gray/shulker_gray_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/light_gray/shulker_light_gray_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/cyan/shulker_cyan_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/purple/shulker_purple_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/blue/shulker_blue_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/brown/shulker_brown_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/green/shulker_green_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/red/shulker_red_diamond.png"),
        new ResourceLocation("ironshulkerbox:textures/model/black/shulker_black_diamond.png"),

        new ResourceLocation("ironshulkerbox:textures/model/vanilla/shulker_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/white/shulker_white_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/orange/shulker_orange_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/magenta/shulker_magenta_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/light_blue/shulker_light_blue_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/yellow/shulker_yellow_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/lime/shulker_lime_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/pink/shulker_pink_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/gray/shulker_gray_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/light_gray/shulker_light_gray_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/cyan/shulker_cyan_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/purple/shulker_purple_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/blue/shulker_blue_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/brown/shulker_brown_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/green/shulker_green_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/red/shulker_red_copper.png"),
        new ResourceLocation("ironshulkerbox:textures/model/black/shulker_black_copper.png"),

        new ResourceLocation("ironshulkerbox:textures/model/vanilla/shulker_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/white/shulker_white_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/orange/shulker_orange_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/magenta/shulker_magenta_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/light_blue/shulker_light_blue_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/yellow/shulker_yellow_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/lime/shulker_lime_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/pink/shulker_pink_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/gray/shulker_gray_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/light_gray/shulker_light_gray_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/cyan/shulker_cyan_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/purple/shulker_purple_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/blue/shulker_blue_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/brown/shulker_brown_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/green/shulker_green_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/red/shulker_red_silver.png"),
        new ResourceLocation("ironshulkerbox:textures/model/black/shulker_black_silver.png"),

        new ResourceLocation("ironshulkerbox:textures/model/vanilla/shulker_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/white/shulker_white_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/orange/shulker_orange_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/magenta/shulker_magenta_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/light_blue/shulker_light_blue_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/yellow/shulker_yellow_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/lime/shulker_lime_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/pink/shulker_pink_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/gray/shulker_gray_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/light_gray/shulker_light_gray_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/cyan/shulker_cyan_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/purple/shulker_purple_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/blue/shulker_blue_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/brown/shulker_brown_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/green/shulker_green_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/red/shulker_red_crystal.png"),
        new ResourceLocation("ironshulkerbox:textures/model/black/shulker_black_crystal.png"),

        new ResourceLocation("ironshulkerbox:textures/model/vanilla/shulker_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/white/shulker_white_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/orange/shulker_orange_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/magenta/shulker_magenta_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/light_blue/shulker_light_blue_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/yellow/shulker_yellow_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/lime/shulker_lime_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/pink/shulker_pink_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/gray/shulker_gray_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/light_gray/shulker_light_gray_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/cyan/shulker_cyan_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/purple/shulker_purple_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/blue/shulker_blue_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/brown/shulker_brown_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/green/shulker_green_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/red/shulker_red_obsidian.png"),
        new ResourceLocation("ironshulkerbox:textures/model/black/shulker_black_obsidian.png") };

    public ShulkerRenderer() {
        super(new ModelShulker(), 1);
        // this.addLayer(new ShulkerRenderer.HeadLayer());
        // this.modelVersion = p_i1262_1_.getModelVersion();
        this.shadowSize = 0.0F;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.doRender((EntityShulker) entity, x, y, z, entityYaw, partialTicks);
    }

    public void doRender(EntityShulker entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // if (this.modelVersion != ((ModelShulker)this.mainModel).getModelVersion())
        // {
        // this.mainModel = new ModelShulker();
        // this.modelVersion = ((ModelShulker)this.mainModel).getModelVersion();
        // }

        int i = entity.getClientTeleportInterp();

        if (i > 0 && entity.isAttachedToBlock()) {
            BlockPos blockpos = entity.getAttachmentPos();
            BlockPos blockpos1 = entity.getOldAttachPos();
            double d0 = (double) ((float) i - partialTicks) / 6.0D;
            d0 = d0 * d0;
            double d1 = (double) (blockpos.getX() - blockpos1.getX()) * d0;
            double d2 = (double) (blockpos.getY() - blockpos1.getY()) * d0;
            double d3 = (double) (blockpos.getZ() - blockpos1.getZ()) * d0;
            super.doRender(entity, x - d1, y - d2, z - d3, entityYaw, partialTicks);
        } else {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    @Override
    protected void renderModel(EntityLivingBase p_77036_1_, float p_77036_2_, float p_77036_3_, float p_77036_4_,
        float p_77036_5_, float p_77036_6_, float p_77036_7_) {
        super.renderModel(p_77036_1_, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);

        GL11.glPushMatrix();

        switch (((EntityShulker) p_77036_1_).getAttachmentFacing()) {
            case DOWN:
            default:
                break;
            // Flipped East and West to account for incorrect mappings
            case WEST:
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(1.0F, -1.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                break;

            case EAST:
                GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                break;

            case NORTH:
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, -1.0F);
                break;

            case SOUTH:
                GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 1.0F);
                break;

            case UP:
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -2.0F, 0.0F);
        }

        EntityShulker shulker = (EntityShulker) p_77036_1_;

        if (!shulker.isClosed() || shulker.getClientPeekAmount(1) != 0 || shulker.isInvisible()) {
            ModelRenderer modelrenderer = ((ModelShulker) mainModel).head;
            modelrenderer.rotateAngleY = p_77036_5_ * 0.017453292F;
            modelrenderer.rotateAngleX = p_77036_6_ * 0.017453292F;
            bindTexture(getEntityTexture(shulker));
            modelrenderer.render(p_77036_7_);
            if (p_77036_1_.hurtTime > 0 || p_77036_1_.deathTime > 0) {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDepthFunc(GL11.GL_EQUAL);
                float f14 = shulker.getBrightness(1);
                GL11.glColor4f(f14, 0.0F, 0.0F, 0.4F);
                modelrenderer.render(p_77036_7_);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
        }

        GL11.glPopMatrix();
    }

    // @Override
    // public boolean shouldRender(EntityLiving livingEntity, ICamera camera, double camX, double camY, double camZ)
    // {
    // this.shouldRender((EntityShulker)livingEntity, camera, camX, camY, camZ);
    // }
    //
    // public boolean shouldRender(EntityShulker livingEntity, ICamera camera, double camX, double camY, double camZ)
    // {
    // if (super.shouldRender(livingEntity, camera, camX, camY, camZ))
    // {
    // return true;
    // }
    // else
    // {
    // if (livingEntity.getClientTeleportInterp() > 0 && livingEntity.isAttachedToBlock())
    // {
    // BlockPos blockpos = livingEntity.getOldAttachPos();
    // BlockPos blockpos1 = livingEntity.getAttachmentPos();
    // Vec3 vec3d = Vec3.createVectorHelper((double)blockpos1.getX(), (double)blockpos1.getY(),
    // (double)blockpos1.getZ());
    // Vec3 vec3d1 = Vec3.createVectorHelper((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
    //
    // if (camera.isBoundingBoxInFrustum(AxisAlignedBB.getBoundingBox(vec3d1.xCoord, vec3d1.yCoord, vec3d1.zCoord,
    // vec3d.xCoord, vec3d.yCoord, vec3d.zCoord)))
    // {
    // return true;
    // }
    // }
    //
    // return false;
    // }
    // }

    @Override
    protected void rotateCorpse(EntityLivingBase entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks) {
        this.rotateCorpse((EntityShulker) entityLiving, p_77043_2_, p_77043_3_, partialTicks);
    }

    protected void rotateCorpse(EntityShulker entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks) {
        super.rotateCorpse(entityLiving, p_77043_2_, p_77043_3_, partialTicks);

        switch (entityLiving.getAttachmentFacing()) {
            case DOWN:
            default:
                break;
            // Flipped East and West to account for incorrect mappings
            case WEST:
                GL11.glTranslatef(0.5F, 0.5F, 0.0F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                break;

            case EAST:
                GL11.glTranslatef(-0.5F, 0.5F, 0.0F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                break;

            case NORTH:
                GL11.glTranslatef(0.0F, 0.5F, -0.5F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                break;

            case SOUTH:
                GL11.glTranslatef(0.0F, 0.5F, 0.5F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                break;

            case UP:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        }
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime) {
        GL11.glScalef(0.999F, 0.999F, 0.999F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        byte color = ((EntityShulker) p_110775_1_).getColor();
        return ShulkerRenderer.SHULKER_ENDERGOLEM_TEXTURES[color == 16 ? 0 : color + 1];
    }
}
