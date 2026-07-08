package ganymedes01.etfuturum.entities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

	public EntityCushion(World world, int x, int y, int z, double subY) {
		this(world);
		this.x = x;
		this.y = y;
		this.z = z;

		this.posX = x + 0.5;
		this.posY = y + subY;
		this.posZ = z + 0.5;
	}

	@Override
	protected void entityInit() {

	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();

		if (!worldObj.isRemote && worldObj.getTotalWorldTime() % 20 == 0) {
			if (!onValidSurface()) {
				setDead();
			}
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		this.x = nbt.getInteger("TileX");
		this.y = nbt.getInteger("TileY");
		this.z = nbt.getInteger("TileZ");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("TileX", this.x);
		nbt.setInteger("TileY", this.y);
		nbt.setInteger("TileZ", this.z);
	}

	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean interactFirst(EntityPlayer player) {
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player) {
			return true;
		} else if (this.riddenByEntity != null && this.riddenByEntity != player) {
			return false;
		} else {
			if (!this.worldObj.isRemote) {
				player.mountEntity(this);
			}

			return true;
		}
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

	// Disable bounding checks on client!
	@SideOnly(Side.CLIENT)
	@Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements) {
		this.setPosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	@Override
	protected boolean shouldSetPosAfterLoading() {
		return false;
	}

	public boolean onValidSurface() {
		return !worldObj.isAirBlock(x, y, z);
	}

}
