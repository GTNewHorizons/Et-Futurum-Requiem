package ganymedes01.etfuturum.entities;

import java.util.List;
import java.util.function.Predicate;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.entities.ai.EntityAICustomAvoidEntity;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.spectator.SpectatorMode;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityPanda extends EntityAnimal {

	private static final int MAIN_GENE = 18;
	private static final int HIDDEN_GENE = 19;
	private static final int PANDA_FLAGS = 20;
	private static final int UNHAPPY_COUNTER = 21;
	private static final int SNEEZE_COUNTER = 22;

	private static final int EATING_FLAG = 1;
	private static final int SNEEZING_FLAG = 2;
	private static final int ROLLING_FLAG = 4;
	private static final int SITTING_FLAG = 8;
	private static final int ON_BACK_FLAG = 16;
	private static final int EATING_DURATION = 80;
	private static final int SNEEZE_DURATION = 20;
	private static final int ROLL_DURATION = 32;
	private static final int LAZY_MOVEMENT_LERP_TICKS = 9;
	private static final int ATTACK_COOLDOWN = 20;
	private static final int UNHAPPY_DURATION = 32;
	private static final int MAX_PICKUP_PURSUIT_TICKS = 200;
	private static final int BAMBOO_SEARCH_RADIUS = 7;
	private static final int BAMBOO_SEARCH_HEIGHT = 3;
	private static final float DEFAULT_EQUIPMENT_DROP_CHANCE = 0.085F;
	private static final double LAZY_LIE_START_CHANCE = 0.0075D;
	private static final double BABY_ROLL_START_CHANCE = 0.006D;
	private static final double PLAYFUL_ROLL_START_CHANCE = 0.0559D;
	private static final double LAZY_MOVEMENT_LERP_MAX_DISTANCE_SQ = 0.01D;
	private static final double LAZY_MOVEMENT_LERP_MAX_HEIGHT = 0.0625D;

	private static Item bopBamboo;
	private static Block bopBambooBlock;
	private static boolean bopBambooResolved;

	private int eatingTicks;
	private int rollTicks;
	private Vec3 rollDelta;
	private float sittingAnimationProgress;
	private float previousSittingAnimationProgress;
	private float onBackAnimationProgress;
	private float previousOnBackAnimationProgress;
	private float rollAnimationProgress;
	private float previousRollAnimationProgress;
	private boolean stopAttackingAfterHit;
	private boolean pacifiedByBamboo;
	private int attackCooldown;

	public EntityPanda(World world) {
		super(world);
		setSize(1.3F, 1.25F);
		getNavigator().setAvoidsWater(true);
		moveHelper = new PandaMoveHelper();

		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new AIPandaPanic());
		tasks.addTask(2, new AIEatFood());
		tasks.addTask(3, new AIPandaAttack());
		tasks.addTask(3, new AIPandaMate());
		tasks.addTask(4, new AIPandaTempt(ModItems.BAMBOO.get()));

		Item externalBamboo = getBopBamboo();
		if (externalBamboo != null && externalBamboo != ModItems.BAMBOO.get()) {
			tasks.addTask(4, new AIPandaTempt(externalBamboo));
		}

		tasks.addTask(5, new AIPickupFood());
		tasks.addTask(6, new AIPandaAvoidEntity(EntityPlayer.class, 8.0F, EntityPanda::canWorriedPandaAvoid));
		tasks.addTask(6, new AIPandaAvoidEntity(EntityMob.class, 4.0F, entity -> true));
		tasks.addTask(8, new AIPandaLieOnBack());
		tasks.addTask(9, new AIPandaWatchClosest());
		tasks.addTask(10, new AIPandaLookIdle());
		tasks.addTask(12, new AIPandaRoll());
		tasks.addTask(13, new AIPandaFollowParent());
		tasks.addTask(14, new AIPandaWander());
		targetTasks.addTask(1, new AIPandaHurtByTarget());
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(MAIN_GENE, (byte) Gene.NORMAL.getId());
		dataWatcher.addObject(HIDDEN_GENE, (byte) Gene.NORMAL.getId());
		dataWatcher.addObject(PANDA_FLAGS, (byte) 0);
		dataWatcher.addObject(UNHAPPY_COUNTER, 0);
		dataWatcher.addObject(SNEEZE_COUNTER, 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.15D);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	public boolean allowLeashing() {
		return false;
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		if (attackCooldown > 0) {
			return false;
		}
		attackCooldown = ATTACK_COOLDOWN;
		playSound(Reference.MCAssetVer + ":entity.panda.bite", 1.0F, 1.0F);
		if (getVariant() != Gene.AGGRESSIVE) {
			stopAttackingAfterHit = true;
		}

		super.attackEntityAsMob(target);
		float damage = (float) getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
		return target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		setSitting(false);
		return super.attackEntityFrom(source, amount);
	}

	@Override
	protected String getLivingSound() {
		if (getVariant() == Gene.AGGRESSIVE) {
			return Reference.MCAssetVer + ":entity.panda.aggressive_ambient";
		}
		if (getVariant() == Gene.WORRIED) {
			return Reference.MCAssetVer + ":entity.panda.worried_ambient";
		}
		return Reference.MCAssetVer + ":entity.panda.ambient";
	}

	@Override
	protected String getHurtSound() {
		return Reference.MCAssetVer + ":entity.panda.hurt";
	}

	@Override
	protected String getDeathSound() {
		return Reference.MCAssetVer + ":entity.panda.death";
	}

	/**
	 * MCP name: {@code playStepSound}
	 */
	@Override
	protected void func_145780_a(int x, int y, int z, Block block) {
		playSound(Reference.MCAssetVer + ":entity.panda.step", 0.15F, 1.0F);
	}

	@Override
	protected void dropFewItems(boolean hitRecently, int lootingLevel) {
		Item bamboo = GameRegistry.findItem(Reference.MOD_ID, "bamboo");
		if (bamboo == null) {
			bamboo = getBopBamboo();
		}
		if (bamboo != null) {
			dropItem(bamboo, 1);
		}
	}

	@Override
	public float getEyeHeight() {
		return isChild() ? 0.40625F : super.getEyeHeight();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(
			double x,
			double y,
			double z,
			float yaw,
			float pitch,
			int rotationIncrements) {
		double xDistance = x - posX;
		double zDistance = z - posZ;
		double horizontalDistanceSq = xDistance * xDistance + zDistance * zDistance;
		if (getVariant() == Gene.LAZY
				&& !isRolling()
				&& horizontalDistanceSq > 1.0E-7D
				&& horizontalDistanceSq <= LAZY_MOVEMENT_LERP_MAX_DISTANCE_SQ
				&& Math.abs(y - posY) <= LAZY_MOVEMENT_LERP_MAX_HEIGHT) {
			rotationIncrements = Math.max(rotationIncrements, LAZY_MOVEMENT_LERP_TICKS);
		}
		super.setPositionAndRotation2(x, y, z, yaw, pitch, rotationIncrements);
	}

	@Override
	public void onLivingUpdate() {
		previousSittingAnimationProgress = sittingAnimationProgress;
		sittingAnimationProgress = updateAnimationProgress(sittingAnimationProgress, isSitting(), 0.15F, 0.19F);
		previousOnBackAnimationProgress = onBackAnimationProgress;
		onBackAnimationProgress = updateAnimationProgress(onBackAnimationProgress, isOnBack(), 0.15F, 0.19F);
		previousRollAnimationProgress = rollAnimationProgress;
		rollAnimationProgress = updateAnimationProgress(rollAnimationProgress, isRolling(), 0.15F, 0.19F);

		if (worldObj.isRemote) {
			if (isEating()) {
				++eatingTicks;
			} else {
				eatingTicks = 0;
			}
		}

		super.onLivingUpdate();
		updateWorriedState();
		updateUnhappyState();
		updateSneezeState();
		updateRollingState();

		if (isSitting()) {
			rotationPitch = 0.0F;
		}

		if (!worldObj.isRemote && getAttackTarget() == null) {
			stopAttackingAfterHit = false;
			pacifiedByBamboo = false;
		}
		if (attackCooldown > 0) {
			--attackCooldown;
		}
	}

	private void updateWorriedState() {
		if (worldObj.isRemote || getVariant() != Gene.WORRIED) {
			return;
		}

		if (worldObj.isThundering() && !isInWater()) {
			if (!isSitting()) {
				getNavigator().clearPathEntity();
			}
			setSitting(true);
			setPandaEating(false);
			eatingTicks = 0;
		} else if (!isEating()) {
			setSitting(false);
		}
	}

	private void updateRollingState() {
		if (!isRolling()) {
			rollTicks = 0;
			rollDelta = null;
			return;
		}

		++rollTicks;
		if (rollTicks > ROLL_DURATION) {
			if (worldObj.isRemote) {
				rollTicks = ROLL_DURATION;
			} else {
				setRolling(false);
			}
			return;
		}
		if (worldObj.isRemote) {
			return;
		}

		if (rollTicks == 1) {
			float yawRadians = rotationYaw * (float) Math.PI / 180.0F;
			float rollSpeed = isChild() ? 0.1F : 0.2F;
			rollDelta = Vec3.createVectorHelper(
					motionX - MathHelper.sin(yawRadians) * rollSpeed,
					0.0D,
					motionZ + MathHelper.cos(yawRadians) * rollSpeed);
			motionX = rollDelta.xCoord;
			motionY = 0.27D;
			motionZ = rollDelta.zCoord;
			isAirBorne = true;
		} else if (rollTicks == 7 || rollTicks == 15 || rollTicks == 23) {
			motionX = 0.0D;
			motionZ = 0.0D;
			if (onGround) {
				motionY = 0.27D;
				isAirBorne = true;
			}
		} else if (rollDelta != null) {
			motionX = rollDelta.xCoord;
			motionZ = rollDelta.zCoord;
		}
	}

	private void updateSneezeState() {
		if (worldObj.isRemote) {
			return;
		}

		if (!isSneezing() && canStartSneezing()) {
			setSneezing(true);
		}
		if (!isSneezing()) {
			return;
		}

		int sneezeTicks = getSneezeTicks() + 1;
		setSneezeTicks(sneezeTicks);
		if (sneezeTicks > SNEEZE_DURATION) {
			setSneezing(false);
			finishSneezing();
		} else if (sneezeTicks == 1) {
			playSound(Reference.MCAssetVer + ":entity.panda.pre_sneeze", 1.0F, 1.0F);
		}
	}

	private boolean canStartSneezing() {
		if (!isChild() || !canPerformPandaAction()) {
			return false;
		}
		if (getVariant() == Gene.WEAK && rand.nextInt(500) == 1) {
			return true;
		}
		return rand.nextInt(6000) == 1;
	}

	private void finishSneezing() {
		worldObj.setEntityState(this, (byte) 47);
		playSound(Reference.MCAssetVer + ":entity.panda.sneeze", 1.0F, 1.0F);

		List<EntityPanda> nearbyPandas = worldObj.getEntitiesWithinAABB(
				EntityPanda.class,
				boundingBox.expand(10.0D, 10.0D, 10.0D));
		for (EntityPanda panda : nearbyPandas) {
			if (!panda.isChild() && panda.onGround && !panda.isInWater() && panda.canPerformPandaAction()) {
				panda.jump();
			}
		}

		if (rand.nextInt(700) == 0 && worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {
			dropItem(Items.slime_ball, 1);
		}
	}

	private void updateUnhappyState() {
		if (worldObj.isRemote) {
			return;
		}

		int unhappyTicks = getUnhappyTicks();
		if (unhappyTicks <= 0) {
			return;
		}

		EntityPlayer player = worldObj.getClosestPlayerToEntity(this, 8.0D);
		if (player != null) {
			faceEntity(player, 90.0F, 90.0F);
		}

		if (unhappyTicks == 29 || unhappyTicks == 14) {
			playSound(Reference.MCAssetVer + ":entity.panda.cant_breed", 1.0F, 1.0F);
		}
		setUnhappyTicks(unhappyTicks - 1);
	}

	private static float updateAnimationProgress(float progress, boolean active, float riseSpeed, float fallSpeed) {
		if (active) {
			return Math.min(1.0F, progress + riseSpeed);
		}
		return Math.max(0.0F, progress - fallSpeed);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return isBamboo(stack);
	}

	@Override
	public boolean interact(EntityPlayer player) {
		if (isOnBack()) {
			if (!worldObj.isRemote) {
				setOnBack(false);
			}
			return true;
		}
		if (isScaredByThunderstorm()) {
			return false;
		}

		ItemStack offeredStack = player.inventory.getCurrentItem();
		if (!isBamboo(offeredStack)) {
			return super.interact(player);
		}

		if (worldObj.isRemote) {
			return true;
		}

		pacifyWithBamboo();

		if (isChild()) {
			consumePlayerItem(player, offeredStack);
			addGrowth((int) (((-getGrowingAge()) / 20) * 0.1F));
			player.swingItem();
			worldObj.setEntityState(this, (byte) 46);
			return true;
		}

		if (getGrowingAge() == 0 && !isInLove()) {
			return super.interact(player);
		}

		if (isSitting() || isInWater() || isBurning()) {
			return false;
		}

		ItemStack previousHeldStack = getHeldItem();
		if (previousHeldStack != null && !player.capabilities.isCreativeMode) {
			entityDropItem(previousHeldStack.copy(), 0.0F);
		}

		ItemStack heldStack = offeredStack.copy();
		heldStack.stackSize = 1;
		consumePlayerItem(player, offeredStack);
		setCurrentItemOrArmor(0, heldStack);
		equipmentDropChances[0] = 2.0F;
		eatingTicks = 0;
		setSitting(true);
		setPandaEating(true);
		getNavigator().clearPathEntity();
		player.swingItem();
		return true;
	}

	private void pacifyWithBamboo() {
		if (getAttackTarget() != null || getAITarget() != null) {
			pacifiedByBamboo = true;
			setAttackTarget(null);
			setRevengeTarget(null);
			getNavigator().clearPathEntity();
		}
	}

	private static void consumePlayerItem(EntityPlayer player, ItemStack stack) {
		if (!player.capabilities.isCreativeMode && --stack.stackSize <= 0) {
			player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
		}
	}

	public static boolean isBamboo(ItemStack stack) {
		if (stack == null || stack.getItem() == null) {
			return false;
		}

		Item item = stack.getItem();
		if (item == ModItems.BAMBOO.get()) {
			return true;
		}

		Item externalBamboo = getBopBamboo();
		return externalBamboo != null && item == externalBamboo;
	}

	private static boolean isPandaFood(ItemStack stack) {
		return isBamboo(stack) || stack != null && stack.getItem() == Items.cake;
	}

	private static Item getBopBamboo() {
		resolveBopBamboo();
		return bopBamboo;
	}

	private static Block getBopBambooBlock() {
		resolveBopBamboo();
		return bopBambooBlock;
	}

	private static void resolveBopBamboo() {
		if (!bopBambooResolved) {
			if (Loader.isModLoaded("BiomesOPlenty")) {
				if (bopBamboo == null) {
					bopBamboo = GameRegistry.findItem("BiomesOPlenty", "bamboo");
				}
				if (bopBambooBlock == null) {
					bopBambooBlock = GameRegistry.findBlock("BiomesOPlenty", "bamboo");
				}
			}
			bopBambooResolved = bopBamboo != null && bopBambooBlock != null
					|| Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION);
		}
	}

	public boolean isSitting() {
		return getPandaFlag(SITTING_FLAG);
	}

	private void setSitting(boolean sitting) {
		setPandaFlag(SITTING_FLAG, sitting);
	}

	@Override
	public boolean isEating() {
		return getPandaFlag(EATING_FLAG);
	}

	private void setPandaEating(boolean eating) {
		setPandaFlag(EATING_FLAG, eating);
	}

	public boolean isRolling() {
		return getPandaFlag(ROLLING_FLAG);
	}

	private void setRolling(boolean rolling) {
		setPandaFlag(ROLLING_FLAG, rolling);
		if (!rolling) {
			rollTicks = 0;
			rollDelta = null;
		}
	}

	public boolean isOnBack() {
		return getPandaFlag(ON_BACK_FLAG);
	}

	private void setOnBack(boolean onBack) {
		setPandaFlag(ON_BACK_FLAG, onBack);
	}

	public boolean isSneezing() {
		return getPandaFlag(SNEEZING_FLAG);
	}

	private void setSneezing(boolean sneezing) {
		setPandaFlag(SNEEZING_FLAG, sneezing);
		if (!sneezing) {
			setSneezeTicks(0);
		}
	}

	public int getSneezeTicks() {
		return dataWatcher.getWatchableObjectInt(SNEEZE_COUNTER);
	}

	private void setSneezeTicks(int ticks) {
		dataWatcher.updateObject(SNEEZE_COUNTER, Math.max(0, ticks));
	}

	private boolean getPandaFlag(int flag) {
		return (dataWatcher.getWatchableObjectByte(PANDA_FLAGS) & flag) != 0;
	}

	private void setPandaFlag(int flag, boolean value) {
		byte flags = dataWatcher.getWatchableObjectByte(PANDA_FLAGS);
		dataWatcher.updateObject(PANDA_FLAGS, value ? (byte) (flags | flag) : (byte) (flags & ~flag));
	}

	public float getSittingAnimationProgress(float partialTick) {
		return previousSittingAnimationProgress
				+ (sittingAnimationProgress - previousSittingAnimationProgress) * partialTick;
	}

	public float getOnBackAnimationProgress(float partialTick) {
		return previousOnBackAnimationProgress
				+ (onBackAnimationProgress - previousOnBackAnimationProgress) * partialTick;
	}

	public float getRollAnimationProgress(float partialTick) {
		return previousRollAnimationProgress
				+ (rollAnimationProgress - previousRollAnimationProgress) * partialTick;
	}

	public int getRollTicks() {
		return rollTicks;
	}

	public boolean isScaredByThunderstorm() {
		return getVariant() == Gene.WORRIED && worldObj.isThundering();
	}

	public int getEatingTicks() {
		return eatingTicks;
	}

	public int getUnhappyTicks() {
		return dataWatcher.getWatchableObjectInt(UNHAPPY_COUNTER);
	}

	private void setUnhappyTicks(int ticks) {
		dataWatcher.updateObject(UNHAPPY_COUNTER, Math.max(0, ticks));
	}

	public Gene getMainGene() {
		return Gene.byId(dataWatcher.getWatchableObjectByte(MAIN_GENE));
	}

	public void setMainGene(Gene gene) {
		dataWatcher.updateObject(MAIN_GENE, (byte) Gene.orNormal(gene).getId());
	}

	public Gene getHiddenGene() {
		return Gene.byId(dataWatcher.getWatchableObjectByte(HIDDEN_GENE));
	}

	public void setHiddenGene(Gene gene) {
		dataWatcher.updateObject(HIDDEN_GENE, (byte) Gene.orNormal(gene).getId());
	}

	public Gene getVariant() {
		Gene mainGene = getMainGene();
		return mainGene.isRecessive() && mainGene != getHiddenGene() ? Gene.NORMAL : mainGene;
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData livingData) {
		IEntityLivingData spawnData = super.onSpawnWithEgg(livingData);
		setMainGene(getRandomGene());
		setHiddenGene(getRandomGene());
		applyGeneAttributes();
		return spawnData;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setString("MainGene", getMainGene().getName());
		nbt.setString("HiddenGene", getHiddenGene().getName());
		if (isEating()) {
			nbt.setInteger("PandaEatingTicks", eatingTicks);
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setMainGene(Gene.byName(nbt.getString("MainGene")));
		setHiddenGene(Gene.byName(nbt.getString("HiddenGene")));
		applyGeneAttributes();

		eatingTicks = isPandaFood(getHeldItem())
				? Math.max(0, Math.min(EATING_DURATION - 1, nbt.getInteger("PandaEatingTicks")))
				: 0;
		setSneezing(false);
		setRolling(false);
		setOnBack(false);
		setSitting(false);
		setPandaEating(false);
	}

	@Override
	public EntityPanda createChild(EntityAgeable mate) {
		EntityPanda child = new EntityPanda(worldObj);
		child.inheritGenes(this, mate instanceof EntityPanda ? (EntityPanda) mate : null);
		child.applyGeneAttributes();
		return child;
	}

	private void applyGeneAttributes() {
		getEntityAttribute(SharedMonsterAttributes.maxHealth)
				.setBaseValue(getVariant() == Gene.WEAK ? 10.0D : 20.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed)
				.setBaseValue(getVariant() == Gene.LAZY ? 0.07D : 0.15D);
		setHealth(Math.min(getHealth(), getMaxHealth()));
	}

	private void inheritGenes(EntityPanda mother, EntityPanda father) {
		if (father == null) {
			if (rand.nextBoolean()) {
				setMainGene(mother.getOneOfGenesRandomly());
				setHiddenGene(getRandomGene());
			} else {
				setMainGene(getRandomGene());
				setHiddenGene(mother.getOneOfGenesRandomly());
			}
		} else if (rand.nextBoolean()) {
			setMainGene(mother.getOneOfGenesRandomly());
			setHiddenGene(father.getOneOfGenesRandomly());
		} else {
			setMainGene(father.getOneOfGenesRandomly());
			setHiddenGene(mother.getOneOfGenesRandomly());
		}

		if (rand.nextInt(32) == 0) {
			setMainGene(getRandomGene());
		}
		if (rand.nextInt(32) == 0) {
			setHiddenGene(getRandomGene());
		}
	}

	private Gene getOneOfGenesRandomly() {
		return rand.nextBoolean() ? getMainGene() : getHiddenGene();
	}

	private Gene getRandomGene() {
		int value = rand.nextInt(16);
		if (value == 0) {
			return Gene.LAZY;
		}
		if (value == 1) {
			return Gene.WORRIED;
		}
		if (value == 2) {
			return Gene.PLAYFUL;
		}
		if (value == 4) {
			return Gene.AGGRESSIVE;
		}
		if (value < 9) {
			return Gene.WEAK;
		}
		return value < 11 ? Gene.BROWN : Gene.NORMAL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleHealthUpdate(byte status) {
		if (status == 45) {
			ItemStack heldItem = getHeldItem();
			if (isPandaFood(heldItem)) {
				String particle = "iconcrack_" + Item.getIdFromItem(heldItem.getItem());
				if (heldItem.getHasSubtypes()) {
					particle += "_" + heldItem.getItemDamage();
				}

				for (int i = 0; i < 6; ++i) {
					double xSpeed = (rand.nextDouble() - 0.5D) * 0.08D;
					double ySpeed = rand.nextDouble() * 0.08D + 0.04D;
					double zSpeed = (rand.nextDouble() - 0.5D) * 0.08D;
					worldObj.spawnParticle(
							particle,
							posX + (rand.nextDouble() - 0.5D) * 0.35D,
							posY + 0.75D + rand.nextDouble() * 0.15D,
							posZ + (rand.nextDouble() - 0.5D) * 0.35D,
							xSpeed,
							ySpeed,
							zSpeed);
				}
			}
			return;
		}
		if (status == 46) {
			for (int i = 0; i < 3; ++i) {
				double xSpeed = rand.nextGaussian() * 0.02D;
				double ySpeed = rand.nextGaussian() * 0.02D;
				double zSpeed = rand.nextGaussian() * 0.02D;
				worldObj.spawnParticle(
						"happyVillager",
						posX + rand.nextFloat() * 0.5D,
						posY + 0.5D + rand.nextFloat() * 0.5D,
						posZ + rand.nextFloat() * 0.5D,
						xSpeed,
						ySpeed,
						zSpeed);
			}
			return;
		}
		if (status == 47) {
			float yawRadians = renderYawOffset * (float) Math.PI / 180.0F;
			double distance = (width + 1.0F) * 0.5D;
			worldObj.spawnParticle(
					"slime",
					posX - distance * MathHelper.sin(yawRadians),
					posY + getEyeHeight() - 0.1D,
					posZ + distance * MathHelper.cos(yawRadians),
					motionX,
					0.0D,
					motionZ);
			return;
		}

		super.handleHealthUpdate(status);
	}

	private void pickUpFood(EntityItem entityItem) {
		if (worldObj.isRemote
				|| isChild()
				|| getVariant() == Gene.WORRIED
				|| !canPerformPandaAction()
				|| !worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")
				|| entityItem == null
				|| entityItem.isDead
				|| getHeldItem() != null) {
			return;
		}

		ItemStack droppedStack = entityItem.getEntityItem();
		if (!isPandaFood(droppedStack) || droppedStack.stackSize <= 0) {
			return;
		}

		ItemStack heldStack = droppedStack.copy();
		heldStack.stackSize = 1;
		setCurrentItemOrArmor(0, heldStack);
		equipmentDropChances[0] = 2.0F;

		if (droppedStack.stackSize == 1) {
			onItemPickup(entityItem, 1);
			entityItem.setDead();
		} else {
			ItemStack remainder = droppedStack.copy();
			remainder.stackSize = droppedStack.stackSize - 1;
			entityItem.setEntityItemStack(remainder);
		}
	}

	private void finishEating() {
		ItemStack heldStack = getHeldItem();
		if (isPandaFood(heldStack)) {
			--heldStack.stackSize;
			setCurrentItemOrArmor(0, heldStack.stackSize > 0 ? heldStack : null);
		}
		if (getHeldItem() == null) {
			equipmentDropChances[0] = DEFAULT_EQUIPMENT_DROP_CHANCE;
		}

		eatingTicks = 0;
		setPandaEating(false);
		setSitting(false);
	}

	private boolean hasNearbyBamboo() {
		int originX = MathHelper.floor_double(posX);
		int originY = MathHelper.floor_double(posY);
		int originZ = MathHelper.floor_double(posZ);
		Block externalBamboo = getBopBambooBlock();

		for (int x = -BAMBOO_SEARCH_RADIUS; x <= BAMBOO_SEARCH_RADIUS; ++x) {
			for (int z = -BAMBOO_SEARCH_RADIUS; z <= BAMBOO_SEARCH_RADIUS; ++z) {
				int blockX = originX + x;
				int blockZ = originZ + z;
				if (!worldObj.blockExists(blockX, originY, blockZ)) {
					continue;
				}

				for (int y = 0; y < BAMBOO_SEARCH_HEIGHT; ++y) {
					Block block = worldObj.getBlock(originX + x, originY + y, originZ + z);
					if (block == ModBlocks.BAMBOO.get() || externalBamboo != null && block == externalBamboo) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean canPerformAttack() {
		return !isBurning() && canPerformPandaAction();
	}

	private boolean canPerformPandaAction() {
		return !isOnBack()
				&& !isScaredByThunderstorm()
				&& !isEating()
				&& !isRolling()
				&& !isSitting();
	}

	private static boolean canWorriedPandaAvoid(EntityLivingBase entity) {
		if (!(entity instanceof EntityPlayer)) {
			return true;
		}
		EntityPlayer player = (EntityPlayer) entity;
		return !SpectatorMode.isSpectator(player);
	}

	private class PandaMoveHelper extends EntityMoveHelper {

		private PandaMoveHelper() {
			super(EntityPanda.this);
		}

		@Override
		public void onUpdateMoveHelper() {
			if (canPerformPandaAction()) {
				super.onUpdateMoveHelper();
				return;
			}

			setMoveTo(EntityPanda.this.posX, EntityPanda.this.posY, EntityPanda.this.posZ, 0.0D);
			EntityPanda.this.setMoveForward(0.0F);
			EntityPanda.this.moveStrafing = 0.0F;
		}
	}

	private class AIPandaAvoidEntity extends EntityAICustomAvoidEntity {

		private AIPandaAvoidEntity(
				Class<? extends Entity> targetClass,
				float distance,
				Predicate<EntityLivingBase> selector) {
			super(EntityPanda.this, targetClass, distance, 2.0D, 2.0D, selector);
		}

		@Override
		public boolean shouldExecute() {
			return getVariant() == Gene.WORRIED && canPerformPandaAction() && super.shouldExecute();
		}

		@Override
		public boolean continueExecuting() {
			return getVariant() == Gene.WORRIED && canPerformPandaAction() && super.continueExecuting();
		}
	}

	private class AIPandaTempt extends EntityAITempt {

		private AIPandaTempt(Item item) {
			super(EntityPanda.this, 1.0D, item, false);
		}

		@Override
		public boolean shouldExecute() {
			return canPerformPandaAction() && super.shouldExecute();
		}

		@Override
		public boolean continueExecuting() {
			return canPerformPandaAction() && super.continueExecuting();
		}
	}

	private class AIPandaFollowParent extends EntityAIFollowParent {

		private AIPandaFollowParent() {
			super(EntityPanda.this, 1.25D);
		}

		@Override
		public boolean shouldExecute() {
			return canPerformPandaAction() && super.shouldExecute();
		}

		@Override
		public boolean continueExecuting() {
			return canPerformPandaAction() && super.continueExecuting();
		}
	}

	private class AIPandaWander extends EntityAIWander {

		private AIPandaWander() {
			super(EntityPanda.this, 1.0D);
		}

		@Override
		public boolean shouldExecute() {
			return canPerformPandaAction() && super.shouldExecute();
		}

		@Override
		public boolean continueExecuting() {
			return canPerformPandaAction() && super.continueExecuting();
		}
	}

	private class AIPandaWatchClosest extends EntityAIWatchClosest {

		private AIPandaWatchClosest() {
			super(EntityPanda.this, EntityPlayer.class, 6.0F);
		}

		@Override
		public boolean shouldExecute() {
			return canPerformPandaAction() && super.shouldExecute();
		}

		@Override
		public boolean continueExecuting() {
			return canPerformPandaAction() && super.continueExecuting();
		}
	}

	private class AIPandaLookIdle extends EntityAILookIdle {

		private AIPandaLookIdle() {
			super(EntityPanda.this);
		}

		@Override
		public boolean shouldExecute() {
			return canPerformPandaAction() && super.shouldExecute();
		}

		@Override
		public boolean continueExecuting() {
			return canPerformPandaAction() && super.continueExecuting();
		}
	}

	private class AIPandaLieOnBack extends EntityAIBase {

		private int nextLieOnBackTick;

		@Override
		public boolean shouldExecute() {
			return nextLieOnBackTick < ticksExisted
					&& getVariant() == Gene.LAZY
					&& canPerformPandaAction()
					&& rand.nextDouble() < LAZY_LIE_START_CHANCE;
		}

		@Override
		public boolean continueExecuting() {
			if (!isOnBack() || isInWater()) {
				return false;
			}
			if (getVariant() != Gene.LAZY && rand.nextInt(600) == 1) {
				return false;
			}
			return rand.nextInt(2000) != 1;
		}

		@Override
		public void startExecuting() {
			if (!canPerformPandaAction()) {
				return;
			}
			setOnBack(true);
			nextLieOnBackTick = 0;
		}

		@Override
		public void resetTask() {
			setOnBack(false);
			nextLieOnBackTick = ticksExisted + 200;
		}
	}

	private class AIPandaRoll extends EntityAIBase {

		private AIPandaRoll() {
			setMutexBits(7);
		}

		@Override
		public boolean shouldExecute() {
			if ((!isChild() && getVariant() != Gene.PLAYFUL) || !onGround || !canPerformPandaAction()) {
				return false;
			}
			if (isAirAheadAndBelow()) {
				return true;
			}

			double chance = getVariant() == Gene.PLAYFUL
					? PLAYFUL_ROLL_START_CHANCE
					: BABY_ROLL_START_CHANCE;
			return rand.nextDouble() < chance;
		}

		@Override
		public boolean continueExecuting() {
			return false;
		}

		@Override
		public void startExecuting() {
			if (!canPerformPandaAction()) {
				return;
			}
			getNavigator().clearPathEntity();
			setRolling(true);
		}

		@Override
		public boolean isInterruptible() {
			return false;
		}

		private boolean isAirAheadAndBelow() {
			float yawRadians = rotationYaw * (float) Math.PI / 180.0F;
			float forwardX = -MathHelper.sin(yawRadians);
			float forwardZ = MathHelper.cos(yawRadians);
			int offsetX = Math.abs(forwardX) > 0.5F ? (int) (forwardX / Math.abs(forwardX)) : 0;
			int offsetZ = Math.abs(forwardZ) > 0.5F ? (int) (forwardZ / Math.abs(forwardZ)) : 0;
			return worldObj.isAirBlock(
					MathHelper.floor_double(posX) + offsetX,
					MathHelper.floor_double(posY) - 1,
					MathHelper.floor_double(posZ) + offsetZ);
		}
	}

	private class AIPandaPanic extends EntityAIPanic {

		private AIPandaPanic() {
			super(EntityPanda.this, 2.0D);
		}

		@Override
		public boolean shouldExecute() {
			return isBurning() && super.shouldExecute();
		}
	}

	private class AIPandaAttack extends EntityAIAttackOnCollide {

		private AIPandaAttack() {
			super(EntityPanda.this, 1.2D, true);
		}

		@Override
		public boolean shouldExecute() {
			return canPerformAttack() && super.shouldExecute();
		}

		@Override
		public boolean continueExecuting() {
			return canPerformAttack() && super.continueExecuting();
		}
	}

	private class AIPandaHurtByTarget extends EntityAIHurtByTarget {

		private AIPandaHurtByTarget() {
			super(EntityPanda.this, false);
		}

		@Override
		public void startExecuting() {
			stopAttackingAfterHit = false;
			pacifiedByBamboo = false;
			super.startExecuting();
		}

		@Override
		public boolean continueExecuting() {
			if (stopAttackingAfterHit || pacifiedByBamboo) {
				setAttackTarget(null);
				setRevengeTarget(null);
				return false;
			}
			return super.continueExecuting();
		}
	}

	private class AIPandaMate extends EntityAIMate {

		private int nextUnhappyTick;

		private AIPandaMate() {
			super(EntityPanda.this, 1.0D);
		}

		@Override
		public boolean shouldExecute() {
			if (!canPerformPandaAction() || !super.shouldExecute() || getUnhappyTicks() > 0) {
				return false;
			}
			if (hasNearbyBamboo()) {
				return true;
			}
			if (nextUnhappyTick <= ticksExisted) {
				setUnhappyTicks(UNHAPPY_DURATION);
				nextUnhappyTick = ticksExisted + 600;
			}
			return false;
		}

		@Override
		public boolean continueExecuting() {
			return canPerformPandaAction() && super.continueExecuting();
		}
	}

	private class AIEatFood extends EntityAIBase {

		private AIEatFood() {
			setMutexBits(7);
		}

		@Override
		public boolean shouldExecute() {
			return !isChild()
					&& !isOnBack()
					&& !isRolling()
					&& !isScaredByThunderstorm()
					&& isPandaFood(getHeldItem())
					&& !isInWater();
		}

		@Override
		public boolean continueExecuting() {
			return !isChild()
					&& !isOnBack()
					&& !isRolling()
					&& !isScaredByThunderstorm()
					&& isEating()
					&& isPandaFood(getHeldItem())
					&& !isInWater()
					&& eatingTicks < EATING_DURATION;
		}

		@Override
		public void startExecuting() {
			if (eatingTicks <= 0 || eatingTicks >= EATING_DURATION) {
				eatingTicks = 0;
			}
			setSitting(true);
			setPandaEating(true);
			getNavigator().clearPathEntity();
		}

		@Override
		public void resetTask() {
			if (!worldObj.isRemote && (isBurning() || isInWater()) && isPandaFood(getHeldItem())) {
				ItemStack heldStack = getHeldItem().copy();
				entityDropItem(heldStack, 0.0F);
				setCurrentItemOrArmor(0, null);
				equipmentDropChances[0] = DEFAULT_EQUIPMENT_DROP_CHANCE;
				eatingTicks = 0;
			}
			setPandaEating(false);
			setSitting(false);
		}

		@Override
		public void updateTask() {
			getNavigator().clearPathEntity();
			moveForward = 0.0F;
			moveStrafing = 0.0F;
			setSitting(true);
			++eatingTicks;

			if (eatingTicks >= 20 && eatingTicks % 12 == 0) {
				float volume = 0.5F + 0.5F * rand.nextInt(2);
				float pitch = 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F;
				playSound(Reference.MCAssetVer + ":entity.panda.eat", volume, pitch);
				worldObj.setEntityState(EntityPanda.this, (byte) 45);
			}

			if (eatingTicks >= EATING_DURATION) {
				finishEating();
			}
		}
	}

	private class AIPickupFood extends EntityAIBase {

		private EntityItem targetItem;
		private int pathUpdateTicks;
		private int pursuitTicks;
		private int pathFailures;

		private AIPickupFood() {
			setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			if (isChild()
					|| getVariant() == Gene.WORRIED
					|| getHeldItem() != null
					|| !canPerformPandaAction()
					|| rand.nextInt(10) != 0
					|| !worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
				return false;
			}

			targetItem = findNearestFood();
			return targetItem != null;
		}

		@Override
		public boolean continueExecuting() {
			return !isChild()
					&& getVariant() != Gene.WORRIED
					&& canPerformPandaAction()
					&& worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")
					&& getHeldItem() == null
					&& targetItem != null
					&& targetItem.isEntityAlive()
					&& targetItem.delayBeforeCanPickup <= 0
					&& isPandaFood(targetItem.getEntityItem())
					&& getDistanceSqToEntity(targetItem) <= 160.0D
					&& pursuitTicks < MAX_PICKUP_PURSUIT_TICKS
					&& pathFailures < 3;
		}

		@Override
		public void startExecuting() {
			pathUpdateTicks = 10;
			pursuitTicks = 0;
			pathFailures = 0;
			moveToTarget();
		}

		@Override
		public void resetTask() {
			targetItem = null;
			getNavigator().clearPathEntity();
		}

		@Override
		public void updateTask() {
			if (targetItem == null) {
				return;
			}

			++pursuitTicks;
			if (getDistanceSqToEntity(targetItem) < 2.25D) {
				pickUpFood(targetItem);
				return;
			}

			if (--pathUpdateTicks <= 0) {
				pathUpdateTicks = 10;
				moveToTarget();
			}
		}

		private void moveToTarget() {
			if (targetItem != null) {
				if (getNavigator().tryMoveToEntityLiving(targetItem, 1.2D)) {
					pathFailures = 0;
				} else {
					++pathFailures;
				}
			}
		}

		private EntityItem findNearestFood() {
			List<EntityItem> items = worldObj.getEntitiesWithinAABB(
					EntityItem.class,
					boundingBox.expand(8.0D, 4.0D, 8.0D));
			EntityItem nearest = null;
			double nearestDistance = Double.MAX_VALUE;

			for (EntityItem item : items) {
				if (item.isDead || item.delayBeforeCanPickup > 0 || !isPandaFood(item.getEntityItem())) {
					continue;
				}

				double distance = getDistanceSqToEntity(item);
				if (distance < nearestDistance) {
					nearest = item;
					nearestDistance = distance;
				}
			}
			return nearest;
		}
	}

	public enum Gene {

		NORMAL(0, "normal", false),
		LAZY(1, "lazy", false),
		WORRIED(2, "worried", false),
		PLAYFUL(3, "playful", false),
		BROWN(4, "brown", true),
		WEAK(5, "weak", true),
		AGGRESSIVE(6, "aggressive", false);

		private final int id;
		private final String name;
		private final boolean recessive;

		Gene(int id, String name, boolean recessive) {
			this.id = id;
			this.name = name;
			this.recessive = recessive;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public boolean isRecessive() {
			return recessive;
		}

		public static Gene byId(int id) {
			for (Gene gene : values()) {
				if (gene.id == id) {
					return gene;
				}
			}
			return NORMAL;
		}

		public static Gene byName(String name) {
			if (name != null) {
				for (Gene gene : values()) {
					if (gene.name.equals(name)) {
						return gene;
					}
				}
			}
			return NORMAL;
		}

		private static Gene orNormal(Gene gene) {
			return gene == null ? NORMAL : gene;
		}
	}
}
