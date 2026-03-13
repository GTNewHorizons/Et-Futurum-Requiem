package ganymedes01.etfuturum.network;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ganymedes01.etfuturum.entities.EntityNewBoatWithChest;

public class ChestBoatOpenInventoryHandler implements IMessageHandler<ChestBoatOpenInventoryMessage, IMessage> {

    @Override
    public IMessage onMessage(ChestBoatOpenInventoryMessage message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player.ridingEntity instanceof EntityNewBoatWithChest)
            player.displayGUIChest((EntityNewBoatWithChest) player.ridingEntity);
        return null;
    }
}
