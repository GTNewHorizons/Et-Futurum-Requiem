package ganymedes01.etfuturum.pose;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerScaleEvent extends Event {
    public final EntityPlayer player;
    public float scale;

    public PlayerScaleEvent(EntityPlayer player, float scale) {
        this.player = player;
        this.scale = scale;
    }
}
