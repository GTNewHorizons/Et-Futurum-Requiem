package ganymedes01.etfuturum.entities;

import ganymedes01.etfuturum.Tags;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityHusk extends EntityZombie {

	/*
	 * We set this to true to force the brightness to be 0 in onLivingUpdate so the husk does not burn.
	 * It is set back to false after onLivingUpdate, which is nested in onUpdate, so it will not affect
	 * what other code sees when it gets the entity brightness.
	 */
	private boolean ignoreSunlight;

	public EntityHusk(final World p_i1745_1_) {
		super(p_i1745_1_);
		getNavigator().setBreakDoors(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
		this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0D, true));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
		this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, false));
		setSize(0.6F, 1.8F);
	}


	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		final boolean flag = super.attackEntityAsMob(entity);
		if (flag) {
			final int i = this.worldObj.difficultySetting.getDifficultyId();
			if (this.getHeldItem() == null && this.isBurning() && this.rand.nextFloat() < i * 0.3f) {
				entity.setFire(0);
			}
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.hunger.id, 140 * i, 0));
		}
		return flag;
	}

	@Override
	public void onLivingUpdate() {
		ignoreSunlight = true;
		super.onLivingUpdate();
		ignoreSunlight = false;
	}

	@Override
	public float getBrightness(float p_70013_1_) {
		return ignoreSunlight ? 0F : super.getBrightness(p_70013_1_);
	}
	
	/*
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (!super.attackEntityFrom(source, amount))
		{
			return false;
		}
		else
		{
			EntityLivingBase entitylivingbase = this.getAttackTarget();

			if (entitylivingbase == null && this.getEntityToAttack() instanceof EntityLivingBase)
			{
				entitylivingbase = (EntityLivingBase)this.getEntityToAttack();
			}

			if (entitylivingbase == null && source.getEntity() instanceof EntityLivingBase)
			{
				entitylivingbase = (EntityLivingBase)source.getEntity();
			}


			int i = MathHelper.floor_double(this.posX);
			int j = MathHelper.floor_double(this.posY);
			int k = MathHelper.floor_double(this.posZ);

			SummonAidEvent summonAid = ForgeEventFactory.fireZombieSummonAid(this, worldObj, i, j, k, entitylivingbase, this.getEntityAttribute(field_110186_bp).getAttributeValue());
			
			if (summonAid.getResult() == Result.DENY)
			{
				return true;
			}
			else if (summonAid.getResult() == Result.ALLOW || entitylivingbase != null && this.worldObj.difficultySetting == EnumDifficulty.HARD && (double)this.rand.nextFloat() < this.getEntityAttribute(field_110186_bp).getAttributeValue())
			{
				EntityHusk entityzombie;
				if (summonAid.customSummonedAid != null && summonAid.getResult() == Result.ALLOW)
				{
					entityzombie = (EntityHusk) summonAid.customSummonedAid;
				}
				else
				{
					entityzombie = new EntityHusk(this.worldObj);
				}

				for (int l = 0; l < 50; ++l)
				{
					int i1 = i + MathHelper.getRandomIntegerInRange(this.rand, 7, 40) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
					int j1 = j + MathHelper.getRandomIntegerInRange(this.rand, 7, 40) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);
					int k1 = k + MathHelper.getRandomIntegerInRange(this.rand, 7, 40) * MathHelper.getRandomIntegerInRange(this.rand, -1, 1);

					if (World.doesBlockHaveSolidTopSurface(this.worldObj, i1, j1 - 1, k1) && this.worldObj.getBlockLightValue(i1, j1, k1) < 10)
					{
						entityzombie.setPosition((double)i1, (double)j1, (double)k1);

						if (this.worldObj.checkNoEntityCollision(entityzombie.boundingBox) && this.worldObj.getCollidingBoundingBoxes(entityzombie, entityzombie.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(entityzombie.boundingBox))
						{
							this.worldObj.spawnEntityInWorld(entityzombie);
							if (entitylivingbase != null) entityzombie.setAttackTarget(entitylivingbase);
							entityzombie.onSpawnWithEgg((IEntityLivingData)null);
							this.getEntityAttribute(field_110186_bp).applyModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806D, 0));
							entityzombie.getEntityAttribute(field_110186_bp).applyModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806D, 0));
							break;
						}
					}
				}
			}

			return true;
		}
	}
	*/

	@Override
	protected String getLivingSound() {
		return Tags.MC_ASSET_VER + ":entity.husk.ambient";
	}

	@Override
	protected String getHurtSound() {
		return Tags.MC_ASSET_VER + ":entity.husk.hurt";
	}

	@Override
	protected String getDeathSound() {
		return Tags.MC_ASSET_VER + ":entity.husk.death";
	}

	/**
	 * MCP name: {@code playStepSound}
	 */
	@Override
	protected void func_145780_a(final int x, final int y, final int z, final Block blockIn) {
		this.playSound(Tags.MC_ASSET_VER + ":entity.husk.step", 0.15f, 1.0f);
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		return ModEntityList.getEggFromEntity(this);
	}
}
