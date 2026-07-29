package ganymedes01.etfuturum.entities;

import ganymedes01.etfuturum.ModItems;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityPanda extends EntityAnimal {

	private static final int MAIN_GENE = 18;
	private static final int HIDDEN_GENE = 19;

	public EntityPanda(World world) {
		super(world);
		setSize(1.3F, 1.25F);
		getNavigator().setAvoidsWater(true);

		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIPanic(this, 2.0D));
		tasks.addTask(2, new EntityAIMate(this, 1.0D));
		tasks.addTask(3, new EntityAITempt(this, 1.0D, ModItems.BAMBOO.get(), false));
		tasks.addTask(4, new EntityAIFollowParent(this, 1.25D));
		tasks.addTask(5, new EntityAIWander(this, 1.0D));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(7, new EntityAILookIdle(this));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(MAIN_GENE, (byte) Gene.NORMAL.getId());
		dataWatcher.addObject(HIDDEN_GENE, (byte) Gene.NORMAL.getId());
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
	public boolean isBreedingItem(ItemStack stack) {
		return stack != null && stack.getItem() == ModItems.BAMBOO.get();
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
		setMainGene(getRandomGene());
		setHiddenGene(getRandomGene());
		return super.onSpawnWithEgg(livingData);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setString("MainGene", getMainGene().getName());
		nbt.setString("HiddenGene", getHiddenGene().getName());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setMainGene(Gene.byName(nbt.getString("MainGene")));
		setHiddenGene(Gene.byName(nbt.getString("HiddenGene")));
	}

	@Override
	public EntityPanda createChild(EntityAgeable mate) {
		EntityPanda child = new EntityPanda(worldObj);
		child.inheritGenes(this, mate instanceof EntityPanda ? (EntityPanda) mate : null);
		return child;
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
