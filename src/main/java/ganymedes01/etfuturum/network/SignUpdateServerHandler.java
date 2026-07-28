package ganymedes01.etfuturum.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ducks.ISign;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

/**
 * Server-side handler for sign text updates.
 * Receives text from a player, applies it to the tile entity, and relays to all nearby players.
 */
public class SignUpdateServerHandler implements IMessageHandler<SignUpdateMessage, IMessage> {

	@Override
	public IMessage onMessage(SignUpdateMessage message, MessageContext ctx) {
		WorldServer world = ctx.getServerHandler().playerEntity.getServerForPlayer();
		if (world == null) {
			return null;
		}

		TileEntity te = world.getTileEntity(message.tileX, message.tileY, message.tileZ);
		if (te instanceof ISign) {
			ISign sign = (ISign) te;

			System.arraycopy(message.frontLines, 0, sign.getSignText(true), 0, 4);
			System.arraycopy(message.backLines, 0, sign.getSignText(false), 0, 4);

			// Ignore client message for wax/dye; server authoritative
			message.waxed = sign.isWaxed();
			message.dyeId = sign.getDyeId();

			te.markDirty();
			world.markBlockForUpdate(message.tileX, message.tileY, message.tileZ);
			Chunk chunk = world.getChunkFromChunkCoords(message.tileX >> 4, message.tileZ >> 4);
			if (chunk != null) {
				chunk.setChunkModified();
			}
			EtFuturum.networkWrapper.sendToAllAround(message,
					new NetworkRegistry.TargetPoint(world.provider.dimensionId,
							message.tileX + 0.5, message.tileY + 0.5, message.tileZ + 0.5, 64));
		}
		return null;
	}
}
