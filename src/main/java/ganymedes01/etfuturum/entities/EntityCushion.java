package ganymedes01.etfuturum.entities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCushion extends Entity {

	private static final int DYE_WATCHABLE = 14;

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
		dataWatcher.addObject(DYE_WATCHABLE, (byte) 0);
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();

		if (!worldObj.isRemote && worldObj.getTotalWorldTime() % 20 == 0) {
			if (!onValidSurface()) {
				breakCushion(null);
			}
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setDyeColor(nbt.getInteger("Color"));
		this.x = nbt.getInteger("TileX");
		this.y = nbt.getInteger("TileY");
		this.z = nbt.getInteger("TileZ");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("Color", this.getDyeColor());
		nbt.setInteger("TileX", this.x);
		nbt.setInteger("TileY", this.y);
		nbt.setInteger("TileZ", this.z);
	}

	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean interactFirst(EntityPlayer player) {
		if (riddenByEntity != null && riddenByEntity instanceof EntityPlayer && riddenByEntity != player) {
			return true;
		} else if (riddenByEntity != null && riddenByEntity != player) {
			return false;
		} else {
			if (!worldObj.isRemote) {
				player.mountEntity(this);
			}

			return true;
		}
	}

	@Override
	public boolean hitByEntity(Entity entity) {
		return entity instanceof EntityPlayer ? attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) entity), 0.0F) : false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (isEntityInvulnerable()) {
			return false;
		} else {
			if (!isDead && !worldObj.isRemote) {
				setBeenAttacked();
				breakCushion(source.getEntity());
			}

			return true;
		}
	}

	// Disable bounding checks on client, stops it teleporting up out of blocks it _should_ be inside
	@SideOnly(Side.CLIENT)
	@Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements) {
		setPosition(x, y, z);
		setRotation(yaw, pitch);
	}

	@Override
	protected boolean shouldSetPosAfterLoading() {
		return false;
	}

	public void breakCushion(Entity entity) {
		setDead();

		if (entity instanceof EntityPlayer player) {
			if (player.capabilities.isCreativeMode) return;
		}

		entityDropItem(ModItems.CUSHION.newItemStack(1, getDyeColor()), 0.0F);
	}

	public boolean onValidSurface() {
		return !worldObj.isAirBlock(x, y, z);
	}

	public byte getDyeColor() {
		return dataWatcher.getWatchableObjectByte(DYE_WATCHABLE);
	}

	public void setDyeColor(int color) {
		dataWatcher.updateObject(DYE_WATCHABLE, (byte) color);
	}

}
