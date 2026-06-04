package ganymedes01.etfuturum.entities;

import com.google.common.collect.Lists;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.potion.ModPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class EntityShulkerBullet extends Entity {
	private EntityLivingBase owner;
	private Entity target;
	private EnumFacing direction;
	private int steps;
	private double targetDeltaX;
	private double targetDeltaY;
	private double targetDeltaZ;
	private UUID ownerUniqueId;
	private BlockPos ownerBlockPos;
	private UUID targetUniqueId;
	private BlockPos targetBlockPos;

	private static final int OWNER_ID = 2;
	private static final int TARGET_ID = 3;

	public EntityShulkerBullet(World worldIn) {
		super(worldIn);
		this.setSize(0.3125F, 0.3125F);
		this.noClip = true;
	}

	public EntityShulkerBullet(World worldIn, double x, double y, double z, double motionXIn, double motionYIn, double motionZIn) {
		this(worldIn);
		this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
		this.motionX = motionXIn;
		this.motionY = motionYIn;
		this.motionZ = motionZIn;
	}

	public EntityShulkerBullet(World worldIn, EntityLivingBase ownerIn, Entity targetIn, EnumFacing p_i46772_4_) {
		this(worldIn);
		this.owner = ownerIn;
		BlockPos blockpos = new BlockPos(ownerIn);
		double d0 = (double) blockpos.getX() + 0.5D;
		double d1 = (double) blockpos.getY() + 0.5D;
		double d2 = (double) blockpos.getZ() + 0.5D;
		this.setLocationAndAngles(d0, d1, d2, this.rotationYaw, this.rotationPitch);
		this.target = targetIn;
		this.direction = EnumFacing.UP;
		this.selectNextMoveDirection(p_i46772_4_);
		this.getDataWatcher().updateObject(OWNER_ID, owner.getEntityId());
		this.getDataWatcher().updateObject(TARGET_ID, target.getEntityId());
	}

	@Override
	protected void entityInit() {
		this.getDataWatcher().addObject(OWNER_ID, -1);
		this.getDataWatcher().addObject(TARGET_ID, -1);
	}

	private void firstTick() {
		if (getDataWatcher().getWatchableObjectInt(OWNER_ID) > -1 && owner == null) {
			Entity entity = worldObj.getEntityByID(getDataWatcher().getWatchableObjectInt(OWNER_ID));
			if (entity instanceof EntityLivingBase) {
				owner = (EntityLivingBase) entity;
			}
		}
		if (getDataWatcher().getWatchableObjectInt(TARGET_ID) > -1 && target == null) {
			Entity entity = worldObj.getEntityByID(getDataWatcher().getWatchableObjectInt(TARGET_ID));
			if (entity instanceof EntityLivingBase) {
				target = entity;
			}
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		if (this.owner != null) {
			BlockPos blockpos = new BlockPos(this.owner);
			NBTTagCompound nbttagcompound = Utils.createUUIDTag(this.owner.getUniqueID());
			nbttagcompound.setInteger("X", blockpos.getX());
			nbttagcompound.setInteger("Y", blockpos.getY());
			nbttagcompound.setInteger("Z", blockpos.getZ());
			compound.setTag("Owner", nbttagcompound);
		}

		if (this.target != null) {
			BlockPos blockpos1 = new BlockPos(this.target);
			NBTTagCompound nbttagcompound1 = Utils.createUUIDTag(this.target.getUniqueID());
			nbttagcompound1.setInteger("X", blockpos1.getX());
			nbttagcompound1.setInteger("Y", blockpos1.getY());
			nbttagcompound1.setInteger("Z", blockpos1.getZ());
			compound.setTag("Target", nbttagcompound1);
		}

		if (this.direction != null) {
			compound.setInteger("Dir", this.direction.ordinal());
		}

		compound.setInteger("Steps", this.steps);
		compound.setDouble("TXD", this.targetDeltaX);
		compound.setDouble("TYD", this.targetDeltaY);
		compound.setDouble("TZD", this.targetDeltaZ);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		this.steps = compound.getInteger("Steps");
		this.targetDeltaX = compound.getDouble("TXD");
		this.targetDeltaY = compound.getDouble("TYD");
		this.targetDeltaZ = compound.getDouble("TZD");

		if (compound.hasKey("Dir", 99)) {
			this.direction = EnumFacing.getFront(compound.getInteger("Dir"));
		}

		if (compound.hasKey("Owner", 10)) {
			NBTTagCompound nbttagcompound = compound.getCompoundTag("Owner");
			this.ownerUniqueId = Utils.getUUIDFromTag(nbttagcompound);
			this.ownerBlockPos = new BlockPos(nbttagcompound.getInteger("X"), nbttagcompound.getInteger("Y"), nbttagcompound.getInteger("Z"));
		}

		if (compound.hasKey("Target", 10)) {
			NBTTagCompound nbttagcompound1 = compound.getCompoundTag("Target");
			this.targetUniqueId = Utils.getUUIDFromTag(nbttagcompound1);
			this.targetBlockPos = new BlockPos(nbttagcompound1.getInteger("X"), nbttagcompound1.getInteger("Y"), nbttagcompound1.getInteger("Z"));
		}
	}

	private void setDirection(EnumFacing directionIn) {
		this.direction = directionIn;
	}

	private void selectNextMoveDirection(EnumFacing p_184569_1_) {
		double d0 = 0.5D;
		BlockPos blockpos;

		if (this.target == null) {
			blockpos = (new BlockPos(this)).down();
		} else {
			d0 = (double) this.target.height * 0.5D;
			blockpos = new BlockPos(this.target.posX, this.target.posY + d0, this.target.posZ);
		}

		double d1 = (double) blockpos.getX() + 0.5D;
		double d2 = (double) blockpos.getY() + d0;
		double d3 = (double) blockpos.getZ() + 0.5D;
		EnumFacing enumfacing = null;

		if (p_184569_1_ != null && blockpos.getSquaredDistance(this.posX, this.posY, this.posZ, false) >= 4.0D) {
			BlockPos blockpos1 = new BlockPos(this);
			List<EnumFacing> list = Lists.newArrayList();

			if (p_184569_1_.getFrontOffsetX() == 0) {
				//Swapped East and West to make up for incorrect mapping
				if (blockpos1.getX() < blockpos.getX() && this.worldObj.isAirBlock(blockpos1.west().getX(), blockpos1.getY(), blockpos1.getZ())) {
					list.add(EnumFacing.WEST);
				} else if (blockpos1.getX() > blockpos.getX() && this.worldObj.isAirBlock(blockpos1.east().getX(), blockpos1.getY(), blockpos1.getZ())) {
					list.add(EnumFacing.EAST);
				}
			}

			if (p_184569_1_.getFrontOffsetY() == 0) {
				if (blockpos1.getY() < blockpos.getY() && this.worldObj.isAirBlock(blockpos1.getX(), blockpos1.up().getY(), blockpos1.getZ())) {
					list.add(EnumFacing.UP);
				} else if (blockpos1.getY() > blockpos.getY() && this.worldObj.isAirBlock(blockpos1.getX(), blockpos1.down().getY(), blockpos1.getZ())) {
					list.add(EnumFacing.DOWN);
				}
			}

			if (p_184569_1_.getFrontOffsetZ() == 0) {
				if (blockpos1.getZ() < blockpos.getZ() && this.worldObj.isAirBlock(blockpos1.getX(), blockpos1.getY(), blockpos1.south().getZ())) {
					list.add(EnumFacing.SOUTH);
				} else if (blockpos1.getZ() > blockpos.getZ() && this.worldObj.isAirBlock(blockpos1.getX(), blockpos1.getY(), blockpos1.north().getZ())) {
					list.add(EnumFacing.NORTH);
				}
			}

			enumfacing = EnumFacing.getFront(rand.nextInt(6));

			if (list.isEmpty()) {
				for (int i = 5; !this.worldObj.isAirBlock(blockpos1.getX() + enumfacing.getFrontOffsetX(), blockpos1.getY() + enumfacing.getFrontOffsetY(),
						blockpos1.getZ() + enumfacing.getFrontOffsetZ()) && i > 0; --i) {
					enumfacing = EnumFacing.getFront(rand.nextInt(6));
				}
			} else {
				enumfacing = list.get(this.rand.nextInt(list.size()));
			}

			d1 = this.posX + (double) enumfacing.getFrontOffsetX();
			d2 = this.posY + (double) enumfacing.getFrontOffsetY();
			d3 = this.posZ + (double) enumfacing.getFrontOffsetZ();
		}

		this.setDirection(enumfacing);
		double d6 = d1 - this.posX;
		double d7 = d2 - this.posY;
		double d4 = d3 - this.posZ;
		double d5 = MathHelper.sqrt_double(d6 * d6 + d7 * d7 + d4 * d4);

		if (d5 == 0.0D) {
			this.targetDeltaX = 0.0D;
			this.targetDeltaY = 0.0D;
			this.targetDeltaZ = 0.0D;
		} else {
			this.targetDeltaX = d6 / d5 * 0.15D;
			this.targetDeltaY = d7 / d5 * 0.15D;
			this.targetDeltaZ = d4 / d5 * 0.15D;
		}

		this.isAirBorne = true;
		this.steps = 10 + this.rand.nextInt(5) * 10;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
			this.setDead();
		} else {
			super.onUpdate();
			if (!this.worldObj.isRemote) {
				if (this.target == null && this.targetUniqueId != null) {
					for (EntityLivingBase entitylivingbase : this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, BlockPos.getBB(this.targetBlockPos.add(-2, -2, -2), this.targetBlockPos.add(2, 2, 2)))) {
						if (entitylivingbase.getUniqueID().equals(this.targetUniqueId)) {
							this.target = entitylivingbase;
							break;
						}
					}

					this.targetUniqueId = null;
				}

				if (this.owner == null && this.ownerUniqueId != null) {
					for (EntityLivingBase entitylivingbase1 : this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, BlockPos.getBB(this.ownerBlockPos.add(-2, -2, -2), this.ownerBlockPos.add(2, 2, 2)))) {
						if (entitylivingbase1.getUniqueID().equals(this.ownerUniqueId)) {
							this.owner = entitylivingbase1;
							break;
						}
					}

					this.ownerUniqueId = null;
				}

				if (this.target == null || !this.target.isEntityAlive()
					/*|| this.target instanceof EntityPlayer && ((EntityPlayer)this.target).isSpectator() */) {
					this.motionY -= 0.04D;
				} else {
					this.targetDeltaX = MathHelper.clamp_double(this.targetDeltaX * 1.025D, -1.0D, 1.0D);
					this.targetDeltaY = MathHelper.clamp_double(this.targetDeltaY * 1.025D, -1.0D, 1.0D);
					this.targetDeltaZ = MathHelper.clamp_double(this.targetDeltaZ * 1.025D, -1.0D, 1.0D);
					this.motionX += (this.targetDeltaX - this.motionX) * 0.2D;
					this.motionY += (this.targetDeltaY - this.motionY) * 0.2D;
					this.motionZ += (this.targetDeltaZ - this.motionZ) * 0.2D;
				}
			}

			if (ticksExisted == 1 && worldObj.isRemote) {
				firstTick();
			}

			MovingObjectPosition raytraceresult = Utils.forwardsRaycast(this, true, false, ticksExisted <= 30 ? this.owner : null);

			if (raytraceresult != null) {
				this.bulletHit(raytraceresult);
			}

			this.setPosition(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			Utils.rotateTowardsMovement(this, 0.5F);

			if (this.worldObj.isRemote) {
				CustomParticles.spawnEndRodParticle(worldObj, this.posX - this.motionX, this.posY - this.motionY + 0.15D, this.posZ - this.motionZ);
			} else if (this.target != null && !this.target.isDead) {
				if (this.steps > 0) {
					--this.steps;

					if (this.steps == 0) {
						this.selectNextMoveDirection(this.direction);
					}
				}

				if (this.direction != null) {
					BlockPos blockpos = new BlockPos(this);
					EnumFacing enumfacing$axis = this.direction;

					if (this.worldObj.isBlockNormalCubeDefault(blockpos.getX() + direction.getFrontOffsetX(), blockpos.getY() + direction.getFrontOffsetY(),
							blockpos.getZ() + direction.getFrontOffsetZ(), false)) {
						this.selectNextMoveDirection(enumfacing$axis);
					} else {
						BlockPos blockpos1 = new BlockPos(this.target);

						if ((enumfacing$axis.getFrontOffsetX() != 0 && blockpos.getX() == blockpos1.getX()) || (enumfacing$axis.getFrontOffsetY() != 0 && blockpos.getY() == blockpos1.getY()) || (enumfacing$axis.getFrontOffsetZ() != 0 && blockpos.getZ() == blockpos1.getZ())) {
							this.selectNextMoveDirection(enumfacing$axis);
						}
					}
				}
			}
		}
	}

	@Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements) {
		this.setPosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	/**
	 * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
	 */
	@Override
	public boolean isBurning() {
		return false;
	}

	/**
	 * Checks if the entity is in range to render.
	 */
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return distance < 16384.0D;
	}

	/**
	 * Gets how bright this entity is.
	 */
	@Override
	public float getBrightness(float partialTicks) {
		return 1F;
	}

	@Override
	public int getBrightnessForRender(float partialTicks) {
		return 15728880;
	}

	protected void bulletHit(MovingObjectPosition result) {
		if (result.entityHit == null) {
			this.worldObj.spawnParticle("largeexplode", this.posX + .5D, this.posY + .5D, this.posZ + .5D, 0.2D, 0.2D, 0.2D);
			this.playSound(Tags.MOD_ID + ":entity.shulker_bullet.hit", 1, 1);
		} else if (!worldObj.isRemote) {
			boolean flag = result.entityHit.attackEntityFrom(new EntityDamageSourceIndirect("mob", this, this.owner), 4.0F);

			if (flag) {
//                this.applyEnchantments(this.owner entityLivingBaseIn, result.entityHit entityIn);

				if (result.entityHit instanceof EntityLivingBase) {
					((EntityLivingBase) result.entityHit).addPotionEffect(new PotionEffect(ModPotions.levitation.getId(), 10 * 20));
				}
			}
		}

		this.setDead();
	}

	/**
	 * Returns true if other Entities should be prevented from moving through this Entity.
	 */
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	/**
	 * Called when the entity is attacked.
	 */
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		for (int i = 0; i < 12; ++i) {
			float bound = width + 0.15F;
			this.worldObj.spawnParticle("crit", this.posX + (double) (this.rand.nextFloat() * bound * 2.0F) - (double) bound, this.posY + (double) (this.rand.nextFloat() * bound), this.posZ + (double) (this.rand.nextFloat() * bound * 2.0F) - (double) bound, 0, 0, 0);
		}
		if (!this.worldObj.isRemote) {
			this.playSound(Tags.MOD_ID + ":entity.shulker_bullet.hurt", 1.0F, 1.0F);
			this.setDead();
		}

		return true;
	}
}
