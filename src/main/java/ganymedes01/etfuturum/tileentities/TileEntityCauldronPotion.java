package ganymedes01.etfuturum.tileentities;

import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityCauldronPotion extends TileEntityCauldronColoredWater {

    public ItemStack potion;

    @Override
    public int getWaterColor() {
        if (worldObj.isRemote && potion != null && potion.getItem() instanceof ItemPotion) {
            return Items.potionitem.getColorFromDamage(potion.getItemDamage());
        }
        return 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.potion = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("Potion"));

        if (potion == null || !(potion.getItem() instanceof ItemPotion)) {
            System.err.println(
                "Cauldron @ " + xCoord
                    + " "
                    + yCoord
                    + " "
                    + zCoord
                    + " had an invalid potion ItemStack. Resetting to a normal cauldron.");
            resetCauldron();
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();

        this.writeToNBT(nbt);

        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (potion != null) {
            compound.setTag("Potion", this.potion.writeToNBT(new NBTTagCompound()));
        }
    }
}
