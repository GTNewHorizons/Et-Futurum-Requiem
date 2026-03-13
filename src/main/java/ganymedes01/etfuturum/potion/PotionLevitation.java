package ganymedes01.etfuturum.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class PotionLevitation extends ModPotions {

    protected PotionLevitation(String name, int p_i1573_1_, boolean p_i1573_2_, int p_i1573_3_) {
        super(name, p_i1573_1_, p_i1573_2_, p_i1573_3_);
        setPotionName("potion." + name);
        setEffectiveness(1);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int level) {
        if (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).capabilities.isFlying) {
            entity.fallDistance = 0;
            entity.addVelocity(0, (0.05D * (double) (level + 1) - entity.motionY) * 0.2D, 0);
            if (!entity.isInWater() && !entity.handleLavaMovement()) entity.motionY += 0.08D; // Counter falling
                                                                                              // velocity
        }
    }

    @Override
    public boolean isReady(int p_76397_1_, int p_76397_2_) {
        return true;
    }

    @Override
    public boolean hasPacket() {
        return true;
    }
}
