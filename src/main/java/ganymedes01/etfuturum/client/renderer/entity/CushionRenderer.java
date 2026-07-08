package ganymedes01.etfuturum.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import ganymedes01.etfuturum.entities.EntityCushion;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
// import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class CushionRenderer extends Render {

	private final RenderBlocks renderBlocks = new RenderBlocks();
	// private IIcon texture;

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
		this.renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		Block block = Blocks.planks;

		GL11.glPushMatrix();
		this.renderBlocks.overrideBlockBounds(0.0D, 0.5D, 0.0D, 1.0D, 0.75D, 1.0D);
		// this.renderBlocks.setOverrideBlockTexture(this.field_94147_f);
		this.renderBlocks.renderBlockAsItem(block, 0, 1.0F);
		// this.renderBlocks.clearOverrideBlockTexture();
		this.renderBlocks.unlockBlockBounds();
		GL11.glPopMatrix();
	}
	
}
