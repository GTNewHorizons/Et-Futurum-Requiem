package ganymedes01.etfuturum.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.SaveFormatOld;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;


@SideOnly(Side.CLIENT)
public class WorldIconManager {

    public static final int ICON_DISPLAY_SIZE = 32;
    public static final int ICON_OFFSET = 35;
    private static final int ICON_FILE_SIZE = 64;
    private static final long INITIAL_DELAY_MS = 5_000;
    private static final ResourceLocation MISSING_ICON = new ResourceLocation("minecraft", "textures/misc/unknown_pack.png");

    private static final Map<String, ResourceLocation> iconCache = new HashMap<>();

    private static BufferedImage heldGrab;
    private static String heldGrabFolder;
    private static boolean startPlaceholderDone;
    private static long worldLoadTime;

    public static ResourceLocation getOrLoadIcon(String worldFolderName) {
        ResourceLocation cached = iconCache.get(worldFolderName);
        if (cached != null) return cached;

        File iconFile = iconFileFor(worldFolderName);
        if (iconFile == null || !iconFile.exists()) return MISSING_ICON;

        try {
            BufferedImage img = ImageIO.read(iconFile);
            if (img == null) return MISSING_ICON;
            DynamicTexture tex = new DynamicTexture(img);
            ResourceLocation loc = Minecraft.getMinecraft().getTextureManager()
                    .getDynamicTextureLocation("etfu_world_icon_" + worldFolderName, tex);
            iconCache.put(worldFolderName, loc);
            return loc;
        } catch (Exception e) {
            return MISSING_ICON;
        }
    }

    public static void drawIcon(ResourceLocation icon, int x, int y) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        int size = ICON_DISPLAY_SIZE;
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV(x, y + size, 0, 0, 1);
        tess.addVertexWithUV(x + size, y + size, 0, 1, 1);
        tess.addVertexWithUV(x + size, y, 0, 1, 0);
        tess.addVertexWithUV(x, y, 0, 0, 0);
        tess.draw();
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void clearCache() {
        Minecraft mc = Minecraft.getMinecraft();
        for (ResourceLocation loc : iconCache.values()) {
            mc.getTextureManager().deleteTexture(loc);
        }
        iconCache.clear();
    }

    public static void onPreRenderOverlay() {
        // Pre(ALL): world drawn, HUD not yet, so the frame is already clean
        if (heldGrab != null) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen instanceof GuiIngameMenu)) return;
        if (!mc.isIntegratedServerRunning() || mc.getIntegratedServer() == null
                || mc.getIntegratedServer().isServerStopped()) return;

        BufferedImage icon = grabIcon();
        if (icon != null) {
            heldGrab = icon;
            heldGrabFolder = mc.getIntegratedServer().getFolderName();
        }
    }

    public static void onRenderTickEnd() {
        Minecraft mc = Minecraft.getMinecraft();

        if (!mc.isIntegratedServerRunning() || mc.getIntegratedServer() == null
                || mc.getIntegratedServer().isServerStopped()) {
            // back on the title screen, reset the session state
            worldLoadTime = 0;
            startPlaceholderDone = false;
            return;
        }
        if (mc.theWorld == null) {
            // between dimensions, keep the session state but don't tick the placeholder timer
            return;
        }

        // closed the menu without quitting, drop the grab so the next pause re-screenshots
        if (heldGrab != null && mc.currentScreen == null) {
            heldGrab = null;
            heldGrabFolder = null;
        }

        if (startPlaceholderDone) return;
        if (mc.currentScreen != null || mc.thePlayer == null) return;

        long now = System.currentTimeMillis();
        if (worldLoadTime == 0) {
            worldLoadTime = now;
            return;
        }
        if (now - worldLoadTime < INITIAL_DELAY_MS) return;

        // only try the placeholder once per session, success or fail
        startPlaceholderDone = true;
        File iconFile = iconFileFor(mc.getIntegratedServer().getFolderName());
        if (iconFile == null || iconFile.exists()) return;

        BufferedImage icon = grabIcon();
        if (icon != null) {
            writeIconAsync(icon, iconFile);
        }
    }

    public static void onWorldUnload() {
        // only quit-via-pause-menu leaves a held grab, so dimension changes and kicks write nothing
        if (heldGrab != null && heldGrabFolder != null) {
            File iconFile = iconFileFor(heldGrabFolder);
            if (iconFile != null) writeIconAsync(heldGrab, iconFile);
        }
        heldGrab = null;
        heldGrabFolder = null;
    }

    private static BufferedImage grabIcon() {
        Minecraft mc = Minecraft.getMinecraft();
        Framebuffer fb = mc.getFramebuffer();

        int w;
        int h;
        ByteBuffer buf;
        if (fb != null && fb.framebufferTexture >= 0 && OpenGlHelper.isFramebufferEnabled()) {
            w = fb.framebufferTextureWidth;
            h = fb.framebufferTextureHeight;
            if (w <= 0 || h <= 0) return null;
            buf = BufferUtils.createByteBuffer(w * h * 4);
            // sample mc's framebuffer texture directly by id rather than glReadPixels; glReadPixels
            // reads whatever FBO is bound, which under Angelica HUD caching is the cleared HUD buffer
            int prevTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, fb.framebufferTexture);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTex);
        } else {
            // no FBO, just read the bound buffer
            w = mc.displayWidth;
            h = mc.displayHeight;
            if (w <= 0 || h <= 0) return null;
            buf = BufferUtils.createByteBuffer(w * h * 4);
            GL11.glReadPixels(0, 0, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
        }

        // GL is bottom-left, flip Y on unpack
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int row = y * w;
            int flippedRow = (h - 1 - y) * w;
            for (int x = 0; x < w; x++) {
                int i = (row + x) * 4;
                int r = buf.get(i) & 0xFF;
                int g = buf.get(i + 1) & 0xFF;
                int b = buf.get(i + 2) & 0xFF;
                pixels[flippedRow + x] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
        }

        BufferedImage full = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        full.setRGB(0, 0, w, h, pixels, 0, w);

        int square = Math.min(w, h);
        BufferedImage cropped = full.getSubimage((w - square) / 2, (h - square) / 2, square, square);

        BufferedImage icon = new BufferedImage(ICON_FILE_SIZE, ICON_FILE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(cropped, 0, 0, ICON_FILE_SIZE, ICON_FILE_SIZE, null);
        g2d.dispose();
        return icon;
    }

    private static void writeIconAsync(BufferedImage icon, File targetFile) {
        Thread saveThread = new Thread(() -> {
            try {
                File parent = targetFile.getParentFile();
                if (parent != null) parent.mkdirs();
                ImageIO.write(icon, "png", targetFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "EFR-WorldIcon-Save");
        saveThread.setDaemon(true);
        saveThread.start();
    }

    private static File iconFileFor(String folderName) {
        if (folderName == null) return null;
        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.getSaveLoader() instanceof SaveFormatOld)) return null;
        File savesDir = ((SaveFormatOld) mc.getSaveLoader()).savesDirectory;
        return new File(savesDir, folderName + "/icon.png");
    }
}
