package ganymedes01.etfuturum.network;

import net.minecraft.entity.Entity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class BoatMoveMessage implements IMessage {

    public int dimensionId;
    public int entityId;
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;

    public BoatMoveMessage() {

    }

    public BoatMoveMessage(Entity entityIn) {
        this.dimensionId = entityIn.worldObj.provider.dimensionId;
        this.entityId = entityIn.getEntityId();
        this.x = entityIn.posX;
        this.y = entityIn.posY;
        this.z = entityIn.posZ;
        this.yaw = entityIn.rotationYaw;
        this.pitch = entityIn.rotationPitch;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.dimensionId = buf.readInt();
        this.entityId = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.dimensionId);
        buf.writeInt(this.entityId);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
    }
}
