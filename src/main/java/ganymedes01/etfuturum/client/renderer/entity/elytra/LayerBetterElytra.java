/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * <p>
 * File Created @ [21/03/2016, 00:14:23 (GMT)]
 */
package ganymedes01.etfuturum.client.renderer.entity.elytra;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ganymedes01.etfuturum.items.equipment.ItemArmorElytra;

public class LayerBetterElytra {

    private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");
    private static final ModelElytra modelElytra = new ModelElytra();
    protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation(
        "textures/misc/enchanted_item_glint.png");
    public static float netHeadYaw = 0.0F;
    public static float headPitch = 0.0F;

    public static void doRenderLayer(EntityLivingBase entityIn, float limbSwing, float limbSwingAmount,
        float partialTicks, float ageInTicks, float scale) {
        ItemStack itemstack = ItemArmorElytra.getElytra(entityIn);
        if (entityIn instanceof AbstractClientPlayer player && itemstack != null) {
            GL11.glPushAttrib(-1);
            /*
             * int colorIndex =
             * itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("backlytra:elytraDye",
             * NBT.TAG_ANY_NUMERIC)
             * ? itemstack.getTagCompound().getInteger("backlytra:elytraDye")
             * : -1;
             * if (colorIndex < 0 || colorIndex == 15)
             * GL11.glColor3f(1, 1, 1);
             * else {
             * Color color = new Color(ItemDye.dyeColors[colorIndex]);
             * float r = color.getRed() / 255F;
             * float g = color.getGreen() / 255F;
             * float b = color.getBlue() / 255F;
             * GL11.glColor3f(r, g, b);
             * }
             */
            // GL11.glColor3f(1, 1, 1);

            Minecraft.getMinecraft().renderEngine
                .bindTexture(player.func_152122_n()/* hasCape */ ? player.getLocationCape() : TEXTURE_ELYTRA);

            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, 0.125F);
            modelElytra
                .setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
            modelElytra.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            if (itemstack.isItemEnchanted()) renderGlint(
                entityIn,
                modelElytra,
                limbSwing,
                limbSwingAmount,
                partialTicks,
                ageInTicks,
                netHeadYaw,
                headPitch,
                scale);

            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private static void renderGlint(EntityLivingBase entitylivingbaseIn, ModelElytra modelbaseIn, float p_177183_3_,
        float p_177183_4_, float partialTicks, float p_177183_6_, float p_177183_7_, float p_177183_8_, float scale) {
        float f = entitylivingbaseIn.ticksExisted + partialTicks;
        Minecraft.getMinecraft().renderEngine.bindTexture(ENCHANTED_ITEM_GLINT_RES);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDepthMask(false);
        float f1 = 0.5F;
        GL11.glColor4f(f1, f1, f1, 1.0F);

        for (int i = 0; i < 2; ++i) {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            float f2 = 0.76F;
            GL11.glColor4f(0.5F * f2, 0.25F * f2, 0.8F * f2, 1.0F);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glLoadIdentity();
            float f3 = 0.33333334F;
            GL11.glScalef(f3, f3, f3);
            GL11.glRotatef(30.0F - i * 60.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(0.0F, f * (0.001F + i * 0.003F) * 20.0F, 0.0F);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            modelbaseIn
                .render(entitylivingbaseIn, p_177183_3_, p_177183_4_, p_177183_6_, p_177183_7_, p_177183_8_, scale);
        }

        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthMask(true);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
