package ganymedes01.etfuturum.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ducks.ISign;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Client-side handler for sign text updates relayed from the server.
 * Updates the client-side tile entity and triggers a re-render.
 */
public class SignUpdateClientHandler implements IMessageHandler<SignUpdateMessage, IMessage> {

	@Override
	public IMessage onMessage(SignUpdateMessage message, MessageContext ctx) {
		handleMessage(message);
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handleMessage(SignUpdateMessage message) {
		World world = Minecraft.getMinecraft().theWorld;
		if (world == null) {
			return;
		}

		TileEntity te = world.getTileEntity(message.tileX, message.tileY, message.tileZ);
		if (te instanceof ISign) {
			ISign sign = (ISign) te;
			System.arraycopy(message.frontLines, 0, sign.getSignText(true), 0, 4);
			System.arraycopy(message.backLines, 0, sign.getSignText(false), 0, 4);
			sign.setDyeId(message.dyeId);
			sign.setWaxed(message.waxed);
			world.func_147479_m(message.tileX, message.tileY, message.tileZ);
		}
	}
}
