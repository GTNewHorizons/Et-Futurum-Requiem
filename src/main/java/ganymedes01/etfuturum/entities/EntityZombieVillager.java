package ganymedes01.etfuturum.entities;

import ganymedes01.etfuturum.Tags;
import net.minecraft.block.Block;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityZombieVillager extends EntityZombie {

	public EntityZombieVillager(World world) {
		super(world);
	}

	public int getType() {
		return getDataWatcher().getWatchableObjectInt(15);
	}

	public void setType(int type) {
		getDataWatcher().updateObject(15, type);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		getDataWatcher().addObject(15, 0);
	}

	@Override
	public boolean isVillager() {
		return true;
	}

	@Override
	public void setChild(boolean isChild) {
	}

	@Override
	public boolean isChild() {
		return false;
	}

	@Override
	protected void convertToVillager() {
		EntityVillager villager = new EntityVillager(worldObj);
		villager.copyLocationAndAnglesFrom(this);
		villager.onSpawnWithEgg(null);
		villager.setLookingForHome();
		villager.setProfession(getType());

		if (isChild())
			villager.setGrowingAge(-24000);

		worldObj.removeEntity(this);
		worldObj.spawnEntityInWorld(villager);
		villager.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 0));
		worldObj.playAuxSFXAtEntity(null, 1017, (int) posX, (int) posY, (int) posZ, 0);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("VillagerType", getType());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setType(nbt.getInteger("VillagerType"));
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		setType(worldObj.rand.nextInt(6));
		return super.onSpawnWithEgg(data);
	}

	@Override
	protected String getLivingSound() {
		return Tags.MC_ASSET_VER + ":entity.zombie_villager.ambient";
	}

	@Override
	protected String getHurtSound() {
		return Tags.MC_ASSET_VER + ":entity.zombie_villager.hurt";
	}

	@Override
	protected String getDeathSound() {
		return Tags.MC_ASSET_VER + ":entity.zombie_villager.death";
	}

	/**
	 * MCP name: {@code playStepSound}
	 */
	@Override
	protected void func_145780_a(int x, int y, int z, Block blockIn) {
		this.playSound(Tags.MC_ASSET_VER + ":entity.zombie_villager.step", 0.15F, 1.0F);
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		return ModEntityList.getEggFromEntity(this);
	}
}