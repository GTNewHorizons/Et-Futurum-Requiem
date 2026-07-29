package ganymedes01.etfuturum.entities;

import java.util.List;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityPanda extends EntityAnimal {

	private static final int MAIN_GENE = 18;
	private static final int HIDDEN_GENE = 19;
	private static final int PANDA_FLAGS = 20;
	private static final int UNHAPPY_COUNTER = 21;

	private static final int SITTING_FLAG = 8;
	private static final int EATING_FLAG = 1;
	private static final int EATING_DURATION = 80;
	private static final int UNHAPPY_DURATION = 32;
	private static final int MAX_PICKUP_PURSUIT_TICKS = 200;
	private static final int BAMBOO_SEARCH_RADIUS = 7;
	private static final int BAMBOO_SEARCH_HEIGHT = 3;
	private static final float DEFAULT_EQUIPMENT_DROP_CHANCE = 0.085F;

	private static Item bopBamboo;
	private static Block bopBambooBlock;
	private static boolean bopBambooResolved;

	private int eatingTicks;
	private float sittingAnimationProgress;
	private float previousSittingAnimationProgress;

	public EntityPanda(World world) {
		super(world);
		setSize(1.3F, 1.25F);
		getNavigator().setAvoidsWater(true);

		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIPanic(this, 2.0D));
		tasks.addTask(2, new AIEatBamboo());
		tasks.addTask(3, new AIPandaMate());
		tasks.addTask(4, new EntityAITempt(this, 1.0D, ModItems.BAMBOO.get(), false));

		Item externalBamboo = getBopBamboo();
		if (externalBamboo != null && externalBamboo != ModItems.BAMBOO.get()) {
			tasks.addTask(4, new EntityAITempt(this, 1.0D, externalBamboo, false));
		}

		tasks.addTask(5, new AIPickupBamboo());
		tasks.addTask(6, new EntityAIFollowParent(this, 1.25D));
		tasks.addTask(7, new EntityAIWander(this, 1.0D));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(9, new EntityAILookIdle(this));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(MAIN_GENE, (byte) Gene.NORMAL.getId());
		dataWatcher.addObject(HIDDEN_GENE, (byte) Gene.NORMAL.getId());
		dataWatcher.addObject(PANDA_FLAGS, (byte) 0);
		dataWatcher.addObject(UNHAPPY_COUNTER, 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.15D);
	}

	@Override
	protected boolean isAIEnabled() {
		return true;
	}

	@Override
	public float getEyeHeight() {
		return isChild() ? 0.40625F : super.getEyeHeight();
	}

	@Override
	public void onLivingUpdate() {
		previousSittingAnimationProgress = sittingAnimationProgress;
		sittingAnimationProgress = updateAnimationProgress(sittingAnimationProgress, isSitting(), 0.15F, 0.19F);

		if (worldObj.isRemote) {
			if (isEating()) {
				++eatingTicks;
			} else {
				eatingTicks = 0;
			}
		}

		super.onLivingUpdate();
		updateUnhappyState();
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

		eatingTicks = isBamboo(getHeldItem())
				? Math.max(0, Math.min(EATING_DURATION - 1, nbt.getInteger("PandaEatingTicks")))
				: 0;
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
			if (isBamboo(heldItem)) {
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

		super.handleHealthUpdate(status);
	}

	private void pickUpBamboo(EntityItem entityItem) {
		if (worldObj.isRemote
				|| isChild()
				|| !worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")
				|| entityItem == null
				|| entityItem.isDead
				|| getHeldItem() != null) {
			return;
		}

		ItemStack droppedStack = entityItem.getEntityItem();
		if (!isBamboo(droppedStack) || droppedStack.stackSize <= 0) {
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
		if (isBamboo(heldStack)) {
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

	private class AIPandaMate extends EntityAIMate {

		private int nextUnhappyTick;

		private AIPandaMate() {
			super(EntityPanda.this, 1.0D);
		}

		@Override
		public boolean shouldExecute() {
			if (!super.shouldExecute() || getUnhappyTicks() > 0) {
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
	}

	private class AIEatBamboo extends EntityAIBase {

		private AIEatBamboo() {
			setMutexBits(7);
		}

		@Override
		public boolean shouldExecute() {
			return !isChild() && isBamboo(getHeldItem()) && onGround && !isInWater();
		}

		@Override
		public boolean continueExecuting() {
			return !isChild() && isEating() && isBamboo(getHeldItem()) && onGround && !isInWater()
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
			setPandaEating(false);
			setSitting(false);
		}

		@Override
		public void updateTask() {
			getNavigator().clearPathEntity();
			moveForward = 0.0F;
			moveStrafing = 0.0F;
			++eatingTicks;

			if (eatingTicks >= 20 && eatingTicks % 12 == 0) {
				playSound(Reference.MCAssetVer + ":entity.panda.eat", 0.5F, 0.9F + rand.nextFloat() * 0.2F);
				worldObj.setEntityState(EntityPanda.this, (byte) 45);
			}

			if (eatingTicks >= EATING_DURATION) {
				finishEating();
			}
		}
	}

	private class AIPickupBamboo extends EntityAIBase {

		private EntityItem targetItem;
		private int pathUpdateTicks;
		private int pursuitTicks;
		private int pathFailures;

		private AIPickupBamboo() {
			setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			if (isChild() || getHeldItem() != null || isSitting() || rand.nextInt(10) != 0
					|| !worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) {
				return false;
			}

			targetItem = findNearestBamboo();
			return targetItem != null;
		}

		@Override
		public boolean continueExecuting() {
			return !isChild()
					&& worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")
					&& getHeldItem() == null
					&& targetItem != null
					&& targetItem.isEntityAlive()
					&& targetItem.delayBeforeCanPickup <= 0
					&& isBamboo(targetItem.getEntityItem())
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
				pickUpBamboo(targetItem);
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

		private EntityItem findNearestBamboo() {
			List<EntityItem> items = worldObj.getEntitiesWithinAABB(
					EntityItem.class,
					boundingBox.expand(8.0D, 4.0D, 8.0D));
			EntityItem nearest = null;
			double nearestDistance = Double.MAX_VALUE;

			for (EntityItem item : items) {
				if (item.isDead || item.delayBeforeCanPickup > 0 || !isBamboo(item.getEntityItem())) {
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
