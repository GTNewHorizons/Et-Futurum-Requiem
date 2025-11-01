package ganymedes01.etfuturum.client.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import roadhog360.hogutils.api.utils.FastRandom;

public class BeeNectarFX extends EntityFX {

	private static final int color = 0xE7C5B5;
	private static final float red = (color >> 16 & 0xff) / 255F;
	private static final float green = (color >> 8 & 0xff) / 255F;
	private static final float blue = (color & 0xff) / 255F;

	public BeeNectarFX(World worldIn, double p_i1203_2_, double p_i1203_4_, double p_i1203_6_) {
		super(worldIn, p_i1203_2_, p_i1203_4_, p_i1203_6_, 0, 0, 0);
		rand = new FastRandom();
		setRBGColorF(red, green, blue);
		this.motionX = this.motionY = this.motionZ = 0.0D;

		this.setParticleTextureIndex(112);
		this.setSize(0.01F, 0.01F);
		this.particleGravity = 0.15F;
		this.particleMaxAge = 60;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (this.onGround) {
			this.setDead();
		}
	}
}
