package ganymedes01.etfuturum.client.renderer.item;

import ganymedes01.etfuturum.client.renderer.block.BlockLanternRenderer;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Renders the lantern as a 3D model while held in hand, hanging from the fist by its
 * chain. Inventory icons and dropped items keep the default flat sprite, since this
 * renderer only handles the equipped render types.
 */
public class ItemLanternRenderer implements IItemRenderer {

	// Chain style that extends the links upwards, so the chain reads as running into the fist.
	private static final int HANGING_META = 1;

	// Temporary: draws RGB axis gizmos (X red, Y green, Z blue) to visualise the transform frames.
	private static final boolean DEBUG_AXES = false;

	/**
	 * Right arm pitch used by MixinModelBiped when holding a lantern: nearly horizontal,
	 * pointing forward. Defined here (a normal class) rather than in the mixin, since mixins
	 * may not expose non-private static fields. The renderer undoes this rotation so the
	 * lantern hangs vertically instead of following the extended arm.
	 */
	public static final float LANTERN_ARM_PITCH = -((float) Math.PI / 2F) + 0.15F;
	private final RenderBlocks renderBlocks = new RenderBlocks();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		Block block = Block.getBlockFromItem(item.getItem());
		if (block == null) {
			return;
		}

		TextureManager tm = Minecraft.getMinecraft().getTextureManager();
		ResourceLocation terrain = tm.getResourceLocation(0);
		tm.bindTexture(terrain);

		// When the arm-pose mixin is active, the third person arm is rotated to near
		// horizontal, so the attached lantern must be counter-rotated to hang vertically.
		boolean armPosed = ConfigMixins.heldLanternPose;

		GL11.glPushMatrix();

		// Position the model in the hand. These offsets are pure visual tuning;
		// adjust in-game if the hanging pose looks off.
		switch (type) {
			case EQUIPPED_FIRST_PERSON:
				// First person is view space (ItemRenderer + ForgeHooksClient), not the posed arm,
				// so it needs its own placement. +Y is up here; no un-tilting is required.
				if (DEBUG_AXES) {
					drawDebugAxes(2.0F);
				}
				GL11.glScalef(0.6F, 0.6F, 0.6F);
				GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
				break;
			case EQUIPPED:
			default:
				if (armPosed) {
					// Un-tilt the lantern to world orientation while keeping the origin at the hand.
					// Only the rotational part of vanilla's "hold a block" transform and our arm pose
					// are undone (no translations), so the origin stays where vanilla put the item:
					// in the hand. glScalef(-1,-1,1) cancels the sign of vanilla's scale(-0.375,-0.375,0.375),
					// leaving a positive 0.375 scale; the rotations then cancel to world orientation.
					float armDeg = (float) Math.toDegrees(LANTERN_ARM_PITCH);
					GL11.glScalef(-1.0F, -1.0F, 1.0F);
					GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
					GL11.glRotatef(-armDeg, 1.0F, 0.0F, 0.0F);

					// Origin is now the hand, axes world-aligned (X right, Y up, Z forward), scale 0.375.
					if (DEBUG_AXES) {
						drawDebugAxes(2.0F);
					}

					// Enlarge, flip so the chain points up (model +Y maps to world-down here),
					// then hang the body below the hand.
					GL11.glScalef(2.0F, 2.0F, 2.0F);
					GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
					GL11.glTranslatef(-0.5F, -1.6F, 0F);
				} else {
					GL11.glTranslatef(0.4F, 0.1F, 0.4F);
					GL11.glScalef(0.8F, 0.8F, 0.8F);
					GL11.glTranslatef(0.0F, -0.5F, 0.0F);
				}
				break;
		}

		boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_LIGHTING);

		// The lantern is a light source, so render it uniformly full-bright.
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		t.setBrightness(0x00f000f0);
		BlockLanternRenderer.drawLanternModel(renderBlocks, block, HANGING_META);
		t.draw();

		if (lighting) {
			GL11.glEnable(GL11.GL_LIGHTING);
		}

		GL11.glPopMatrix();
	}

	/**
	 * Temporary debug helper: draws a coloured axis gizmo at the current origin.
	 * +X is red, +Y is green, +Z is blue. Does not disturb the surrounding matrix.
	 */
	private static void drawDebugAxes(float length) {
		boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
		boolean texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glLineWidth(4.0F);

		Tessellator t = Tessellator.instance;
		t.startDrawing(GL11.GL_LINES);
		t.setColorOpaque_I(0xFF0000); // +X red
		t.addVertex(0.0D, 0.0D, 0.0D);
		t.addVertex(length, 0.0D, 0.0D);
		t.setColorOpaque_I(0x00FF00); // +Y green
		t.addVertex(0.0D, 0.0D, 0.0D);
		t.addVertex(0.0D, length, 0.0D);
		t.setColorOpaque_I(0x0000FF); // +Z blue
		t.addVertex(0.0D, 0.0D, 0.0D);
		t.addVertex(0.0D, 0.0D, length);
		t.draw();

		if (texture) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		if (lighting) {
			GL11.glEnable(GL11.GL_LIGHTING);
		}
	}
}
