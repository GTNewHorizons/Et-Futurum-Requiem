package ganymedes01.etfuturum.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
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
    private static final long CAPTURE_COOLDOWN_MS = 30_000;
    private static final long INITIAL_DELAY_MS = 8_000;
    private static final ResourceLocation MISSING_ICON = new ResourceLocation("minecraft", "textures/misc/unknown_pack.png");

    private static final Map<String, ResourceLocation> iconCache = new HashMap<>();
    private static volatile boolean pendingCapture = false;
    private static long lastCaptureTime = 0;
    private static long worldLoadTime = 0;
    private static boolean worldTimerStarted = false;
    private static boolean pendingHudlessCapture = false;
    private static File pendingCaptureFile = null;

    public static ResourceLocation getOrLoadIcon(String worldFolderName) {
        ResourceLocation cached = iconCache.get(worldFolderName);
        if (cached != null) return cached;

        Minecraft mc = Minecraft.getMinecraft();
        File savesDir = ((SaveFormatOld) mc.getSaveLoader()).savesDirectory;
        File iconFile = new File(savesDir, worldFolderName + "/icon.png");

        if (!iconFile.exists()) return MISSING_ICON;

        try {
            BufferedImage img = ImageIO.read(iconFile);
            if (img == null) return MISSING_ICON;
            DynamicTexture tex = new DynamicTexture(img);
            ResourceLocation loc = mc.getTextureManager()
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

    public static void onRenderTickEnd() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!mc.isIntegratedServerRunning()) return;
        if (mc.theWorld == null) {
            worldTimerStarted = false;
            return;
        }
        if (mc.getIntegratedServer() == null || mc.getIntegratedServer().isServerStopped()) return;
        if (mc.currentScreen != null) return;

        long now = System.currentTimeMillis();

        if (!worldTimerStarted) {
            worldTimerStarted = true;
            worldLoadTime = now;
            return;
        }

        boolean shouldCapture = false;

        if (now - worldLoadTime >= INITIAL_DELAY_MS && lastCaptureTime == 0) {
            shouldCapture = true;
        } else if (pendingCapture && now - lastCaptureTime >= CAPTURE_COOLDOWN_MS) {
            shouldCapture = true;
            pendingCapture = false;
        }

        if (!shouldCapture) return;

        String folderName = mc.getIntegratedServer().getFolderName();
        File savesDir = ((SaveFormatOld) mc.getSaveLoader()).savesDirectory;
        pendingCaptureFile = new File(savesDir, folderName + "/icon.png");
        pendingHudlessCapture = true;
    }

    // world is in the framebuffer but the HUD has not been drawn yet
    public static void onPreRenderHUD() {
        if (!pendingHudlessCapture) return;
        pendingHudlessCapture = false;
        if (pendingCaptureFile != null) {
            takeAutoScreenshot(pendingCaptureFile);
            pendingCaptureFile = null;
        }
    }

    public static void scheduleCaptureFromSave() {
        pendingCapture = true;
    }

    public static void resetData() {
        pendingCapture = false;
        pendingHudlessCapture = false;
        pendingCaptureFile = null;
        lastCaptureTime = 0;
        worldLoadTime = 0;
        worldTimerStarted = false;
    }

    private static void takeAutoScreenshot(File targetFile) {
        Minecraft mc = Minecraft.getMinecraft();

        int w = mc.displayWidth;
        int h = mc.displayHeight;
        if (w <= 0 || h <= 0) return;

        ByteBuffer buf = BufferUtils.createByteBuffer(w * h * 4);
        GL11.glReadPixels(0, 0, w, h, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);

        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int srcIdx = (y * w + x) * 4;
                int r = buf.get(srcIdx) & 0xFF;
                int g = buf.get(srcIdx + 1) & 0xFF;
                int b = buf.get(srcIdx + 2) & 0xFF;
                int flippedY = h - 1 - y;
                pixels[flippedY * w + x] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
        }

        BufferedImage screenshot = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        screenshot.setRGB(0, 0, w, h, pixels, 0, w);

        lastCaptureTime = System.currentTimeMillis();

        Thread saveThread = new Thread(() -> {
            try {
                int cropX = 0;
                int cropY = 0;
                int cropW = w;
                int cropH = h;
                if (w > h) {
                    cropX = (w - h) / 2;
                    cropW = h;
                } else {
                    cropY = (h - w) / 2;
                    cropH = w;
                }

                BufferedImage cropped = screenshot.getSubimage(cropX, cropY, cropW, cropH);
                BufferedImage scaled = new BufferedImage(ICON_FILE_SIZE, ICON_FILE_SIZE, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = scaled.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(cropped, 0, 0, ICON_FILE_SIZE, ICON_FILE_SIZE, null);
                g2d.dispose();

                ImageIO.write(scaled, "png", targetFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "EFR-WorldIcon-Save");
        saveThread.setDaemon(true);
        saveThread.start();
    }
}
