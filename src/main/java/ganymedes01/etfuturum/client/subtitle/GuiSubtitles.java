package ganymedes01.etfuturum.client.subtitle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.SoundEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;

public class GuiSubtitles extends Gui {

    public static GuiSubtitles INSTANCE;
    private final Minecraft mc;
    private final List<Subtitle> subtitles = new ArrayList<>();
    private final Pattern subtitleBlacklistPattern;

    public GuiSubtitles(Minecraft mc) {
        this.mc = mc;
        subtitleBlacklistPattern = Pattern.compile(ConfigFunctions.subtitleBlacklist);
    }

    private static final Pattern MOB_SOUND = Pattern.compile("mob\\.([a-zA-Z]+)\\.([a-zA-Z]+)$");
    private static final Pattern FALL_SOUND = Pattern.compile("game\\.[a-zA-Z]+\\.hurt\\.fall\\.(small|big)");

    private static final HashMap<String, String> ENTITY_SOUND_NAME_TRANSLATION_MAP = new HashMap<>();

    private String smartTranslation(String key) {
        Matcher matcher = MOB_SOUND.matcher(key);
        if (matcher.matches()) {
            String mobId = ENTITY_SOUND_NAME_TRANSLATION_MAP.computeIfAbsent(matcher.group(1), id -> {
                for (String possible : EntityList.stringToClassMapping.keySet()) {
                    if (id.equalsIgnoreCase(possible)) {
                        return possible;
                    }
                }
                return null;
            });
            if (mobId == null) return null;
            String soundType = matcher.group(2);
            String mobKey = "subtitle.mob." + soundType;
            String translated = StatCollector
                .translateToLocalFormatted(mobKey, StatCollector.translateToLocal("entity." + mobId + ".name"));
            if (translated.equals(mobKey)) return null;
            return translated;
        }
        matcher = FALL_SOUND.matcher(key);
        if (matcher.matches()) {
            return StatCollector.translateToLocal("subtitle.fall." + matcher.group(1));
        }
        return null;
    }

    private String getSubtitleText(ISound sound, String name) {
        if (subtitleBlacklistPattern.matcher(name)
            .matches()) return null;
        if (name.startsWith("step.") || name.endsWith(".step")) name = "footsteps";
        if (name.endsWith(".place")) name = "block.placed";
        if (name.contains("\\.dig\\.") || name.endsWith(".break")) name = "block.destroyed";
        String key = "subtitle." + name;
        String translated = I18n.format(key);
        if (translated.equals(key)) {
            translated = smartTranslation(name);
            if (translated != null) return translated;
        } else return translated;
        return key;
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onRender(RenderGameOverlayEvent.Pre e) {
        if (e.type == RenderGameOverlayEvent.ElementType.CHAT)
            renderSubtitles(new ScaledResolution(mc, mc.displayWidth, mc.displayHeight));
    }

    @SubscribeEvent
    public void onPlaySound(SoundEvent.SoundSourceEvent e) {
        String s = getSubtitleText(e.sound, e.name.toLowerCase());
        if (s == null || e.sound.getAttenuationType() == ISound.AttenuationType.NONE) return;
        Vec3 soundVec = Vec3.createVectorHelper(e.sound.getXPosF(), e.sound.getYPosF(), e.sound.getZPosF());

        if (!this.subtitles.isEmpty()) {
            for (Subtitle sub : this.subtitles) {
                if (sub.getString()
                    .equals(s)) {
                    sub.refresh(soundVec);
                    return;
                }
            }
        }

        this.subtitles.add(new Subtitle(s, soundVec));
    }

    public void renderSubtitles(ScaledResolution resolution) {
        if (!subtitles.isEmpty()) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            Vec3 vec3d = Vec3.createVectorHelper(
                mc.thePlayer.posX,
                mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(),
                mc.thePlayer.posZ);
            Vec3 vec3d1 = Vec3.createVectorHelper(0.0D, 0.0D, -1.0D);
            vec3d1.rotateAroundX(-mc.thePlayer.rotationPitch * 0.017453292F);
            vec3d1.rotateAroundY(-mc.thePlayer.rotationYaw * 0.017453292F);
            Vec3 vec3d2 = Vec3.createVectorHelper(0.0D, 1.0D, 0.0D);
            vec3d2.rotateAroundX(-mc.thePlayer.rotationPitch * 0.017453292F);
            vec3d2.rotateAroundY(-mc.thePlayer.rotationYaw * 0.017453292F);
            Vec3 vec3d3 = vec3d1.crossProduct(vec3d2);
            int i = 0;
            int j = 0;
            Iterator<Subtitle> iterator = this.subtitles.iterator();

            while (iterator.hasNext()) {
                Subtitle sub = iterator.next();

                if (sub.getStartTime() + 3000L <= Minecraft.getSystemTime()) {
                    iterator.remove();
                } else {
                    j = Math.max(j, mc.fontRenderer.getStringWidth(sub.getString()));
                }
            }

            j = j + mc.fontRenderer.getStringWidth("<")
                + mc.fontRenderer.getStringWidth(" ")
                + mc.fontRenderer.getStringWidth(">")
                + mc.fontRenderer.getStringWidth(" ");

            for (Subtitle sub : this.subtitles) {
                String s = sub.getString();
                Vec3 vec3d4 = sub.getLocation()
                    .subtract(vec3d)
                    .normalize();
                double d0 = vec3d3.dotProduct(vec3d4);
                double d1 = vec3d1.dotProduct(vec3d4);

                boolean flag = d1 > 0.5D;
                int l = j / 2;
                int i1 = mc.fontRenderer.FONT_HEIGHT;
                int j1 = i1 / 2;
                int k1 = mc.fontRenderer.getStringWidth(s);
                double timeFactorSlide = ((float) (Minecraft.getSystemTime() - sub.getStartTime()) / 3000.0F);
                if (timeFactorSlide < 0) timeFactorSlide = 0;
                else if (timeFactorSlide > 1) timeFactorSlide = 1;
                double timeFactor = 255.0 + (75.0 - 255.0) * timeFactorSlide;
                int l1 = MathHelper.floor_double(timeFactor);
                int i2 = l1 << 16 | l1 << 8 | l1;
                GL11.glPushMatrix();
                GL11.glTranslatef(
                    (float) resolution.getScaledWidth() - (float) l - 2.0F,
                    (float) (resolution.getScaledHeight() - 30) - (float) (i * (i1 + 1)),
                    0.0F);
                GL11.glScalef(1.0F, 1.0F, 1.0F);
                drawRect(-l - 1, -j1 - 1, l + 1, j1 + 1, -872415232);
                GL11.glEnable(GL11.GL_BLEND);

                if (!flag) {
                    if (d0 > 0.0D) {
                        mc.fontRenderer.drawString(">", l - mc.fontRenderer.getStringWidth(">"), -j1, i2 + -16777216);
                    } else if (d0 < 0.0D) {
                        mc.fontRenderer.drawString("<", -l, -j1, i2 + -16777216);
                    }
                }

                mc.fontRenderer.drawString(s, -k1 / 2, -j1, i2 + -16777216);
                GL11.glPopMatrix();
                ++i;
            }
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        }
    }

    public static class Subtitle {

        private final String subtitle;
        private long startTime;
        private Vec3 location;

        public Subtitle(String subtitleIn, Vec3 locationIn) {
            this.subtitle = subtitleIn;
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }

        public String getString() {
            return this.subtitle;
        }

        public long getStartTime() {
            return this.startTime;
        }

        public Vec3 getLocation() {
            return this.location;
        }

        public void refresh(Vec3 locationIn) {
            this.location = locationIn;
            this.startTime = Minecraft.getSystemTime();
        }
    }
}
