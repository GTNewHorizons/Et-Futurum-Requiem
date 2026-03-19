package ganymedes01.etfuturum.client.renderer.block;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;

@ThreadSafeISBRH(perThread = false)
public class BlockBubbleColumnRenderer extends BlockModelBase {
	public BlockBubbleColumnRenderer(int modelID) {
		super(modelID);
		set2DInventory();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if (ForgeHooksClient.getWorldRenderPass() == 1) {
			return renderer.renderBlockLiquid(Blocks.water, x, y, z);
		}
		return false;
	}
}
