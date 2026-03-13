package ganymedes01.etfuturum.client.particle;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CherryLeafFX extends EtFuturumFXParticle {

    private final float randomFloat;
    private float rotSpeed;
    private final float spinAcceleration;
    protected float roll;
    protected float oRoll;

    public CherryLeafFX(World world, double x, double y, double z) {
        super(
            world,
            x,
            y,
            z,
            0,
            0,
            0,
            300,
            particleRand.nextBoolean() ? 0.5F : 0.75F,
            0xFFFFFFFF,
            "textures/particle/cherry_" + particleRand.nextInt(12) + ".png",
            1);
        randomFloat = rand.nextFloat();
        this.rotSpeed = (float) Math.toRadians(this.rand.nextBoolean() ? -30.0D : 30.0D);
        this.spinAcceleration = (float) Math.toRadians(this.rand.nextBoolean() ? -5.0D : 5.0D);
        this.particleGravity = 7.5E-4F;
        // this.friction = 1.0F;
        noClip = false;
    }

    @Override
    protected ResourceLocation[] loadTextures(World world, double x, double y, double z, ResourceLocation texture) {
        return new ResourceLocation[] { texture };
    }

    @Override
    public void onUpdate() {

        float f = (float) (300 - this.particleMaxAge);
        float f1 = Math.min(f / 300.0F, 1.0F);
        double d0 = Math.cos(Math.toRadians(randomFloat * 60.0F)) * 2.0D * Math.pow(f1, 1.25D);
        double d1 = Math.sin(Math.toRadians(randomFloat * 60.0F)) * 2.0D * Math.pow(f1, 1.25D);
        this.motionX += d0 * (double) 0.0025F;
        this.motionZ += d1 * (double) 0.0025F;
        this.motionY -= this.particleGravity;
        // this.moveEntity(motionX, motionY, motionZ);
        this.rotSpeed += this.spinAcceleration / 20.0F;
        this.oRoll = this.roll;
        this.roll += this.rotSpeed / 20.0F;
        particleAngle = roll;

        if (this.onGround || this.particleAge > 299 && (motionX == 0.0D || motionZ == 0.0D)) {
            this.setDead();
        }
        super.onUpdate();
    }
}
