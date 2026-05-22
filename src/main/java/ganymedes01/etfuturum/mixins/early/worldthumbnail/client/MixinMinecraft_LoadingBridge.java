package ganymedes01.etfuturum.mixins.early.worldthumbnail.client;

import ganymedes01.etfuturum.client.GuiLoadingBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MixinMinecraft_LoadingBridge {

    @Redirect(method = "launchIntegratedServer",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V",
                    ordinal = 1))
    private void etfu$bridgeLoadingScreen(Minecraft mc, GuiScreen screen) {
        Framebuffer fb = mc.getFramebuffer();
        if (fb != null) {
            fb.setFramebufferColor(0, 0, 0, 1);
        }
        mc.displayGuiScreen(new GuiLoadingBridge());

        if (mc.currentScreen != null && fb != null) {
            ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            fb.framebufferClear();
            fb.bindFramebuffer(true);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0, 1000, 3000);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0, 0, -2000);
            mc.currentScreen.drawScreen(0, 0, 0);
            fb.unbindFramebuffer();
            fb.framebufferRender(mc.displayWidth, mc.displayHeight);
            Display.update();
        }
    }
}
