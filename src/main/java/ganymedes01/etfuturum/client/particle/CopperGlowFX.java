package ganymedes01.etfuturum.client.particle;

import net.minecraft.world.World;

public class CopperGlowFX extends EtFuturumFXParticle {

    public CopperGlowFX(World world, double x, double y, double z, int color) {
        super(
            world,
            x,
            y,
            z,
            0,
            0,
            0,
            30 + CustomParticles.rand.nextInt(26),
            1,
            color,
            "textures/particle/glow.png",
            1);
        motionX = (particleRand.nextDouble() * 0.01D) - 0.005D;
        motionY = (particleRand.nextDouble() * 0.01D) - 0.005D;
        motionZ = (particleRand.nextDouble() * 0.01D) - 0.005D;
        this.particleGravity = 0;
    }
}
