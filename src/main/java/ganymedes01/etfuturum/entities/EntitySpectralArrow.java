package ganymedes01.etfuturum.entities;

import ganymedes01.etfuturum.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntitySpectralArrow extends EntityArrow {

	private int duration = 200;

	public EntitySpectralArrow(World world) {
		super(world);
	}

	public EntitySpectralArrow(World world, EntityLivingBase shooter, float velocity) {
		super(world, shooter, velocity);
	}

	public EntitySpectralArrow(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (worldObj.isRemote && !inGround) {
			worldObj.spawnParticle("mobSpell",
					posX + rand.nextGaussian() * 0.12D,
					posY + rand.nextGaussian() * 0.12D,
					posZ + rand.nextGaussian() * 0.12D,
					0.9D, 0.9D, 0.4D);
		}
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("Duration")) {
			duration = compound.getInteger("Duration");
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("Duration", duration);
	}

	@Override
	public void onCollideWithPlayer(net.minecraft.entity.player.EntityPlayer player) {
		if (!worldObj.isRemote && inGround && arrowShake <= 0) {
			boolean flag = canBePickedUp == 1 || canBePickedUp == 2 && player.capabilities.isCreativeMode;

			ItemStack stack = ModItems.SPECTRAL_ARROW.newItemStack();

			if (canBePickedUp == 1 && !player.inventory.addItemStackToInventory(stack))
				flag = false;

			if (flag) {
				playSound("random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				player.onItemPickup(this, 1);
				setDead();
			}
		}
	}
}
