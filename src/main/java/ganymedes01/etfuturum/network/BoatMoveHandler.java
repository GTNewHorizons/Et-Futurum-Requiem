package ganymedes01.etfuturum.network;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class BoatMoveHandler implements IMessageHandler<BoatMoveMessage, IMessage> {

    @Override
    public IMessage onMessage(BoatMoveMessage message, MessageContext ctx) {
        WorldServer vehWorld = DimensionManager.getWorld(message.dimensionId);
        if (vehWorld != null) {
            Entity vehicle = vehWorld.getEntityByID(message.entityId);
            if (vehicle == null) return null;
            if (vehicle.riddenByEntity != ctx.getServerHandler().playerEntity) {
                /* Only take position updates from the riding player */
                return null;
            }
            double expectedDelta = vehicle.motionX * vehicle.motionX + vehicle.motionY * vehicle.motionY
                + vehicle.motionZ * vehicle.motionZ;
            double deltaX = message.x - vehicle.posX;
            double deltaY = message.y - vehicle.posY;
            double deltaZ = message.z - vehicle.posZ;
            double actualDelta = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
            MinecraftServer server = MinecraftServer.getServer();
            if (Math.abs(expectedDelta - actualDelta) > 100.0D && !server.isSinglePlayer()) {
                System.err.println("Vehicle moved wrongly");
                vehicle.setPositionAndRotation(
                    vehicle.posX - 1,
                    vehicle.posY,
                    vehicle.posZ - 1,
                    vehicle.rotationYaw,
                    vehicle.rotationPitch);
                ctx.getServerHandler()
                    .sendPacket(new S18PacketEntityTeleport(vehicle));
                return null;
            }
            vehicle.setPositionAndRotation(message.x, message.y, message.z, message.yaw, message.pitch);
            ctx.getServerHandler()
                .sendPacket(new S18PacketEntityTeleport(vehicle));
        }
        return null;
    }
}
