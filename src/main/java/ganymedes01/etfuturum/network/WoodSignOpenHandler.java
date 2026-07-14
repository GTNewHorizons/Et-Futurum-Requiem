package ganymedes01.etfuturum.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.client.gui.inventory.GuiEditWoodSign;
import ganymedes01.etfuturum.tileentities.TileEntityWoodSign;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;

public class WoodSignOpenHandler implements IMessageHandler<WoodSignOpenMessage, IMessage> {

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(WoodSignOpenMessage message, MessageContext ctx) {
		WorldClient world = FMLClientHandler.instance().getClient().theWorld;
		TileEntity tileEntity = world.getTileEntity(message.tileX, message.tileY, message.tileZ);

		// If the TE hasn't synced yet (first-time placement race), create a fallback.
		// Otherwise use the real world TE so typing updates appear in-world instantly.
		if (!(tileEntity instanceof TileEntityWoodSign)) {
			tileEntity = new TileEntityWoodSign();
			tileEntity.blockType = Block.getBlockById(message.id);
			tileEntity.setWorldObj(world);
			tileEntity.xCoord = message.tileX;
			tileEntity.yCoord = message.tileY;
			tileEntity.zCoord = message.tileZ;
		}

		tileEntity.markDirty();

		FMLClientHandler.instance().getClient().displayGuiScreen(new GuiEditWoodSign((TileEntityWoodSign) tileEntity));
		return null;
	}

}
