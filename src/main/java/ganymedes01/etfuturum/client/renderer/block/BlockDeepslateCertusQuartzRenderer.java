package ganymedes01.etfuturum.client.renderer.block;

import appeng.client.render.ItemRenderer;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import ganymedes01.etfuturum.compat.ExternalContent;
import ganymedes01.etfuturum.lib.RenderIDs;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import org.apache.commons.lang3.ArrayUtils;

/// Needs to be a separate renderer, because the emissive bits of the quartz vary in brightness based on location, but it also smooths out on the corners.
/// I'm too lazy to implement that myself and AE2's rendering code makes no sense to me. It's not even an ISBRH somehow.
public class BlockDeepslateCertusQuartzRenderer implements ISimpleBlockRenderingHandler, IItemRenderer {

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		Block ore = world.getBlockMetadata(x, y, z) == 1 ? ExternalContent.Blocks.AE2_CHARGED_CERTUS_QUARTZ_ORE.get() : ExternalContent.Blocks.AE2_CERTUS_QUARTZ_ORE.get();
		return renderer.renderBlockByRenderType(ore, x, y, z);
	}

	@Override
	public int getRenderId() {
		return RenderIDs.DEEPSLATE_CERTUS_QUARTZ_ORE;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return ItemRenderer.INSTANCE.handleRenderType(item, type);
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return ItemRenderer.INSTANCE.shouldUseRenderHelper(type, item, helper);
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		Item ore = Item.getItemFromBlock(item.getItemDamage() == 1 ? ExternalContent.Blocks.AE2_CHARGED_CERTUS_QUARTZ_ORE.get() : ExternalContent.Blocks.AE2_CERTUS_QUARTZ_ORE.get());
		data = ArrayUtils.add(data, item);
		ItemStack newStack = item.copy();
		newStack.func_150996_a(ore);
		ItemRenderer.INSTANCE.renderItem(type, newStack, data);
		// IDK if it's allocation spam, JIT probably will fix it but this is a bandaid for ONE item, so I do not care
		// I add the real ItemStack to the end of the args, we pass in a "fake" copy to trick AE2 into rendering using its code
		// Then our mixin checks for our inserted data, and uses that to determine what texture to display.
	}

	//unused functions

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}
}
