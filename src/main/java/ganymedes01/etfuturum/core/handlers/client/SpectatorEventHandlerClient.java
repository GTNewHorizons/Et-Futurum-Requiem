package ganymedes01.etfuturum.core.handlers.client;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.*;

@EventBusSubscriber(side = {Side.CLIENT})
public abstract class SpectatorEventHandlerClient {

    private static boolean doRefreshModel = false;
    private static boolean canSelect = false;

    @EventBusSubscriber.Condition
    private static boolean condition() {
        return ConfigMixins.enableSpectatorMode;
    }

    private static void setBipedVisible(ModelBiped biped, boolean visible) {
        biped.bipedHead.showModel = visible;
        biped.bipedHeadwear.showModel = visible;
        biped.bipedBody.showModel = visible;
        biped.bipedRightArm.showModel = visible;
        biped.bipedLeftArm.showModel = visible;
        biped.bipedRightLeg.showModel = visible;
        biped.bipedLeftLeg.showModel = visible;
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (SpectatorUtils.getSpectatingEntity(event.entityPlayer) == null) {
            if (SpectatorUtils.isSpectator(event.entityPlayer)) {
                setBipedVisible(event.renderer.modelBipedMain, false);
                event.renderer.modelBipedMain.bipedHead.showModel = true;
                event.renderer.modelBipedMain.bipedHeadwear.showModel = true;
            } else {
                setBipedVisible(event.renderer.modelBipedMain, true);
            }
        } else {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerArmor(RenderPlayerEvent.Specials.Pre event) {
        if (SpectatorUtils.isSpectator(event.entityPlayer)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderEntity(RenderLivingEvent.Pre event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        Entity entity2 = SpectatorUtils.getSpectatingEntity(player);
        if (SpectatorUtils.isSpectator(player) && entity2 != null && entity2.equals(event.entity) && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onOverlayRenderPre(RenderGameOverlayEvent.Pre event) {
        if (SpectatorUtils.isSpectator(Minecraft.getMinecraft().thePlayer)) {
            if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR || (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && !canSelect)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onHandRender(RenderHandEvent event) {
        if (SpectatorUtils.isSpectator(Minecraft.getMinecraft().thePlayer)) {
            event.setCanceled(true);
            doRefreshModel = true;
        } else if (doRefreshModel) {
//          Redraws the player model for one frame off-screen so it refreshes. Also make sure we only run this logic if this code is targeting the player we're playing as.
//          This is because in some cases loading the player model in 3rd person or the inventory and then going back to another game mode makes the hand invisible.
            doRefreshModel = false;
            RenderManager.instance.renderEntityWithPosYaw(Minecraft.getMinecraft().thePlayer, -180.0D, -180.0D, -180.0D, 0.0F, 0.0F);
        }
    }

    @SubscribeEvent
    public static void onFireRender(RenderBlockOverlayEvent event) {
        if (SpectatorUtils.isSpectator(Minecraft.getMinecraft().thePlayer)) {
            event.setCanceled(true);
        }
    }

    /* TODO look into increasing the distance instead of outright disabling it */
    @SubscribeEvent
    public static void onRenderFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (event.entity instanceof EntityPlayer) {
            if (SpectatorUtils.isSpectator((EntityPlayer) event.entity)) {
                if (event.block.getMaterial().isLiquid()) {
                    event.setCanceled(true);
                    event.density = 0;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockHighlight(DrawBlockHighlightEvent event) {
        if (SpectatorUtils.isSpectator(event.player)) {
            canSelect = SpectatorUtils.canSpectatorSelect(Minecraft.getMinecraft().theWorld.getTileEntity(event.target.blockX, event.target.blockY, event.target.blockZ)) || (event.target.entityHit != null && SpectatorUtils.getSpectatingEntity(event.player) == null);
            if (!canSelect) {
                event.setCanceled(true);
            }
        }
    }
}
