package ganymedes01.etfuturum.entities;

import java.util.ArrayList;

import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class EntityNewSnowGolem extends EntitySnowman implements IShearable {

    private static final int HAS_PUMPKIN = 12;

    public EntityNewSnowGolem(World world) {
        super(world);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(HAS_PUMPKIN, (byte) 1);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        dataWatcher.updateObject(HAS_PUMPKIN, nbt.getBoolean("HasPumpkin") ? (byte) 1 : (byte) 0);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("HasPumpkin", dataWatcher.getWatchableObjectByte(HAS_PUMPKIN) == 1);
    }

    @Override
    public boolean isShearable(ItemStack stack, IBlockAccess world, int x, int y, int z) {
        return hasPumpkin();
    }

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack stack, IBlockAccess world, int x, int y, int z, int fortune) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(new ItemStack(Blocks.pumpkin));
        dataWatcher.updateObject(HAS_PUMPKIN, (byte) 0);
        return list;
    }

    public boolean hasPumpkin() {
        return dataWatcher.getWatchableObjectByte(HAS_PUMPKIN) == (byte) 1;
    }
}
