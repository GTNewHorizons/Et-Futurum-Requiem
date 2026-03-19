package ganymedes01.etfuturum.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BubbleColumnFX extends DeferredBubbleFX {

	public BubbleColumnFX(World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(world, x, y, z, xSpeed, ySpeed, zSpeed);
		// lasts longer than a normal bubble
		this.particleMaxAge = (int) (40.0D / (this.worldObj.rand.nextDouble() * 0.8D + 0.2D));
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY += 0.005D;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		// friction = 0.85
		this.motionX *= 0.85D;
		this.motionY *= 0.85D;
		this.motionZ *= 0.85D;

		if (this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)).getMaterial() != Material.water) {
			this.setDead();
		}

		if (this.particleMaxAge-- <= 0) {
			this.setDead();
		}
	}
}
