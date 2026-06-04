package ganymedes01.etfuturum.entities;

import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.entities.ai.EntityAIMoveToBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarrot;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Arrays;
import java.util.List;

public class EntityRabbit extends EntityAnimal {

	private int jumpTicks = 0;
	private int jumpDuration = 0;
	boolean field_175536_bo = false;
	private boolean wasOnGround = false;
	private int currentMoveTypeDuration = 0;
	private EntityRabbit.EnumMoveType moveType;
	int carrotTicks;

	public EntityRabbit(World world) {
		super(world);
		moveType = EntityRabbit.EnumMoveType.HOP;
		carrotTicks = 0;
		setSize(0.4F, 0.5F);
		jumpHelper = new EntityRabbit.RabbitJumpHelper(this);
		moveHelper = new EntityRabbit.RabbitMoveHelper();
		getNavigator().setAvoidsWater(true);
		//        navigator.func_179678_a(2.5F);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityRabbit.AIPanic(1.33D));
		tasks.addTask(2, new EntityAIMate(this, 0.8D));
		tasks.addTask(3, new EntityAITempt(this, 1.0D, Items.carrot, false));
		tasks.addTask(5, new EntityRabbit.AIRaidFarm());
		tasks.addTask(5, new EntityAIWander(this, 0.6D));
		tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
		tasks.addTask(4, new EntityAIAvoidEntity(this, EntityWolf.class, 16.0F, 1.33D, 1.33D));
		tasks.addTask(4, new EntityAIAvoidEntity(this, EntityPlayer.class, 16.0F, 0.8D, 1.33D));
		setMovementSpeed(0.0D);
	}

	@Override
	public EntityMoveHelper getMoveHelper() {
		return super.getMoveHelper();
	}

	@Override
	public EntityJumpHelper getJumpHelper() {
		return super.getJumpHelper();
	}

	public void setMoveType(EntityRabbit.EnumMoveType type) {
		moveType = type;
	}

	public float getJumpCompletion(float p_175521_1_) {
		return jumpDuration == 0 ? 0.0F : (jumpTicks + p_175521_1_) / jumpDuration;
	}

	public void setMovementSpeed(double newSpeed) {
		getNavigator().setSpeed(newSpeed);
		getMoveHelper().setMoveTo(getMoveHelper().posX, getMoveHelper().posY, getMoveHelper().posZ, newSpeed);
	}

	public void setJumping(boolean jump, EntityRabbit.EnumMoveType moveTypeIn) {
		super.setJumping(jump);

		if (!jump) {
			if (moveType == EntityRabbit.EnumMoveType.ATTACK)
				moveType = EntityRabbit.EnumMoveType.HOP;
		} else {
			setMovementSpeed(1.5D * moveTypeIn.getSpeed());
			playSound(getJumpingSound(), getSoundVolume(), ((rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
		}

		field_175536_bo = jump;
	}

	public void doMovementAction(EntityRabbit.EnumMoveType movetype) {
		setJumping(true, movetype);
		jumpDuration = movetype.func_180073_d();
		jumpTicks = 0;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(18, (byte) 0);
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData livingdata) {
		BiomeGenBase biome = worldObj.getBiomeGenForCoords((int) posX, (int) posZ);
		List<BiomeDictionary.Type> types = Arrays.asList(BiomeDictionary.getTypesForBiome(biome));
		if (types.contains(BiomeDictionary.Type.SANDY)) {
			setRabbitType(RabbitTypes.GOLD.ordinal());
		} else if (types.contains(BiomeDictionary.Type.SNOWY)) {
			setRabbitType(rand.nextBoolean() ? RabbitTypes.WHITE.ordinal() : RabbitTypes.WHITE_SPLOTCHED.ordinal());
		} else if (types.contains(BiomeDictionary.Type.PLAINS) || types.contains(BiomeDictionary.Type.FOREST)) {
			int type = rand.nextInt(3);
			switch (type) {
				case 0:
					type = RabbitTypes.BLACK.ordinal();
					break;
				case 1:
					type = RabbitTypes.BROWN.ordinal();
					break;
				case 2:
					type = RabbitTypes.SALT.ordinal();
					break;
			}
			setRabbitType(type);
		} else {
			setRabbitType(rand.nextInt(6));
		}
		return super.onSpawnWithEgg(livingdata);
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	public void updateAITasks() {
		super.updateAITasks();

		if (getMoveHelper().getSpeed() > 0.8D)
			setMoveType(EntityRabbit.EnumMoveType.SPRINT);
		else if (moveType != EntityRabbit.EnumMoveType.ATTACK)
			setMoveType(EntityRabbit.EnumMoveType.HOP);

		if (currentMoveTypeDuration > 0)
			currentMoveTypeDuration--;

		if (carrotTicks > 0) {
			carrotTicks -= rand.nextInt(3);

			if (carrotTicks < 0)
				carrotTicks = 0;
		}

		if (onGround) {
			if (!wasOnGround) {
				setJumping(false, EntityRabbit.EnumMoveType.NONE);
				checkLandingDelay();
			}

			EntityJumpHelper jumpHelper = getJumpHelper();

			if (jumpHelper instanceof RabbitJumpHelper rabbitJumpHelper) {
				if (!rabbitJumpHelper.getIsJumping()) {
					if (!getNavigator().noPath() && currentMoveTypeDuration == 0) {
						PathEntity pathentity = getNavigator().getPath();
						Vec3 vec3 = Vec3.createVectorHelper(getMoveHelper().posX, getMoveHelper().posY, getMoveHelper().posZ);

						if (pathentity != null && pathentity.getCurrentPathIndex() < pathentity.getCurrentPathLength())
							vec3 = pathentity.getPosition(this);

						calculateRotationYaw(vec3.xCoord, vec3.zCoord);
						doMovementAction(moveType);
					}
				} else if (!rabbitJumpHelper.canJump()) {
					enableJumpControl();
				}
			}
		}

		wasOnGround = onGround;
	}

	private void calculateRotationYaw(double p_175533_1_, double p_175533_3_) {
		rotationYaw = (float) (Math.atan2(p_175533_3_ - posZ, p_175533_1_ - posX) * 180.0D / Math.PI) - 90.0F;
	}

	private void enableJumpControl() {
		if (getJumpHelper() instanceof RabbitJumpHelper rabbitJumpHelper) {
			rabbitJumpHelper.setCanJump(true);
		}
	}

	private void disableJumpControl() {
		if (getJumpHelper() instanceof RabbitJumpHelper rabbitJumpHelper) {
			rabbitJumpHelper.setCanJump(false);
		}
	}

	private void updateMoveTypeDuration() {
		currentMoveTypeDuration = getMoveTypeDuration();
	}

	private void checkLandingDelay() {
		updateMoveTypeDuration();
		disableJumpControl();
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (jumpTicks != jumpDuration) {
			if (jumpTicks == 0 && !worldObj.isRemote)
				worldObj.setEntityState(this, (byte) 1);

			jumpTicks++;
		} else if (jumpDuration != 0) {
			jumpTicks = 0;
			jumpDuration = 0;
		}
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(3.0);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("RabbitType", getRabbitType());
		nbt.setInteger("MoreCarrotTicks", carrotTicks);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setRabbitType(nbt.getInteger("RabbitType"));
		carrotTicks = nbt.getInteger("MoreCarrotTicks");
	}

	protected String getJumpingSound() {
		return Tags.MC_ASSET_VER + ":entity.rabbit.jump";
	}

	@Override
	protected String getLivingSound() {
		return Tags.MC_ASSET_VER + ":entity.rabbit.ambient";
	}

	@Override
	protected String getHurtSound() {
		return Tags.MC_ASSET_VER + ":entity.rabbit.hurt";
	}

	@Override
	protected String getDeathSound() {
		return Tags.MC_ASSET_VER + ":entity.rabbit.death";
	}

	@Override
	protected void dropFewItems(boolean hitRencently, int fortune) {
		int j = rand.nextInt(2) + rand.nextInt(1 + fortune);

		for (int i = 0; i < j; i++)
			dropItem(ModItems.RABBIT_HIDE.get(), 1);

		j = rand.nextInt(2);

		for (int i = 0; i < j; i++)
			if (isBurning())
				dropItem(ModItems.RABBIT_COOKED.get(), 1);
			else
				dropItem(ModItems.RABBIT_RAW.get(), 1);

		if (rand.nextInt(100) <= 10 + fortune)
			entityDropItem(new ItemStack(ModItems.RABBIT_FOOT.get()), 0.0F);
	}

	private boolean isRabbitBreedingItem(Item item) {
		return item == Items.carrot || item == Items.golden_carrot || item == Item.getItemFromBlock(Blocks.yellow_flower);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack != null && isRabbitBreedingItem(stack.getItem());
	}

	public byte getRabbitType() {
		return dataWatcher.getWatchableObjectByte(18);
	}

	public void setRabbitType(int type) {
		dataWatcher.updateObject(18, (byte) type);
	}

	@Override
	public EntityAgeable createChild(EntityAgeable mate) {
		EntityRabbit baby = new EntityRabbit(worldObj);
		if (mate instanceof EntityRabbit)
			baby.setRabbitType(rand.nextBoolean() ? getRabbitType() : ((EntityRabbit) mate).getRabbitType());
		return baby;
	}

	boolean isCarrotEaten() {
		return carrotTicks == 0;
	}

	protected int getMoveTypeDuration() {
		return moveType.getDuration();
	}

	protected void createRunningParticles() {
		//        int i = MathHelper.floor_double(posX);
		//        int j = MathHelper.floor_double(posY - 0.20000000298023224D);
		//        int k = MathHelper.floor_double(posZ);
		//        BlockPos blockpos = new BlockPos(i, j, k);
		//        IBlockState iblockstate = worldObj.getBlockState(blockpos);
		//        Block block = iblockstate.getBlock();
		//
		//        if (block.getRenderType() != -1)
		//            worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX + (rand.nextFloat() - 0.5D) * width, this.getEntityBoundingBox().minY + 0.1D, posZ + (rand.nextFloat() - 0.5D) * width, -motionX * 4.0D, 1.5D, -motionZ * 4.0D, new int[] { Block.getStateId(iblockstate) });
	}

	@Override
	public void handleHealthUpdate(byte id) {
		if (id == 1) {
			createRunningParticles();
			jumpDuration = 10;
			jumpTicks = 0;
		} else
			super.handleHealthUpdate(id);
	}

	class AIEvilAttack extends EntityAIAttackOnCollide {

		public AIEvilAttack() {
			super(EntityRabbit.this, EntityLivingBase.class, 1.4D, true);
		}

		protected double getAttackReachSqr(EntityLivingBase attackTarget) {
			return 4.0F + attackTarget.width;
		}
	}

	class AIPanic extends EntityAIPanic {

		private final double speed;
		private final EntityRabbit theEntity = EntityRabbit.this;

		public AIPanic(double speed) {
			super(EntityRabbit.this, speed);
			this.speed = speed;
		}

		@Override
		public void updateTask() {
			super.updateTask();
			theEntity.setMovementSpeed(speed);
		}
	}

	enum EnumMoveType {
		NONE(0.0F, 0.0F, 30, 1),
		HOP(0.8F, 0.2F, 20, 10),
		STEP(1.0F, 0.45F, 14, 14),
		SPRINT(1.75F, 0.4F, 1, 8),
		ATTACK(2.0F, 0.7F, 7, 8);
		private final float speed;
		private final float field_180077_g;
		private final int duration;
		private final int field_180085_i;

		EnumMoveType(float typeSpeed, float p_i45866_4_, int typeDuration, int p_i45866_6_) {
			speed = typeSpeed;
			field_180077_g = p_i45866_4_;
			duration = typeDuration;
			field_180085_i = p_i45866_6_;
		}

		public float getSpeed() {
			return speed;
		}

		public float func_180074_b() {
			return field_180077_g;
		}

		public int getDuration() {
			return duration;
		}

		public int func_180073_d() {
			return field_180085_i;
		}
	}

	public class RabbitJumpHelper extends EntityJumpHelper {

		private final EntityRabbit theEntity;
		private boolean field_180068_d = false;

		public RabbitJumpHelper(EntityRabbit rabbit) {
			super(rabbit);
			theEntity = rabbit;
		}

		public boolean getIsJumping() {
			return isJumping;
		}

		public boolean canJump() {
			return field_180068_d;
		}

		public void setCanJump(boolean p_180066_1_) {
			field_180068_d = p_180066_1_;
		}

		@Override
		public void doJump() {
			if (isJumping) {
				theEntity.doMovementAction(EntityRabbit.EnumMoveType.STEP);
				isJumping = false;
			}
		}
	}

	class RabbitMoveHelper extends EntityMoveHelper {

		private final EntityRabbit theEntity = EntityRabbit.this;
		private double posX;
		private double posY;
		private double posZ;

		public RabbitMoveHelper() {
			super(EntityRabbit.this);
		}

		@Override
		public void setMoveTo(double p_75642_1_, double p_75642_3_, double p_75642_5_, double p_75642_7_) {
			super.setMoveTo(p_75642_1_, p_75642_3_, p_75642_5_, p_75642_7_);
			posX = p_75642_1_;
			posY = p_75642_3_;
			posZ = p_75642_5_;
		}

		public double getX() {
			return posX;
		}

		public double getY() {
			return posY;
		}

		public double getZ() {
			return posZ;
		}

		@Override
		public void onUpdateMoveHelper() {
			if (theEntity.onGround && !field_175536_bo)
				theEntity.setMovementSpeed(0.0D);

			super.onUpdateMoveHelper();
		}
	}

	class AIRaidFarm extends EntityAIMoveToBlock {

		private boolean wantsToRaid;
		private boolean canRaid = false;

		public AIRaidFarm() {
			super(EntityRabbit.this, 0.699999988079071D, 16);
		}

		@Override
		public boolean shouldExecute() {
			if (runDelay <= 0) {
				if (!worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
					return false;

				canRaid = false;
				wantsToRaid = isCarrotEaten();
			}

			return super.shouldExecute();
		}

		@Override
		public boolean continueExecuting() {
			return canRaid && super.continueExecuting();
		}

		@Override
		public void updateTask() {
			super.updateTask();
			getLookHelper().setLookPosition(destinationBlock.getX() + 0.5D, destinationBlock.getY() + 1, destinationBlock.getZ() + 0.5D, 10.0F, getVerticalFaceSpeed());

			if (getIsAboveDestination()) {
				World world = worldObj;
				BlockPos blockpos = destinationBlock.up();
				Block block = world.getBlock(blockpos.getX(), blockpos.getY(), blockpos.getZ());
				int meta = world.getBlockMetadata(blockpos.getX(), blockpos.getY(), blockpos.getZ());

				if (canRaid && block instanceof BlockCarrot && meta >= 7) {
					world.func_147480_a/*breakBlock*/(blockpos.getX(), blockpos.getY(), blockpos.getZ(), false);
					carrotTicks = 100;
				}

				canRaid = false;
				runDelay = 10;
			}
		}

		@Override
		protected boolean shouldMoveTo(World world, BlockPos pos) {
			pos = pos.up();
			Block block = world.getBlock(pos.getX(), pos.getY(), pos.getZ());
			int meta = world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ());

			if (block instanceof BlockCarrot && meta >= 7 && wantsToRaid && !canRaid) {
				canRaid = true;
				return true;
			}

			return false;
		}
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		return ModEntityList.getEggFromEntity(this);
	}

	private enum RabbitTypes {
		BROWN,
		WHITE,
		BLACK,
		WHITE_SPLOTCHED,
		GOLD,
		SALT
	}
}