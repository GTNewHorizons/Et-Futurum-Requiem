package ganymedes01.etfuturum.client.particle;

import net.minecraft.world.World;

public class EndRodFX extends EtFuturumFXParticle {

    public EndRodFX(World world, double x, double y, double z) {
        super(
            world,
            x,
            y,
            z,
            0,
            0,
            0,
            60 + CustomParticles.rand.nextInt(12),
            1,
            0xFFFFFFFF,
            "textures/particle/glitter.png",
            8);
        motionX = particleRand.nextGaussian() * 0.0005D;
        motionY = particleRand.nextGaussian() * 0.0005D;
        motionZ = particleRand.nextGaussian() * 0.0005D;
        particleGravity = 0.0025F;
        currentTexture = 7;
        fadeAway = true;
        setColorFade(0xF2DEC9);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        motionX *= 0.025D;
        motionZ *= 0.025D;
        if (this.particleAge % Math.round(particleMaxAge / 8) == 0) {
            if (currentTexture > 0) {
                this.currentTexture--;
            }
        }
    }

}
