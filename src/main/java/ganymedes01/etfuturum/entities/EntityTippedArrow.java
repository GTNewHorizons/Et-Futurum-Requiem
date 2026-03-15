package ganymedes01.etfuturum.entities;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import ganymedes01.etfuturum.ModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;


import java.awt.Color;
import java.io.IOException;

public class EntityTippedArrow extends EntityArrow implements IEntityAdditionalSpawnData {

	private ItemStack arrow;

	public EntityTippedArrow(World world) {
		super(world);
	}

	public EntityTippedArrow(World world, EntityLivingBase entity, float f0) {
		super(world, entity, f0);
	}

	public EntityTippedArrow(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityTippedArrow(World world, EntityLivingBase entity, EntityLivingBase target, float f0, float f1) {
		super(world, entity, target, f0, f1);
	}

	public void setArrow(ItemStack effect) {
		this.arrow = effect;
	}

	public ItemStack getArrow() {
		return arrow;
	}

	private boolean isEffectValid() {
		return arrow != null;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.worldObj.isRemote) {
			if (this.inGround) {
				if (this.ticksInGround % 5 == 0) {
					this.spawnPotionParticles(1);
				}
			} else {
				this.spawnPotionParticles(2);
			}
		}
	}

	private void spawnPotionParticles(int particleCount) {
		if (isEffectValid() && particleCount > 0) {
			Color colour = new Color(ModItems.TIPPED_ARROW.get().getColorFromItemStack(arrow, 0));

			for (int j = 0; j < particleCount; ++j) {
				this.worldObj.spawnParticle("mobSpell", this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height - (double) this.yOffset, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, colour.getRed() / 255F, colour.getGreen() / 255F, colour.getBlue() / 255F);
			}
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tagCompund) {
		super.readEntityFromNBT(tagCompund);

		if (tagCompund.hasKey("Potion", 10)) {
			this.arrow = ItemStack.loadItemStackFromNBT(tagCompund.getCompoundTag("Potion"));
		} else {
			this.setPotionDamage(tagCompund.getInteger("potionValue"));
		}

		if (this.arrow == null) {
			this.setDead();
		}
	}

	public void setPotionDamage(int p_82340_1_) {
		if (this.arrow == null) {
			this.arrow = ModItems.TIPPED_ARROW.newItemStack(1, 0);
		}

		this.arrow.setItemDamage(p_82340_1_);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	public void writeEntityToNBT(NBTTagCompound tagCompound) {
		super.writeEntityToNBT(tagCompound);

		if (this.arrow != null) {
			tagCompound.setTag("Potion", this.arrow.writeToNBT(new NBTTagCompound()));
		}
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		if (!worldObj.isRemote && inGround && arrowShake <= 0 && isEffectValid()) {
			boolean flag = canBePickedUp == 1 || canBePickedUp == 2 && player.capabilities.isCreativeMode;

			ItemStack stack = arrow.copy();
			stack.stackSize = 1;

			if (canBePickedUp == 1 && !player.inventory.addItemStackToInventory(stack))
				flag = false;

			if (flag) {
				playSound("random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				player.onItemPickup(this, 1);
				setDead();
			}
		}
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeFloat(rotationYaw);

		int id = shootingEntity == null ? getEntityId() : shootingEntity.getEntityId();
		buffer.writeInt(id);

		buffer.writeDouble(motionX);
		buffer.writeDouble(motionY);
		buffer.writeDouble(motionZ);

		PacketBuffer pb = new PacketBuffer(buffer);

		try {
			pb.writeItemStackToBuffer(arrow);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		rotationYaw = buffer.readFloat();
		shootingEntity = worldObj.getEntityByID(buffer.readInt());

		motionX = buffer.readDouble();
		motionY = buffer.readDouble();
		motionZ = buffer.readDouble();

		posX -= MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
		posY -= 0.10000000149011612D;
		posZ -= MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * 0.16F;

		PacketBuffer pb = new PacketBuffer(buffer);

		try {
			arrow = pb.readItemStackFromBuffer();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
