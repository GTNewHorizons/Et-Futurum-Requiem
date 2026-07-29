package ganymedes01.etfuturum.entities;

import ganymedes01.etfuturum.ModItems;
import net.minecraft.entity.EntityAgeable;
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
import net.minecraft.world.World;

public class EntityPanda extends EntityAnimal {

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

	@Override
	public EntityPanda createChild(EntityAgeable mate) {
		return new EntityPanda(worldObj);
	}
}
