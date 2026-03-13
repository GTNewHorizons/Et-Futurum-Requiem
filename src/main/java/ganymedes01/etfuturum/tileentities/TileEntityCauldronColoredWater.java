package ganymedes01.etfuturum.tileentities;

import net.minecraft.init.Blocks;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityCauldronColoredWater extends TileEntity {

    public abstract int getWaterColor();

    @Override
    public boolean canUpdate() {
        return false;
    }

    protected void resetCauldron() {
        worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.cauldron, 0, 3);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g()); // getNbtCompound
    }
}
