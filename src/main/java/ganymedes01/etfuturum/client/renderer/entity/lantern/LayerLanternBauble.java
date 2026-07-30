package ganymedes01.etfuturum.client.renderer.entity.lantern;

import ganymedes01.etfuturum.blocks.itemblocks.ItemBlockLantern;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

/**
 * Renders the lantern at the belt when worn in a Baubles-Expanded belt slot.
 * <p>
 * Rather than reimplementing icon/UV selection, this calls {@code RenderManager.instance.itemRenderer.renderItem}
 * directly - the exact same call vanilla makes for the item in a player's hand. Traced in the decompiled sources
 * ({@code RenderBiped.renderEquippedItems}, lines ~250-336 of build/rfg/minecraft-src): for an ItemBlock whose
 * render type isn't one of vanilla's fixed 3D cases ({@code RenderBlocks.renderItemIn3d}, which our lantern's
 * custom render ID never matches), the hand-rendering path skips straight to
 * {@code renderManager.itemRenderer.renderItem(entity, stack, 0)} with no extra scale/rotate - so calling that
 * same method here reproduces the in-hand look exactly, whatever it is, without us re-deriving it.
 * <p>
 * Hooked from {@code RenderPlayerEvent.SetArmorModel}, slot 1 (legs). Traced against
 * {@code RendererLivingEntity.doRender} (lines ~142-173): by the time this event fires, GL has already been
 * translated to the entity's feet in world space, rotated to the body's yaw, then hit
 * {@code glScalef(-1,-1,1)} followed by {@code glTranslatef(0, -24*0.0625 - 0.0078125, 0)} (~-1.508 in the
 * already-mirrored frame). Composing that out: the event's local origin sits ~1.508 world blocks above the
 * feet - roughly head/eye height, not the belt - and every axis drawn here inherits that X/Y mirror:
 * - local +Y moves DOWN in world space (mirror cancels the earlier upward shift); ~0.6 reaches belt height.
 * - local X is mirrored, landing consistently on one hip once rotated to the player's facing - confirmed
 * correct (side + height) from an earlier screenshot, kept as-is.
 * - local Z is not mirrored; the lantern appeared behind the character with +0.15, so it's flipped to -0.15.
 * <p>
 * {@code SIZE} is left at 1.0 (vanilla's own in-hand call has no extra outer scale for this code path), so the
 * lantern should already be the same size as when held. {@code ROTATION_Y} is the one knob that's still a pure
 * guess - the in-hand call is normally wrapped in an arm-attached transform we're not replicating, so which way
 * the icon "faces" at the belt is unverified.
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
