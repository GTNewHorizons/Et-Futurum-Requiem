package ganymedes01.etfuturum.client.particle;

import net.minecraft.world.World;

public class BlackHeartFX extends EtFuturumFXParticle {

    public BlackHeartFX(World world, double x, double y, double z) {
        super(
            world,
            x,
            y,
            z,
            (particleRand.nextFloat() - 0.5F) * 0.1D,
            0.15D,
            (particleRand.nextFloat() - 0.5F) * 0.1D,
            15,
            1 + (CustomParticles.rand.nextFloat() * 0.125F),
            0xFFFFFFFF,
            "textures/particle/damage.png",
            1);
        particleGravity = 0.5F;
    }
}
