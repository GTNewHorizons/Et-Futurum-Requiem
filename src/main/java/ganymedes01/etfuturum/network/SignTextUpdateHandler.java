package ganymedes01.etfuturum.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ducks.IWaxableSign;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

/**
 * Server-side handler for sign text updates.
 * Receives text from a player, applies it to the tile entity, and relays to all nearby players.
 */
public class SignTextUpdateHandler implements IMessageHandler<SignTextUpdateMessage, IMessage> {

	@Override
	public IMessage onMessage(SignTextUpdateMessage message, MessageContext ctx) {
		WorldServer world = ctx.getServerHandler().playerEntity.getServerForPlayer();
		if (world == null) return null;

		TileEntity te = world.getTileEntity(message.tileX, message.tileY, message.tileZ);
		if (te instanceof IWaxableSign) {
			IWaxableSign sign = (IWaxableSign) te;
			String[] target = sign.getSignText(message.front);
			System.arraycopy(message.lines, 0, target, 0, 4);

			te.markDirty();
			world.markBlockForUpdate(message.tileX, message.tileY, message.tileZ);
			EtFuturum.networkWrapper.sendToAllAround(message,
					new NetworkRegistry.TargetPoint(world.provider.dimensionId,
							message.tileX + 0.5, message.tileY + 0.5, message.tileZ + 0.5, 64));
		}
		return null;
	}
}
