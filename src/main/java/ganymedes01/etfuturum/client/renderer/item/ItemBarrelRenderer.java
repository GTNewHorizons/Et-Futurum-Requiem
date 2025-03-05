package ganymedes01.etfuturum.client.renderer.item;

import ganymedes01.etfuturum.client.OpenGLHelper;
import ganymedes01.etfuturum.tileentities.TileEntityBarrel;
import ganymedes01.etfuturum.tileentities.TileEntityShulkerBox;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class ItemBarrelRenderer implements IItemRenderer {

	private final TileEntityBarrel box = new TileEntityBarrel();

	public ItemBarrelRenderer() {
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type != ItemRenderType.FIRST_PERSON_MAP;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		box.type = TileEntityBarrel.BarrelType.VALUES[item.hasTagCompound() ? item.getTagCompound().getByte("Type") : 0];

		OpenGLHelper.pushMatrix();

		switch (type) {
			case ENTITY:
				renderShulkerBox(-0.5F, -0.5F, -0.5F, 0, 1);
				break;
			case EQUIPPED:
				renderShulkerBox(0, 0, 0, 0, 1);
				break;
			case EQUIPPED_FIRST_PERSON:
				renderShulkerBox(0, 0, 0, 0, 1);
				break;
			case INVENTORY:
				renderShulkerBox(-0.5F, -0.5F, -0.5F, 0, 1);
				break;
			default:
				break;
		}

		OpenGLHelper.popMatrix();
	}


	private void renderShulkerBox(float x, float y, float z, float angle, float scale) {
		OpenGLHelper.rotate(angle, 0, 1, 0);
		OpenGLHelper.scale(scale, scale, scale);
		OpenGLHelper.translate(x, y, z);

		TileEntityRendererDispatcher.instance.renderTileEntityAt(box, 0, 0, 0, 0);
	}
}
