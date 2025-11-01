package ganymedes01.etfuturum.tileentities;

import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.blocks.BlockBarrel;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import java.util.Iterator;
import java.util.List;

public class TileEntityBarrel extends TileEntity implements IInventory {
	private int ticksSinceSync;
	private float soundTimer;
	private String customName;
	private ItemStack[] chestContents = new ItemStack[36];
	public int numPlayersUsing;
	//TODO: Fish barrel Easter Egg

	@Override
	public int getSizeInventory() {
		return 27;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList nbttaglist = compound.getTagList("Items", 10);
		this.chestContents = new ItemStack[this.getSizeInventory()];
		Utils.loadItemStacksFromNBT(nbttaglist, this.chestContents);

		if (compound.hasKey("CustomName", 8)) {
			this.customName = compound.getString("CustomName");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);

		compound.setTag("Items", Utils.writeItemStacksToNBT(this.chestContents));

		if (this.hasCustomInventoryName()) {
			compound.setString("CustomName", this.customName);
		}
	}

	@Override
	public ItemStack getStackInSlot(int slotIn) {
		return this.chestContents[slotIn];
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
	 * new stack.
	 */
	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (this.chestContents[index] != null) {
			ItemStack itemstack;

			if (this.chestContents[index].stackSize <= count) {
				itemstack = this.chestContents[index];
				this.chestContents[index] = null;
				this.markDirty();
				return itemstack;
			}
			itemstack = this.chestContents[index].splitStack(count);

			if (this.chestContents[index].stackSize == 0) {
				this.chestContents[index] = null;
			}

			this.markDirty();
			return itemstack;
		}
		return null;
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
	 * like when you close a workbench GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		if (this.chestContents[index] != null) {
			ItemStack itemstack = this.chestContents[index];
			this.chestContents[index] = null;
			return itemstack;
		}
		return null;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.chestContents[index] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}

		this.markDirty();
	}

	@Override
	public void updateEntity() {
		++this.ticksSinceSync;
		float f;

		if (!this.worldObj.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + this.xCoord + this.yCoord + this.zCoord) % 200 == 0) {
			this.numPlayersUsing = 0;
			f = 5.0F;
			List<EntityPlayer> list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(this.xCoord - f, this.yCoord - f, this.zCoord - f, this.xCoord + 1 + f, this.yCoord + 1 + f, this.zCoord + 1 + f));
			Iterator<EntityPlayer> iterator = list.iterator();

			while (iterator.hasNext()) {
				EntityPlayer entityplayer = iterator.next();

				if (entityplayer.openContainer instanceof ContainerChest) {
					++this.numPlayersUsing;
				}
			}
		}

		f = 0.1F;
		double d2;

		if (this.numPlayersUsing > 0 && this.soundTimer <= 0.0F) {

			double d1 = this.xCoord + 0.5D;
			d2 = this.zCoord + 0.5D;

			this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord) % 8 + 8, 2);
			this.worldObj.playSoundEffect(d1, this.yCoord + 0.5D, d2, Tags.MC_ASSET_VER + ":block.barrel.open", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);

		}

		if (this.numPlayersUsing == 0 && this.soundTimer > 0.0F || this.numPlayersUsing > 0 && this.soundTimer < 1.0F) {
			float f1 = 0.5F;

			if (this.numPlayersUsing > 0) {
				this.soundTimer += f1;
			} else {
				this.soundTimer -= f1;
			}

			if (this.soundTimer > 10) {
				this.soundTimer = 10;
			}

			if (this.soundTimer < f1 && worldObj.getBlockMetadata(xCoord, yCoord, zCoord) > 7) {
				this.worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord) % 8, 2);
				this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, Tags.MC_ASSET_VER + ":block.barrel.close", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (this.soundTimer < 0) {
				this.soundTimer = 0;
			}
		}
	}

	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : "container." + Tags.MOD_ID + ".barrel";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.customName != null && this.customName.length() > 0;
	}

	public void setCustomName(String p_145976_1_) {
		this.customName = p_145976_1_;
	}

	@Override
	public boolean receiveClientEvent(int id, int type) {
		if (id == 1) {
			this.numPlayersUsing = type;
			return true;
		}
		return super.receiveClientEvent(id, type);
	}

	@Override
	public void openInventory() {
		if (this.numPlayersUsing < 0) {
			this.numPlayersUsing = 0;
		}

		++this.numPlayersUsing;
		this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numPlayersUsing);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
	}

	@Override
	public void closeInventory() {
		if (this.getBlockType() instanceof BlockBarrel) {
			--this.numPlayersUsing;
			this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), 1, this.numPlayersUsing);
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
			this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
		}
	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	/**
	 * invalidates a tile entity
	 */
	@Override
	public void invalidate() {
		super.invalidate();
		this.updateContainingBlockInfo();
	}

}
