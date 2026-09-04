package ganymedes01.etfuturum.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class SignUpdateMessage implements IMessage {

	public int tileX;
	public int tileY;
	public int tileZ;
	public String[] frontLines;
	public String[] backLines;
	public boolean waxed;
	public int dyeId = -1;

	public SignUpdateMessage() {
	}

	// Client -> server, only text matters. Wax/dye are no-ops on the server receiving side
	public SignUpdateMessage(int x, int y, int z, String[] frontLines, String[] backLines) {
		this(x, y, z, frontLines, backLines, false, -1);
	}

	// Server -> client, full state sync.
	public SignUpdateMessage(int x, int y, int z, String[] frontLines, String[] backLines, boolean waxed, int dyeId) {
		this.tileX = x;
		this.tileY = y;
		this.tileZ = z;
		this.frontLines = frontLines;
		this.backLines = backLines;
		this.waxed = waxed;
		this.dyeId = dyeId;
	}

	private static void writeLines(ByteBuf buf, String[] lines) {
		for (int i = 0; i < 4; i++) {
			String line = lines[i] != null ? lines[i] : "";
			byte[] bytes = line.getBytes(java.nio.charset.StandardCharsets.UTF_8);
			buf.writeShort(bytes.length);
			if (bytes.length > 0) {
				buf.writeBytes(bytes);
			}
		}
	}

	private static String[] readLines(ByteBuf buf) {
		String[] lines = new String[4];
		for (int i = 0; i < 4; i++) {
			int len = buf.readShort();
			if (len > 0) {
				byte[] bytes = new byte[len];
				buf.readBytes(bytes);
				lines[i] = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
			} else {
				lines[i] = "";
			}
		}
		return lines;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.tileX = buf.readInt();
		this.tileY = buf.readInt();
		this.tileZ = buf.readInt();
		this.waxed = buf.readBoolean();
		this.dyeId = buf.readInt();
		this.frontLines = readLines(buf);
		this.backLines = readLines(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.tileX);
		buf.writeInt(this.tileY);
		buf.writeInt(this.tileZ);
		buf.writeBoolean(this.waxed);
		buf.writeInt(this.dyeId);
		writeLines(buf, this.frontLines != null ? this.frontLines : new String[]{"", "", "", ""});
		writeLines(buf, this.backLines != null ? this.backLines : new String[]{"", "", "", ""});
	}
}
