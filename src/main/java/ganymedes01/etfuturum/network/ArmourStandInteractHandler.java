package ganymedes01.etfuturum.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ganymedes01.etfuturum.entities.EntityArmourStand;

public class ArmourStandInteractHandler implements IMessageHandler<ArmourStandInteractMessage, IMessage> {

    @Override
    public IMessage onMessage(ArmourStandInteractMessage message, MessageContext ctx) {
        World world = DimensionManager.getWorld(message.dimID);
        EntityArmourStand stand = (EntityArmourStand) world.getEntityByID(message.standID);
        EntityPlayer player = (EntityPlayer) world.getEntityByID(message.playerID);

        stand.interact(player, message.hitPos);
        return null;
    }
}
