package ganymedes01.etfuturum.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
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

	@Override
	public void fromBytes(ByteBuf buf) {
		this.tileX = buf.readInt();
		this.tileY = buf.readInt();
		this.tileZ = buf.readInt();
		this.id = buf.readInt();
		this.front = buf.readBoolean();
		System.out.println("[NET] WoodSignOpenMessage.fromBytes: front=" + this.front
			+ " pos=" + this.tileX + "," + this.tileY + "," + this.tileZ
			+ " blockId=" + this.id);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.tileX);
		buf.writeInt(this.tileY);
		buf.writeInt(this.tileZ);
		buf.writeInt(this.id);
		buf.writeBoolean(this.front);
		System.out.println("[NET] WoodSignOpenMessage.toBytes: front=" + this.front
			+ " pos=" + this.tileX + "," + this.tileY + "," + this.tileZ
			+ " blockId=" + this.id);
	}
}
