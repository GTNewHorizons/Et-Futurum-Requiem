package ganymedes01.etfuturum.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

@SideOnly(Side.CLIENT)
public class ChunkLoadingProgress {

    public static float getRawProgress() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return 0.0F;
        int renderDist = mc.gameSettings.renderDistanceChunks;
        int expected = (renderDist * 2 + 1) * (renderDist * 2 + 1);
        int loaded = mc.theWorld.getChunkProvider().getLoadedChunkCount();
        return Math.min(1.0F, (float) loaded / expected);
    }

    public static boolean isChunkLoaded(int chunkX, int chunkZ) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return false;
        return mc.theWorld.getChunkProvider().chunkExists(chunkX, chunkZ);
    }

    public static int getPlayerChunkX() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) return (int) mc.thePlayer.posX >> 4;
        if (mc.theWorld != null) return mc.theWorld.getSpawnPoint().posX >> 4;
        return 0;
    }

    public static int getPlayerChunkZ() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) return (int) mc.thePlayer.posZ >> 4;
        if (mc.theWorld != null) return mc.theWorld.getSpawnPoint().posZ >> 4;
        return 0;
    }
}
