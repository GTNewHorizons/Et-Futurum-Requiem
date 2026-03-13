package ganymedes01.etfuturum.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import ganymedes01.etfuturum.tileentities.TileEntityWoodSign;
import io.netty.buffer.ByteBuf;

public class WoodSignOpenMessage implements IMessage {

    public int tileX;
    public int tileY;
    public int tileZ;
    public int id;

    public WoodSignOpenMessage() {}

    public WoodSignOpenMessage(TileEntityWoodSign tileentitysign, int i) {
        tileX = tileentitysign.xCoord;
        tileY = tileentitysign.yCoord;
        tileZ = tileentitysign.zCoord;
        id = i;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.tileX = buf.readInt();
        this.tileY = buf.readInt();
        this.tileZ = buf.readInt();
        this.id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.tileX);
        buf.writeInt(this.tileY);
        buf.writeInt(this.tileZ);
        buf.writeInt(this.id);
    }

}
