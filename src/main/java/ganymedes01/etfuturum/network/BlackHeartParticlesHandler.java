package ganymedes01.etfuturum.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;

public class BlackHeartParticlesHandler implements IMessageHandler<BlackHeartParticlesMessage, IMessage> {

    @Override
    public IMessage onMessage(BlackHeartParticlesMessage message, MessageContext ctx) {
        handleMessage(message);
        return null;
    }

    @SideOnly(Side.CLIENT)
    private void handleMessage(BlackHeartParticlesMessage message) {
        if (ConfigWorld.enableDmgIndicator) {
            World world = Minecraft.getMinecraft().theWorld;
            CustomParticles.spawnBlackHeartParticle(world, message.x, message.y, message.z);
        }
    }
}
