package ganymedes01.etfuturum.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import ganymedes01.etfuturum.EtFuturum;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.tileentity.TileEntitySign;

public class WoodSignOpenMessage implements IMessage {

	public int tileX;
	public int tileY;
	public int tileZ;
	public int id;
	public boolean front;

	public WoodSignOpenMessage() {
	}

	public WoodSignOpenMessage(TileEntitySign tileentitysign, int blockId, boolean front) {
		tileX = tileentitysign.xCoord;
		tileY = tileentitysign.yCoord;
		tileZ = tileentitysign.zCoord;
		id = blockId;
		this.front = front;
	}

	public static void openEditor(EntityPlayerMP player, TileEntitySign sign, boolean front) {
		sign.func_145912_a(player);
		// Like modern vanilla, send the block first so the client edits the real sign instead of a placeholder
		player.playerNetServerHandler.sendPacket(new S23PacketBlockChange(sign.xCoord, sign.yCoord, sign.zCoord, sign.getWorldObj()));
		int blockId = Block.getIdFromBlock(sign.getWorldObj().getBlock(sign.xCoord, sign.yCoord, sign.zCoord));
		EtFuturum.networkWrapper.sendTo(new WoodSignOpenMessage(sign, blockId, front), player);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.tileX = buf.readInt();
		this.tileY = buf.readInt();
		this.tileZ = buf.readInt();
		this.id = buf.readInt();
		this.front = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.tileX);
		buf.writeInt(this.tileY);
		buf.writeInt(this.tileZ);
		buf.writeInt(this.id);
		buf.writeBoolean(this.front);
	}
}
