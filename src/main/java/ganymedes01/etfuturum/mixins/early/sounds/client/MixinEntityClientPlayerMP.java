package ganymedes01.etfuturum.mixins.early.sounds.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Session;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityClientPlayerMP.class)
/**
 * This mixin's purpose is to totally disable the player hurt sound.
 * We use mixins instead of events to be 100% sure we're canceling the client player's hurt sound.
 * Then we use our EntityPlayer mixin to choose which hurt sound to use, and make
 *
 * @author roadhog360
 *
 */
public class MixinEntityClientPlayerMP extends EntityPlayerSP {

    public MixinEntityClientPlayerMP(Minecraft p_i1238_1_, World p_i1238_2_, Session p_i1238_3_, int p_i1238_4_) {
        super(p_i1238_1_, p_i1238_2_, p_i1238_3_, p_i1238_4_);
    }

    @Override
    protected String getDeathSound() {
        return "null";
    }

    @Override
    protected String getHurtSound() {
        return "null";
    }

    @Override
    public void playSound(String name, float volume, float pitch) {
        if (!name.equals("null")) {
            super.playSound(name, volume, pitch);
        }
    }
}
