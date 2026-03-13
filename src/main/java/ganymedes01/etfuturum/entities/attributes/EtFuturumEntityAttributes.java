package ganymedes01.etfuturum.entities.attributes;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class EtFuturumEntityAttributes {

    public static final IAttribute flyingSpeed = (new RangedAttribute("etfuturum.flyingSpeed", 0.4D, 0.0D, 1024.0D))
        .setDescription("Flying Speed")
        .setShouldWatch(true);
}
