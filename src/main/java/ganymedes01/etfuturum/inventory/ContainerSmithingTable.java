package ganymedes01.etfuturum.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ganymedes01.etfuturum.inventory.slot.SlotSmithingResult;
import ganymedes01.etfuturum.recipes.SmithingTableRecipes;

public class ContainerSmithingTable extends Container {

    private final World world;
    public final InventoryCrafting inputMatrix = new InventoryCrafting(this, 2, 1);
    private final InventoryCraftResult resultInventory = new InventoryCraftResult();
    private final Slot applicant = new Slot(inputMatrix, 0, 27, 47);
    private final Slot ingot = new Slot(inputMatrix, 1, 76, 47);
    private final Slot result;

    public ContainerSmithingTable(InventoryPlayer inv, World world) {
        this.world = world;
        addSlotToContainer(applicant);
        addSlotToContainer(ingot);
        result = addSlotToContainer(new SlotSmithingResult(inv.player, inputMatrix, resultInventory, 2, 134, 47));
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 9; x++) addSlotToContainer(new Slot(inv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
        for (int y = 0; y < 9; y++) addSlotToContainer(new Slot(inv, y, 8 + y * 18, 142));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        if (!world.isRemote) {
            for (int i = 0; i < inputMatrix.getSizeInventory(); i++) {
                final ItemStack stack = inputMatrix.getStackInSlotOnClosing(i);
                if (stack != null) player.dropPlayerItemWithRandomChoice(stack, false);
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        final Object s = inventorySlots.get(index);
        if (!(s instanceof Slot)) return null;
        final Slot slot = (Slot) s;
        if (!slot.getHasStack()) return null;
        final ItemStack newStack = slot.getStack();
        final ItemStack oldStack = newStack.copy();
        boolean merged = false;
        if (index <= 2) {
            merged = mergeItemStack(newStack, 3, 39, true);
        } else if (index < 30) {
            merged = tryUpper(newStack) || mergeItemStack(newStack, 30, 39, false);
        } else {
            merged = tryUpper(newStack) || mergeItemStack(newStack, 3, 30, false);
        }
        if (!merged) return null;
        if (newStack.stackSize == 0) {
            slot.putStack(null);
        } else slot.onSlotChanged();
        slot.onPickupFromSlot(player, newStack);
        return oldStack;
    }

    @Override
    public void onCraftMatrixChanged(IInventory inv) {
        resultInventory.setInventorySlotContents(
            0,
            SmithingTableRecipes.getInstance()
                .findMatchingRecipe(this.inputMatrix, this.world));
        super.onCraftMatrixChanged(inv);
    }

    private boolean tryUpper(ItemStack newStack) {
        return !applicant.getHasStack() && mergeItemStack(newStack, 1, 1, false)
            || !ingot.getHasStack() && mergeItemStack(newStack, 0, 2, false);
    }

    public boolean unable() {
        return (applicant.getHasStack() || ingot.getHasStack()) && !result.getHasStack();
    }
}
