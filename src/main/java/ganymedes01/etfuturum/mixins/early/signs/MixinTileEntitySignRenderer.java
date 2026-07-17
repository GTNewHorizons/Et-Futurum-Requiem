package ganymedes01.etfuturum.mixins.early.signs;

import ganymedes01.etfuturum.ducks.IWaxableSign;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.lwjgl.opengl.GL11;

/**
 * Render back text on signs
 * 
 * @author mosesyu1028
 */
@Mixin(TileEntitySignRenderer.class)
public class MixinTileEntitySignRenderer {

	@Inject(method = "renderTileEntityAt(Lnet/minecraft/tileentity/TileEntity;DDDF)V", at = @At("TAIL"))
	private void renderBackText(TileEntity te, double x, double y, double z, float partialTicks, CallbackInfo ci) {
        if (!(te instanceof IWaxableSign))
			return;
		TileEntitySign sign = (TileEntitySign) te;
		String[] backText = ((IWaxableSign) sign).getSignText(false);
		boolean standing = !((IWaxableSign) sign).isWallSign(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);

		GL11.glPushMatrix();
		float f1 = 0.6666667F;

		if (standing) {
			GL11.glTranslatef((float) x + 0.5F, (float) y + 0.75F * f1, (float) z + 0.5F);
			float f2 = te.getBlockMetadata() * 360 / 16.0F;
			GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
		} else {
			int meta = te.getBlockMetadata();
			float f3 = 0.0F;
			if (meta == 2) f3 = 180.0F;
			if (meta == 4) f3 = 90.0F;
			if (meta == 5) f3 = -90.0F;
			GL11.glTranslatef((float) x + 0.5F, (float) y + 0.75F * f1, (float) z + 0.5F);
			GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
		}

		// Back face text
		float f3 = 0.016666668F * f1;
		GL11.glTranslatef(0.0F, 0.5F * f1, -0.07F * f1);
		GL11.glScalef(f3, -f3, f3);
		GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
		GL11.glNormal3f(0.0F, 0.0F, -1.0F * f3);
		GL11.glDepthMask(false);

		FontRenderer fontrenderer = ((TileEntitySignRenderer) (Object) this).func_147498_b();
		byte b0 = 0;
		for (int i = 0; i < backText.length; ++i) {
			String s = backText[i];
			if (i == sign.lineBeingEdited) {
				s = "> " + s + "§r <";
			}
			fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, i * 10 - backText.length * 5, b0);
		}

		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}
}
