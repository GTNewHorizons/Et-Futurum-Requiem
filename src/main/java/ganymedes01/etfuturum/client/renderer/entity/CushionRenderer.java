package ganymedes01.etfuturum.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import ganymedes01.etfuturum.client.model.ModelCushion;
import ganymedes01.etfuturum.entities.EntityCushion;
import ganymedes01.etfuturum.recipes.ModRecipes;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class CushionRenderer extends Render {

	private static final ResourceLocation[] TEXTURES;
	private static final ModelCushion MODEL = new ModelCushion();

	static {
		TEXTURES = new ResourceLocation[ModRecipes.dye_names.length];
		for (int i = 0; i < ModRecipes.dye_names.length; i++) {
			TEXTURES[i] = new ResourceLocation("textures/entity/cushion/" + ModRecipes.dye_names[i] + "_cushion.png");
		}
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
		if (!(entity instanceof EntityCushion cushion)) return;

		GL11.glPushMatrix();

		GL11.glTranslated(x, y, z);
		renderCushion(cushion);
		
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}

	private void renderCushion(EntityCushion cushion) {
		bindTexture(TEXTURES[cushion.getDyeColor() % ModRecipes.dye_names.length]);
		MODEL.render();
	}
	
}
