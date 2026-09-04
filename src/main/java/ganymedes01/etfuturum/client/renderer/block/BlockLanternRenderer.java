package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

@ThreadSafeISBRH(perThread = false)
public class BlockLanternRenderer extends BlockModelBase {

	public BlockLanternRenderer(int modelID) {
		super(modelID);
		set2DInventory();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		int i = world.getBlockMetadata(x, y, z);
		float yoffset = i % 2 == 0 ? 0 : 0.0625F;
		renderer.setRenderBounds(0, .4375F, 0, .375F, .875F, .375F);
		renderFaceXNeg(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
		renderFaceXPos(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
		renderFaceZNeg(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
		renderFaceZPos(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);

		renderer.uvRotateTop = 1;
		renderer.uvRotateBottom = 2;
		renderer.setRenderBounds(.0625F, 0, 0, .4375F, .4375F, .375F);
		renderFaceYPos(renderer, block, x, y, z, .25F, yoffset, .3125F);
		renderFaceYNeg(renderer, block, x, y, z, .25F, yoffset, .3125F);
		renderer.setRenderBounds(.125F, .5625F, .0625F, .375F, .5625F, .3125F);
		renderFaceYPos(renderer, block, x, y, z, .25F, yoffset, .3125F);
		renderer.uvRotateBottom = 0;
		renderer.uvRotateTop = 0;

		renderer.setRenderBounds(.0625F, .875F, .0625F, .3125F, 1, .3125F);
		renderFaceXNeg(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
		renderFaceXPos(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
		renderFaceZNeg(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);
		renderFaceZPos(renderer, block, x, y, z, .3125F, -.4375F + yoffset, .3125F);

		IIcon iicon = renderer.hasOverrideBlockTexture() ? renderer.overrideBlockTexture : renderer.getBlockIcon(block);
		//Lantern chain
		if (world.getBlockMetadata(x, y, z) % 2 == 0) {
			//If meta is 0, there are two crossed chain links on the top of the lantern
			renderRawDoubleSidedFace(renderer, block, x, y, z, 0.4375D, 0.5625D, 0.5625D, 0.6875D, 0.4375D, 0.5625D, 11, 10, 14, 12, iicon);
			renderRawDoubleSidedFace(renderer, block, x, y, z, 0.4375D, 0.5625D, 0.5625D, 0.6875D, 0.5625D, 0.4375D, 11, 10, 14, 12, iicon);
		} else {
			//If meta is not 0, there's only one chain link on the lantern itself, followed by two links attached above
			renderRawDoubleSidedFace(renderer, block, x, y, z, 0.4375D, 0.5625D, 0.625D, 0.75D, 0.4375D, 0.5625D, 11, 10, 14, 12, iicon);
			renderRawDoubleSidedFace(renderer, block, x, y, z, 0.5625D, 0.4375D, 0.6875D, 0.9375D, 0.4375D, 0.5625D, 11, 1, 14, 5, iicon);
			renderRawDoubleSidedFace(renderer, block, x, y, z, 0.4375D, 0.5625D, 0.875D, 1D, 0.4375D, 0.5625D, 11, 6, 14, 8, iicon);
		}

		return true;
	}

	/**
	 * Draws the lantern geometry at local origin, independent of any world/blockAccess,
	 * so it can be used for held-item rendering. The caller is responsible for binding
	 * the terrain texture and for the surrounding Tessellator batch (startDrawingQuads/draw),
	 * as well as the tessellator color and brightness.
	 *
	 * @param meta selects the chain style: an even value crosses two links on top,
	 *             an odd value extends the chain upwards (used when the lantern hangs).
	 */
	public static void drawLanternModel(RenderBlocks renderer, Block block, int meta) {
		IIcon icon = block.getIcon(0, meta);
		float yoffset = meta % 2 == 0 ? 0 : 0.0625F;

		renderer.setRenderBounds(0, .4375F, 0, .375F, .875F, .375F);
		renderer.renderFaceXNeg(block, .3125F, -.4375F + yoffset, .3125F, icon);
		renderer.renderFaceXPos(block, .3125F, -.4375F + yoffset, .3125F, icon);
		renderer.renderFaceZNeg(block, .3125F, -.4375F + yoffset, .3125F, icon);
		renderer.renderFaceZPos(block, .3125F, -.4375F + yoffset, .3125F, icon);

		renderer.uvRotateTop = 1;
		renderer.uvRotateBottom = 2;
		renderer.setRenderBounds(.0625F, 0, 0, .4375F, .4375F, .375F);
		renderer.renderFaceYPos(block, .25F, yoffset, .3125F, icon);
		renderer.renderFaceYNeg(block, .25F, yoffset, .3125F, icon);
		renderer.setRenderBounds(.125F, .5625F, .0625F, .375F, .5625F, .3125F);
		renderer.renderFaceYPos(block, .25F, yoffset, .3125F, icon);
		renderer.uvRotateBottom = 0;
		renderer.uvRotateTop = 0;

		renderer.setRenderBounds(.0625F, .875F, .0625F, .3125F, 1, .3125F);
		renderer.renderFaceXNeg(block, .3125F, -.4375F + yoffset, .3125F, icon);
		renderer.renderFaceXPos(block, .3125F, -.4375F + yoffset, .3125F, icon);
		renderer.renderFaceZNeg(block, .3125F, -.4375F + yoffset, .3125F, icon);
		renderer.renderFaceZPos(block, .3125F, -.4375F + yoffset, .3125F, icon);

		//Lantern chain
		if (meta % 2 == 0) {
			//Two crossed chain links on the top of the lantern
			drawChainQuad(0.4375D, 0.5625D, 0.5625D, 0.6875D, 0.4375D, 0.5625D, 11, 10, 14, 12, icon);
			drawChainQuad(0.4375D, 0.5625D, 0.5625D, 0.6875D, 0.5625D, 0.4375D, 11, 10, 14, 12, icon);
		} else {
			//One chain link on the lantern, followed by two links attached above
			drawChainQuad(0.4375D, 0.5625D, 0.625D, 0.75D, 0.4375D, 0.5625D, 11, 10, 14, 12, icon);
			drawChainQuad(0.5625D, 0.4375D, 0.6875D, 0.9375D, 0.4375D, 0.5625D, 11, 1, 14, 5, icon);
			drawChainQuad(0.4375D, 0.5625D, 0.875D, 1D, 0.4375D, 0.5625D, 11, 6, 14, 8, icon);
		}
	}

	/**
	 * Double-sided chain quad at local origin, mirroring renderRawDoubleSidedFace (rotate 0)
	 * but without querying blockAccess for brightness. UV arguments are in texels (0-16).
	 */
	private static void drawChainQuad(double startX, double endX, double startY, double endY,
									  double startZ, double endZ, double startU, double startV,
									  double endU, double endV, IIcon icon) {
		final Tessellator t = Tessellator.instance;
		double uStart = icon.getInterpolatedU(startU);
		double uEnd = icon.getInterpolatedU(endU);
		double vStart = icon.getInterpolatedV(startV);
		double vEnd = icon.getInterpolatedV(endV);

		//Front face
		t.addVertexWithUV(startX, startY, startZ, uEnd, vEnd);
		t.addVertexWithUV(startX, endY, startZ, uEnd, vStart);
		t.addVertexWithUV(endX, endY, endZ, uStart, vStart);
		t.addVertexWithUV(endX, startY, endZ, uStart, vEnd);

		//Back face
		t.addVertexWithUV(endX, startY, endZ, uStart, vEnd);
		t.addVertexWithUV(endX, endY, endZ, uStart, vStart);
		t.addVertexWithUV(startX, endY, startZ, uEnd, vStart);
		t.addVertexWithUV(startX, startY, startZ, uEnd, vEnd);
	}

}
