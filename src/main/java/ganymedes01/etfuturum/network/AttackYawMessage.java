package ganymedes01.etfuturum.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class AttackYawMessage implements IMessage {

    float attackedAtYaw;

    public AttackYawMessage() {}

    public AttackYawMessage(float yaw) {
        attackedAtYaw = yaw;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        attackedAtYaw = buf.readFloat();

    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(attackedAtYaw);
    }

}
