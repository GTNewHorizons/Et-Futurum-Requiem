package ganymedes01.etfuturum.client.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;

public class AmbienceAdditionSound extends PositionedSound {

    public AmbienceAdditionSound(String resourceLoc) {
        super(new ResourceLocation(resourceLoc));
        xPosF = yPosF = zPosF = 0;
        this.field_147666_i = ISound.AttenuationType.NONE; // attenuationType
    }

}
