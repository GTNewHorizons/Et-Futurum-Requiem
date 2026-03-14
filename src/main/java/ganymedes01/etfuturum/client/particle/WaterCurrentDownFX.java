package ganymedes01.etfuturum.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WaterCurrentDownFX extends EntityBubbleFX {

	private float angle;

	public WaterCurrentDownFX(World world, double x, double y, double z) {
		super(world, x, y, z, 0, 0, 0);
		this.particleMaxAge = (int) (world.rand.nextDouble() * 60.0D) + 30;
		this.noClip = true;
		this.motionX = 0;
		this.motionY = -0.05;
		this.motionZ = 0;
		this.angle = rand.nextFloat() * (float) Math.PI * 2F;
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
			return;
		}

		this.motionX += 0.25D * MathHelper.cos(this.angle);
		this.motionZ += 0.25D * MathHelper.sin(this.angle);
		this.motionX *= 0.07D;
		this.motionZ *= 0.07D;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);

		if (this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)).getMaterial() != Material.water
			|| this.isCollided) {
			this.setDead();
		}

		this.angle += 0.08F;
	}
}
