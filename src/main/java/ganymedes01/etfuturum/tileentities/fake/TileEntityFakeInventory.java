package ganymedes01.etfuturum.tileentities.fake;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import ganymedes01.etfuturum.api.inventory.FakeTileEntityProvider;

/**
 * Simple tile entity class for use with {@link FakeTileEntityProvider} that delegates all inventory calls
 * to the provided {@link IInventory}.
 */
public final class TileEntityFakeInventory extends TileEntity implements IInventory, ISidedInventory {

    private final ISidedInventory delegate;

    public TileEntityFakeInventory(ISidedInventory delegate) {
        this.delegate = delegate;
    }

    @Override
    public int getSizeInventory() {
        return delegate.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return delegate.getStackInSlot(slotIn);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return delegate.decrStackSize(index, count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return delegate.getStackInSlotOnClosing(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        delegate.setInventorySlotContents(index, stack);
    }

    @Override
    public String getInventoryName() {
        return delegate.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return delegate.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return delegate.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return delegate.isUseableByPlayer(player);
    }

    @Override
    public void openInventory() {
        delegate.openInventory();
    }

    @Override
    public void closeInventory() {
        delegate.closeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return delegate.isItemValidForSlot(index, stack);
    }

    @Override
    public void markDirty() {
        delegate.markDirty();
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        return delegate.getAccessibleSlotsFromSide(p_94128_1_);
    }

    @Override
    public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
        return delegate.canInsertItem(p_102007_1_, p_102007_2_, p_102007_3_);
    }

    @Override
    public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
        return delegate.canExtractItem(p_102008_1_, p_102008_2_, p_102008_3_);
    }
}
