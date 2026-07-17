package ganymedes01.etfuturum.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ducks.IWaxableSign;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Client-side handler for sign text updates relayed from the server.
 * Updates the client-side tile entity and triggers a re-render.
 */
public class SignTextUpdateClientHandler implements IMessageHandler<SignTextUpdateMessage, IMessage> {

	@Override
	public IMessage onMessage(SignTextUpdateMessage message, MessageContext ctx) {
		handleMessage(message);
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handleMessage(SignTextUpdateMessage message) {
		World world = Minecraft.getMinecraft().theWorld;
		if (world == null) return;

		TileEntity te = world.getTileEntity(message.tileX, message.tileY, message.tileZ);
		if (te instanceof IWaxableSign) {
			IWaxableSign sign = (IWaxableSign) te;
			String[] target = sign.getSignText(message.front);
			System.arraycopy(message.lines, 0, target, 0, 4);
			world.func_147479_m(message.tileX, message.tileY, message.tileZ);
		}
	}
}
