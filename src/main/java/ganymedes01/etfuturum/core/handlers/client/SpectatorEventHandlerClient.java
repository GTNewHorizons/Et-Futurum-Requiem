package ganymedes01.etfuturum.core.handlers.client;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.*;

@EventBusSubscriber(side = {Side.CLIENT})
public abstract class SpectatorEventHandlerClient {

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
        EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
        Entity entity2 = SpectatorUtils.getSpectatingEntity(player);
        if (SpectatorUtils.isSpectator(player) && entity2 != null && entity2.equals(event.entity) && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onOverlayRenderPre(RenderGameOverlayEvent.Pre event) {
        EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
        if (SpectatorUtils.isSpectator(player)) {
            if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
                event.setCanceled(true);
            } else if(event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && SpectatorUtils.getSpectatingEntity(player) == null) {
                MovingObjectPosition mop = FMLClientHandler.instance().getClient().objectMouseOver;
                if(mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    TileEntity te = FMLClientHandler.instance().getWorldClient().getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
                    if (!SpectatorUtils.canSpectatorSelectTileEntity(te)) {
                        event.setCanceled(true);
                    }
                } else if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                    if(mop.entityHit == null || SpectatorUtils.getSpectatingEntity(player) != null) {
                        event.setCanceled(true);
                    }
                } else {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onHandRender(RenderHandEvent event) {
        EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
        if (SpectatorUtils.isSpectator(player)) {
            event.setCanceled(true);
        } else if (SpectatorUtils.wasSpectator(player)) {
            if(RenderManager.instance.getEntityRenderObject(player) instanceof RenderBiped biped) {
                setBipedVisible(biped.modelBipedMain, true);
            }
        }
    }

    @SubscribeEvent
    public static void onFireRender(RenderBlockOverlayEvent event) {
        if (SpectatorUtils.isSpectator(FMLClientHandler.instance().getClientPlayerEntity())) {
            event.setCanceled(true);
        }
    }

    /* TODO look into increasing the distance instead of outright disabling it */
    @SubscribeEvent
    public static void onRenderFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (event.entity instanceof EntityPlayer) {
            if (SpectatorUtils.isSpectator(event.entity)) {
                if (event.block.getMaterial().isLiquid()) {
                    event.setCanceled(true);
                    event.density = 0;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockHighlight(DrawBlockHighlightEvent event) {
        if (SpectatorUtils.isSpectator(event.player) && event.target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            TileEntity te = FMLClientHandler.instance().getWorldClient().getTileEntity(event.target.blockX, event.target.blockY, event.target.blockZ);
            if (!SpectatorUtils.canSpectatorSelectTileEntity(te)) {
                event.setCanceled(true);
            }
        }
    }
}
