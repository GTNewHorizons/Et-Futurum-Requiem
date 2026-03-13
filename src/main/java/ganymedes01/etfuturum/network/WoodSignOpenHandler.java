package ganymedes01.etfuturum.network;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.client.gui.inventory.GuiEditWoodSign;
import ganymedes01.etfuturum.tileentities.TileEntityWoodSign;

public class WoodSignOpenHandler implements IMessageHandler<WoodSignOpenMessage, IMessage> {

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(WoodSignOpenMessage message, MessageContext ctx) {
        WorldClient world = FMLClientHandler.instance()
            .getClient().theWorld;
        if (world.blockExists(message.tileX, message.tileY, message.tileZ)) {
            TileEntity tileEntity = FMLClientHandler.instance()
                .getClient().theWorld.getTileEntity(message.tileX, message.tileY, message.tileZ);

            if (!(tileEntity instanceof TileEntityWoodSign)) {
                tileEntity = new TileEntityWoodSign();
                tileEntity.blockType = Block.getBlockById(message.id);
                tileEntity.setWorldObj(
                    FMLClientHandler.instance()
                        .getClient().theWorld);
                tileEntity.xCoord = message.tileX;
                tileEntity.yCoord = message.tileY;
                tileEntity.zCoord = message.tileZ;
            }

            tileEntity.markDirty();

            FMLClientHandler.instance()
                .getClient()
                .displayGuiScreen(new GuiEditWoodSign((TileEntityWoodSign) tileEntity));
        }
        return null;
    }

}
