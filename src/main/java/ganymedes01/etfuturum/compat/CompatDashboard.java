package ganymedes01.etfuturum.compat;

import com.gtnewhorizons.dashboard.api.HudBounds;
import com.gtnewhorizons.dashboard.api.HudElement;
import com.gtnewhorizons.dashboard.api.HudRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

/**
 * Lets players move, resize and hide the subtitles in Dashboard's editor. The only class using Dashboard, so only call it
 * while Dashboard is loaded.
 */
@SideOnly(Side.CLIENT)
public final class CompatDashboard {

	/** Like the subtitles: rows stack up from 30 pixels above the bottom, one text line plus a pixel apart. */
	private static final int BOTTOM_ROW_CENTER = 30;
	private static final int ROW_HEIGHT = 10;
	private static final int RIGHT_MARGIN = 1;
	private static final int ROW_BACKGROUND = 0xCC000000;
	private static final String[] SAMPLE_SUBTITLES = {"Footsteps", "Zombie groans", "Chest opens"};
	/** Fade 3 seconds. */
	private static final int[] SAMPLE_BRIGHTNESS = {255, 195, 135};

	private static final HudElement SUBTITLES = new HudElement("etfuturum:subtitles", CompatDashboard::getBounds);

	private CompatDashboard() {
	}

	public static void register() {
		SUBTITLES.setPreview((screenWidth, screenHeight) -> HudRegistry.render(SUBTITLES, () -> drawSample(screenWidth, screenHeight)));
		HudRegistry.register(SUBTITLES);
	}

	/** False means the player hid the subtitles, so skip drawing them and do not call {@link #endSubtitles()}. */
	public static boolean beginSubtitles() {
		return HudRegistry.beginRender(SUBTITLES);
	}

	public static void endSubtitles() {
		HudRegistry.endRender();
	}

	/** As wide as the sample rows and right aligned like them, from the top row to the bottom one. */
	private static HudBounds getBounds(int screenWidth, int screenHeight) {
		int width = getRowWidth() + 2;
		int bottom = screenHeight - BOTTOM_ROW_CENTER + ROW_HEIGHT / 2;
		int height = SAMPLE_SUBTITLES.length * ROW_HEIGHT;
		return new HudBounds(screenWidth - RIGHT_MARGIN - width, bottom - height, width, height);
	}

	/** The widest sample, with room for an arrow on each side. */
	private static int getRowWidth() {
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		int widest = 0;
		for (String subtitle : SAMPLE_SUBTITLES) {
			widest = Math.max(widest, font.getStringWidth(subtitle));
		}
		return widest + font.getStringWidth("< ") + font.getStringWidth("> ");
	}

	/** The newest subtitle at the bottom, with arrows pointing where the sounds come from. */
	private static void drawSample(int screenWidth, int screenHeight) {
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		HudBounds bounds = getBounds(screenWidth, screenHeight);
		int centerX = bounds.x + bounds.width / 2;
		int halfWidth = bounds.width / 2 - 1;
		for (int row = 0; row < SAMPLE_SUBTITLES.length; row++) {
			int top = bounds.getBottom() - (row + 1) * ROW_HEIGHT;
			Gui.drawRect(bounds.x, top, bounds.getRight(), top + ROW_HEIGHT, ROW_BACKGROUND);
			int brightness = SAMPLE_BRIGHTNESS[row];
			int color = 0xFF000000 | brightness << 16 | brightness << 8 | brightness;
			String subtitle = SAMPLE_SUBTITLES[row];
			int textY = top + 1;
			font.drawString(subtitle, centerX - font.getStringWidth(subtitle) / 2, textY, color);
			if (row == 1) {
				font.drawString("<", centerX - halfWidth, textY, color);
			} else if (row == 2) {
				font.drawString(">", centerX + halfWidth - font.getStringWidth(">"), textY, color);
			}
		}
	}
}
