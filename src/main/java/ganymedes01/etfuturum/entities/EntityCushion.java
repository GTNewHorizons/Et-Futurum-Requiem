package ganymedes01.etfuturum.entities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCushion extends Entity {

	public int x;
	public int y;
	public int z;

	public EntityCushion(World world) {
		super(world);
		this.yOffset = 0.0F;
		this.setSize(1.0F, 0.25F);
		this.noClip = true;
	}

	public EntityCushion(World world, int x, int y, int z) {
		this(world);
		this.x = x;
		this.y = y;
		this.z = z;

		this.posX = x + 0.5;
		this.posY = getSurfaceHeight();
		this.posZ = z + 0.5;
	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {

	}

	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean hitByEntity(Entity entity) {
		return entity instanceof EntityPlayer ? this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) entity), 0.0F) : false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable()) {
			return false;
		} else {
			if (!this.isDead && !this.worldObj.isRemote) {
				this.setDead();
				this.setBeenAttacked();
				// this.onBroken(source.getEntity());
			}

			return true;
		}
	}

	@Override
	protected boolean shouldSetPosAfterLoading() {
		return false;
	}

	public boolean onValidSurface() {
		return true;
	}

	public double getSurfaceHeight() {
		Block block = worldObj.getBlock(x, y - 1, z);
		block.setBlockBoundsBasedOnState(worldObj, x, y - 1, z);
		double maxY = block.getBlockBoundsMaxY();
		if (block instanceof BlockStairs)
			maxY = 0.5D;
		return y - 1 + maxY;
	}

}
