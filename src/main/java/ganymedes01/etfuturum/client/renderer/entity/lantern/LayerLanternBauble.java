package ganymedes01.etfuturum.client.renderer.entity.lantern;

import ganymedes01.etfuturum.blocks.itemblocks.ItemBlockLantern;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

/**
 * Renders the lantern at the belt when worn in a Baubles-Expanded belt slot.
 * <p>
 * Reuses {@code RenderManager.instance.itemRenderer.renderItem} - the exact call vanilla uses for the item in a
 * player's hand ({@code RenderBiped.renderEquippedItems}) - instead of reimplementing icon/UV selection, so the
 * belt appearance matches the held item exactly.
 * <p>
 * Hooked from {@code RenderPlayerEvent.SetArmorModel}, slot 1 (legs). At that point GL has already been
 * translated to the entity's feet, rotated to the body's yaw, and mirrored via {@code glScalef(-1,-1,1)}
 * ({@code RendererLivingEntity.doRender}), so every offset/rotation below is expressed in that mirrored,
 * feet-relative space rather than plain world coordinates.
 */
public class LayerLanternBauble {

	private static final float OFFSET_X = 0.0F;
	private static final float OFFSET_Y = 0.7F;
	private static final float OFFSET_Z = -0.15F;

	/*
	 * ROTATION_X/Y/Z are solved, not guessed: ItemRenderer.renderItem's internal flat-icon tilt
	 * (glRotatef(50,0,1,0) then glRotatef(335,0,0,1), see ItemRenderer.java:127-128) rotates the drawn
	 * quad's normal from (0,0,1) to ~(0.766, 0, 0.643) and its "up" edge from (0,1,0) to ~(0.272, 0.906, -0.324).
	 * Solving for the rotation that maps those two vectors back to normal=(0,0,1) (perpendicular to the body,
	 * facing local +Z/"front") and up=(0,-1,0) (local -Y, which is world-up under this frame's X/Y mirror -
	 * see the class-level note on the SetArmorModel mirror), then decomposing that rotation into our
	 * X-then-Y-then-Z application order, gives the values below.
	 */
	private static final float ROTATION_X = -26.7F;
	private static final float ROTATION_Y = 44.0F;
	private static final float ROTATION_Z = -144.1F;

	private static final float SIZE = 0.3F;

	public static void doRenderLayer(EntityLivingBase entity, int armorSlot) {
		if (armorSlot != 1) {
			return;
		}

		ItemStack lanternStack = ItemBlockLantern.getWornBaubleLantern(entity);
		if (lanternStack == null) {
			return;
		}

		GL11.glPushMatrix();
		GL11.glTranslatef(OFFSET_X, OFFSET_Y, OFFSET_Z);
		GL11.glRotatef(ROTATION_X, 1F, 0F, 0F);
		GL11.glRotatef(ROTATION_Y, 0F, 1F, 0F);
		GL11.glRotatef(ROTATION_Z, 0F, 0F, 1F);
		GL11.glScalef(SIZE, SIZE, SIZE);

		RenderManager.instance.itemRenderer.renderItem(entity, lanternStack, 0);

		GL11.glPopMatrix();
	}

}
