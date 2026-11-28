package ganymedes01.etfuturum.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Logger;
import ganymedes01.etfuturum.ducks.ISign;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

/**
 * Server-side handler for sign text updates.
 * Receives text from a player, applies it to the tile entity, and relays to all nearby players.
 */
public class SignUpdateServerHandler implements IMessageHandler<SignUpdateMessage, IMessage> {

	private static final int MAX_LINE_LENGTH = 90;
	private static final int MAX_VISIBLE_CHARS = 15;
	private static final double MAX_EDIT_DISTANCE_SQ = 64.0D;

	@Override
	public IMessage onMessage(SignUpdateMessage message, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		WorldServer world = player.getServerForPlayer();
		if (world == null) {
			return null;
		}

		int x = message.tileX, y = message.tileY, z = message.tileZ;

		// Don't load or generate chunks for arbitrary coordinates
		if (!world.blockExists(x, y, z)) {
			return null;
		}

		if (player.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D) > MAX_EDIT_DISTANCE_SQ) {
			return null;
		}

		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof TileEntitySign signTile) || !(te instanceof ISign sign)) {
			return null;
		}

		if (sign.isWaxed() || signTile.func_145911_b() != player) {
			Logger.warn("Player " + player.getCommandSenderName() + " just tried to change non-editable sign");
			return null;
		}

		if (!areLinesValid(message.frontLines) || !areLinesValid(message.backLines)) {
			Logger.warn("Player " + player.getCommandSenderName() + " sent invalid sign text");
			return null;
		}

		System.arraycopy(message.frontLines, 0, sign.getSignText(true), 0, 4);
		System.arraycopy(message.backLines, 0, sign.getSignText(false), 0, 4);

		// Ignore client message for wax/dye; server authoritative
		message.waxed = sign.isWaxed();
		message.dyeId = sign.getDyeId();

		te.markDirty();
		world.markBlockForUpdate(x, y, z);
		Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
		if (chunk != null) {
			chunk.setChunkModified();
		}
		EtFuturum.networkWrapper.sendToAllAround(message,
				new NetworkRegistry.TargetPoint(world.provider.dimensionId, x + 0.5, y + 0.5, z + 0.5, 64));
		return null;
	}

	private static boolean areLinesValid(String[] lines) {
		if (lines == null || lines.length != 4) {
			return false;
		}
		for (String line : lines) {
			if (line == null || line.length() > MAX_LINE_LENGTH) {
				return false;
			}
			int visible = 0;
			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				// Same as ChatAllowedCharacters.isAllowedCharacter, but formatting codes are allowed
				if (c < ' ' || c == 127) {
					return false;
				}
				if (c == '§' && i + 1 < line.length()) {
					i++;
				} else {
					visible++;
				}
			}
			if (visible > MAX_VISIBLE_CHARS) {
				return false;
			}
		}
		return true;
	}
}
