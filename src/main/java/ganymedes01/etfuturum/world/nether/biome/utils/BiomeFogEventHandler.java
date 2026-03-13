package ganymedes01.etfuturum.world.nether.biome.utils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeModContainer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.world.nether.biome.NetherBiomeBase;
import ganymedes01.etfuturum.world.nether.dimension.WorldProviderEFRNether;

public class BiomeFogEventHandler {

    public static final BiomeFogEventHandler INSTANCE = new BiomeFogEventHandler();

    @SubscribeEvent
    public void onGetFogColor(final EntityViewRenderEvent.FogColors event) {
        if (event.entity.worldObj.provider instanceof WorldProviderEFRNether
            && event.entity instanceof EntityPlayer player) {
            final World world = player.worldObj;
            final int x = MathHelper.floor_double(player.posX);
            final int y = MathHelper.floor_double(player.posY);
            final int z = MathHelper.floor_double(player.posZ);
            final Block blockAtEyes = ActiveRenderInfo
                .getBlockAtEntityViewpoint(world, event.entity, (float) event.renderPartialTicks);
            if (blockAtEyes.getMaterial() == Material.lava) {
                return;
            }

            Vec3 mixedColor = getFogBlendColour(
                world,
                player,
                x,
                y,
                z,
                event.red,
                event.green,
                event.blue,
                event.renderPartialTicks);

            event.red = (float) mixedColor.xCoord;
            event.green = (float) mixedColor.yCoord;
            event.blue = (float) mixedColor.zCoord;
        }
    }

    private static Vec3 postProcessColor(final World world, final EntityLivingBase player, float r, float g, float b,
        final double renderPartialTicks) {
        double darkScale = (player.lastTickPosY + (player.posY - player.lastTickPosY) * renderPartialTicks)
            * world.provider.getVoidFogYFactor();
        if (player.isPotionActive(Potion.blindness)) {
            final int duration = player.getActivePotionEffect(Potion.blindness)
                .getDuration();
            darkScale *= ((duration < 20) ? (1.0f - duration / 20.0f) : 0.0);
        }
        if (darkScale < 1.0) {
            darkScale = ((darkScale < 0.0) ? 0.0 : (darkScale * darkScale));
            r *= (float) darkScale;
            g *= (float) darkScale;
            b *= (float) darkScale;
        }
        if (player.isPotionActive(Potion.nightVision)) {
            final int duration = player.getActivePotionEffect(Potion.nightVision)
                .getDuration();
            final float brightness = (duration > 200) ? 1.0f
                : (0.7f + MathHelper
                    .sin((float) ((duration - renderPartialTicks) * 3.141592653589793 * 0.20000000298023224)) * 0.3f);
            float scale = 1.0f / r;
            scale = Math.min(scale, 1.0f / g);
            scale = Math.min(scale, 1.0f / b);
            r = r * (1.0f - brightness) + r * scale * brightness;
            g = g * (1.0f - brightness) + g * scale * brightness;
            b = b * (1.0f - brightness) + b * scale * brightness;
        }
        if (Minecraft.getMinecraft().gameSettings.anaglyph) {
            final float aR = (r * 30.0f + g * 59.0f + b * 11.0f) / 100.0f;
            final float aG = (r * 30.0f + g * 70.0f) / 100.0f;
            final float aB = (r * 30.0f + b * 70.0f) / 100.0f;
            r = aR;
            g = aG;
            b = aB;
        }
        return Vec3.createVectorHelper(r, g, b);
    }

    private static Vec3 getFogBlendColour(final World world, final EntityLivingBase playerEntity, final int playerX,
        final int playerY, final int playerZ, final float defR, final float defG, final float defB,
        final double renderPartialTicks) {
        final GameSettings settings = Minecraft.getMinecraft().gameSettings;
        final int[] ranges = ForgeModContainer.blendRanges;
        int distance = 0;
        if (settings.fancyGraphics && settings.renderDistanceChunks >= 0
            && settings.renderDistanceChunks < ranges.length) {
            distance = ranges[settings.renderDistanceChunks];
        }
        float rBiomeFog = 0.0f;
        float gBiomeFog = 0.0f;
        float bBiomeFog = 0.0f;
        float weightBiomeFog = 0.0f;
        for (int x = -distance; x <= distance; ++x) {
            for (int z = -distance; z <= distance; ++z) {
                final BiomeGenBase biome = world.getBiomeGenForCoords(playerX + x, playerZ + z);
                if (biome instanceof NetherBiomeBase) {
                    final int fogColour = ((NetherBiomeBase) biome).fogSkyColor;
                    float rPart = (float) ((fogColour & 0xFF0000) >> 16);
                    float gPart = (float) ((fogColour & 0xFF00) >> 8);
                    float bPart = (float) (fogColour & 0xFF);
                    float weightPart = 1.0f;
                    if (x == -distance) {
                        final double xDiff = 1.0 - (playerEntity.posX - playerX);
                        rPart *= xDiff;
                        gPart *= xDiff;
                        bPart *= xDiff;
                        weightPart *= xDiff;
                    } else if (x == distance) {
                        final double xDiff = playerEntity.posX - playerX;
                        rPart *= xDiff;
                        gPart *= xDiff;
                        bPart *= xDiff;
                        weightPart *= xDiff;
                    }
                    if (z == -distance) {
                        final double zDiff = 1.0 - (playerEntity.posZ - playerZ);
                        rPart *= zDiff;
                        gPart *= zDiff;
                        bPart *= zDiff;
                        weightPart *= zDiff;
                    } else if (z == distance) {
                        final double zDiff = playerEntity.posZ - playerZ;
                        rPart *= zDiff;
                        gPart *= zDiff;
                        bPart *= zDiff;
                        weightPart *= zDiff;
                    }
                    rBiomeFog += rPart;
                    gBiomeFog += gPart;
                    bBiomeFog += bPart;
                    weightBiomeFog += weightPart;
                }
            }
        }
        if (weightBiomeFog == 0.0f || distance == 0) {
            return Vec3.createVectorHelper(defR, defG, defB);
        }
        rBiomeFog /= 255.0f;
        gBiomeFog /= 255.0f;
        bBiomeFog /= 255.0f;

        float rScale = 0.30f;
        float gScale = 0.30f;
        float bScale = 0.30f;

        rBiomeFog *= rScale / weightBiomeFog;
        gBiomeFog *= gScale / weightBiomeFog;
        bBiomeFog *= bScale / weightBiomeFog;
        final Vec3 processedColor = postProcessColor(
            world,
            playerEntity,
            rBiomeFog,
            gBiomeFog,
            bBiomeFog,
            renderPartialTicks);
        rBiomeFog = (float) processedColor.xCoord;
        gBiomeFog = (float) processedColor.yCoord;
        bBiomeFog = (float) processedColor.zCoord;
        final float weightMixed = (float) (distance * 2 * (distance * 2));
        final float weightDefault = weightMixed - weightBiomeFog;
        processedColor.xCoord = (rBiomeFog * weightBiomeFog + defR * weightDefault) / weightMixed;
        processedColor.yCoord = (gBiomeFog * weightBiomeFog + defG * weightDefault) / weightMixed;
        processedColor.zCoord = (bBiomeFog * weightBiomeFog + defB * weightDefault) / weightMixed;
        return processedColor;
    }
}
