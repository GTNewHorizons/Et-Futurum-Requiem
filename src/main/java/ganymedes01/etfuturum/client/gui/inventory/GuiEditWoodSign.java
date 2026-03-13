package ganymedes01.etfuturum.client.gui.inventory;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import ganymedes01.etfuturum.blocks.BlockWoodSign;

public class GuiEditWoodSign extends GuiScreen {

    /**
     * Reference to the sign object.
     */
    private final TileEntitySign tileSign;
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

    public GuiEditWoodSign(TileEntitySign p_i1097_1_) {
        this.tileSign = p_i1097_1_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.buttonList
            .add(this.doneBtn = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, I18n.format("gui.done")));
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
            nethandlerplayclient.addToSendQueue(
                new C12PacketUpdateSign(
                    this.tileSign.xCoord,
                    this.tileSign.yCoord,
                    this.tileSign.zCoord,
                    this.tileSign.signText));
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

        if (keyCode == 14 && this.tileSign.signText[this.editLine].length() > 0) {
            this.tileSign.signText[this.editLine] = this.tileSign.signText[this.editLine]
                .substring(0, this.tileSign.signText[this.editLine].length() - 1);
        }

        if (ChatAllowedCharacters.isAllowedCharacter(typedChar)
            && this.tileSign.signText[this.editLine].length() < 15) {
            this.tileSign.signText[this.editLine] = this.tileSign.signText[this.editLine] + typedChar;
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
        if (!(tileSign.getBlockType() instanceof BlockWoodSign)) return;
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("sign.edit"), this.width / 2, 40, 16777215);
        GL11.glPushMatrix();
        GL11.glTranslatef(this.width / 2, 0.0F, 50.0F);
        float f1 = 93.75F;
        GL11.glScalef(-f1, -f1, -f1);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        BlockWoodSign block = (BlockWoodSign) this.tileSign.getBlockType();

        if (block.standing) {
            float f2 = this.tileSign.getBlockMetadata() * 360 / 16.0F;
            GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, -1.0625F, 0.0F);
        } else {
            int k = this.tileSign.getBlockMetadata();
            float f3 = 0.0F;

            if (k == 2) {
                f3 = 180.0F;
            }

            if (k == 4) {
                f3 = 90.0F;
            }

            if (k == 5) {
                f3 = -90.0F;
            }

            GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, -1.0625F, 0.0F);
        }

        if (this.updateCounter / 6 % 2 == 0) {
            this.tileSign.lineBeingEdited = this.editLine;
        }

        TileEntityRendererDispatcher.instance.renderTileEntityAt(this.tileSign, -0.5D, -0.75D, -0.5D, 0.0F);
        this.tileSign.lineBeingEdited = -1;
        GL11.glPopMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
