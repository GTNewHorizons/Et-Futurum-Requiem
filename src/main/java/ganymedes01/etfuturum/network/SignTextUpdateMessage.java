package ganymedes01.etfuturum.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class SignTextUpdateMessage implements IMessage {

	public int tileX;
	public int tileY;
	public int tileZ;
	public boolean front;
	public String[] lines;

	public SignTextUpdateMessage() {
	}

	public SignTextUpdateMessage(int x, int y, int z, boolean front, String[] lines) {
		this.tileX = x;
		this.tileY = y;
		this.tileZ = z;
		this.front = front;
		this.lines = lines;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.tileX = buf.readInt();
		this.tileY = buf.readInt();
		this.tileZ = buf.readInt();
		this.front = buf.readBoolean();
		this.lines = new String[4];
		for (int i = 0; i < 4; i++) {
			int len = buf.readShort();
			if (len > 0) {
				byte[] bytes = new byte[len];
				buf.readBytes(bytes);
				this.lines[i] = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
			} else {
				this.lines[i] = "";
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.tileX);
		buf.writeInt(this.tileY);
		buf.writeInt(this.tileZ);
		buf.writeBoolean(this.front);
		for (int i = 0; i < 4; i++) {
			String line = this.lines[i] != null ? this.lines[i] : "";
			byte[] bytes = line.getBytes(java.nio.charset.StandardCharsets.UTF_8);
			buf.writeShort(bytes.length);
			if (bytes.length > 0) {
				buf.writeBytes(bytes);
			}
		}
	}
}
