package ganymedes01.etfuturum.client.gui.inventory;

import com.gtnewhorizon.gtnhlib.util.font.FontRendering;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ducks.IWaxableSign;
import ganymedes01.etfuturum.network.SignTextUpdateMessage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiEditWoodSign extends GuiScreen {

	/**
	 * Reference to the sign object.
	 */
	private final TileEntitySign tileSign;
	/**
	 * Whether the front of the sign is being edited.
	 */
	private final boolean front;
	/** 
	 * Get the text for the side of the sign currently being edited.
	 */
	private final String[] editingText;
	/**
	 * Counts the number of screen updates.
	 */
	private int updateCounter;
	/**
	 * The index of the line that is being edited.
	 */
	private int editLine;
	/**
	 * "Done" button for the GUI.
	 */
	private GuiButton doneBtn;

	public GuiEditWoodSign(TileEntitySign tileSign, boolean front) {
		this.tileSign = tileSign;
		this.front = front;
		this.editingText = ((IWaxableSign) tileSign).getSignText(front);
	}

	@Override
	public void initGui() {
		this.buttonList.clear();
		Keyboard.enableRepeatEvents(true);
		this.buttonList.add(this.doneBtn = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, I18n.format("gui.done")));
		this.tileSign.setEditable(false);
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		NetHandlerPlayClient nethandlerplayclient = this.mc.getNetHandler();

		if (nethandlerplayclient != null) {
			// Update client TE immediately (don't wait for server relay)
			System.arraycopy(this.editingText, 0, ((IWaxableSign) this.tileSign).getSignText(this.front), 0, 4);
			this.tileSign.getWorldObj().func_147479_m(this.tileSign.xCoord, this.tileSign.yCoord, this.tileSign.zCoord);
			// Sync to server for persistence + other players
			EtFuturum.networkWrapper.sendToServer(new SignTextUpdateMessage(
					this.tileSign.xCoord, this.tileSign.yCoord, this.tileSign.zCoord,
					this.front, this.editingText));
		}

		this.tileSign.setEditable(true);
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen() {
		++this.updateCounter;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button.id == 0) {
				this.tileSign.markDirty();
				this.mc.displayGuiScreen(null);
			}
		}
	}

	/**
	 * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		if (keyCode == 200) {
			this.editLine = this.editLine - 1 & 3;
		}

		if (keyCode == 208 || keyCode == 28 || keyCode == 156) {
			this.editLine = this.editLine + 1 & 3;
		}

		if (keyCode == 14 && this.editingText[this.editLine].length() > 0) {
			this.editingText[this.editLine] = this.editingText[this.editLine].substring(0, this.editingText[this.editLine].length() - 1);
		}

		if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && this.editingText[this.editLine].length() < 90 && FontRendering.countVisibleChars(this.editingText[this.editLine]) < 15) {
			this.editingText[this.editLine] = this.editingText[this.editLine] + typedChar;
		}

		if (keyCode == 1) {
			this.actionPerformed(this.doneBtn);
		}
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (!(tileSign.getBlockType() instanceof net.minecraft.block.BlockSign))
			return;
		this.drawDefaultBackground();
		String label = front ? I18n.format("sign.edit") : I18n.format("sign.edit.back");
		this.drawCenteredString(this.fontRendererObj, label, this.width / 2, 40, 16777215);
		GL11.glPushMatrix();
		GL11.glTranslatef(this.width / 2, 0.0F, 50.0F);
		float f1 = 93.75F;
		GL11.glScalef(-f1, -f1, -f1);
		GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(0.0F, -1.0625F, 0.0F);

		if (this.updateCounter / 6 % 2 == 0) {
			this.tileSign.lineBeingEdited = this.editLine;
		}

		// Temporarily swap text arrays so the TESR renders the side we're editing
		String[] realText = this.tileSign.signText;
		if (!front) {
			this.tileSign.signText = this.editingText;
		}

		int realMeta = this.tileSign.blockMetadata;
		this.tileSign.blockMetadata = 0;
		TileEntityRendererDispatcher.instance.renderTileEntityAt(this.tileSign, -0.5D, -0.75D, -0.5D, 0.0F);
		this.tileSign.blockMetadata = realMeta;

		if (!front) {
			this.tileSign.signText = realText;
		}

		this.tileSign.lineBeingEdited = -1;
		GL11.glPopMatrix();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
