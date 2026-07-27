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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;

public class WoodSignOpenHandler implements IMessageHandler<WoodSignOpenMessage, IMessage> {

	@Override
	public IMessage onMessage(WoodSignOpenMessage message, MessageContext ctx) {
		handleMessage(message);
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handleMessage(WoodSignOpenMessage message) {
		World world = FMLClientHandler.instance().getClient().theWorld;
		TileEntity tileEntity = world.getTileEntity(message.tileX, message.tileY, message.tileZ);

		System.out.println("[CLIENT] WoodSignOpenHandler RECEIVED: front=" + message.front
			+ " pos=" + message.tileX + "," + message.tileY + "," + message.tileZ
			+ " blockId=" + message.id
			+ " existingTE=" + (tileEntity instanceof TileEntitySign ? "TileEntitySign" : (tileEntity != null ? tileEntity.getClass().getSimpleName() : "null")));

		if (!(tileEntity instanceof TileEntitySign)) {
			Block block = Block.getBlockById(message.id);
			tileEntity = block instanceof ganymedes01.etfuturum.blocks.BlockWoodSign
					? new TileEntityWoodSign() : new TileEntitySign();
			tileEntity.blockType = block;
			tileEntity.setWorldObj(world);
			tileEntity.xCoord = message.tileX;
			tileEntity.yCoord = message.tileY;
			tileEntity.zCoord = message.tileZ;
			System.out.println("[CLIENT] WoodSignOpenHandler: created new TE " + tileEntity.getClass().getSimpleName());
		}

		tileEntity.markDirty();
		FMLClientHandler.instance().getClient().displayGuiScreen(new GuiEditWoodSign((TileEntitySign) tileEntity, message.front));
	}
}
