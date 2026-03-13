package ganymedes01.etfuturum.client.renderer.item;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import ganymedes01.etfuturum.client.OpenGLHelper;
import ganymedes01.etfuturum.tileentities.TileEntityShulkerBox;

public class ItemShulkerBoxRenderer implements IItemRenderer {

    private final TileEntityShulkerBox box = new TileEntityShulkerBox();

    public ItemShulkerBoxRenderer() {}

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

        box.color = item.hasTagCompound() ? item.getTagCompound()
            .getByte("Color") : 0;
        box.type = TileEntityShulkerBox.ShulkerBoxType.VALUES[item.hasTagCompound() ? item.getTagCompound()
            .getByte("Type") : 0];

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
