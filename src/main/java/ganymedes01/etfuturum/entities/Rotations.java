package ganymedes01.etfuturum.entities;

import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;

public class Rotations {

    protected final float x;
    protected final float y;
    protected final float z;

    public Rotations(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Rotations(NBTTagList nbt) {
        x = nbt.func_150308_e(0); // getFloatAt
        y = nbt.func_150308_e(1); // getFloatAt
        z = nbt.func_150308_e(2); // getFloatAt
    }

    public NBTTagList writeToNBT() {
        NBTTagList nbttaglist = new NBTTagList();
        nbttaglist.appendTag(new NBTTagFloat(x));
        nbttaglist.appendTag(new NBTTagFloat(y));
        nbttaglist.appendTag(new NBTTagFloat(z));
        return nbttaglist;
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (!(p_equals_1_ instanceof Rotations rotations)) return false;
        return x == rotations.x && y == rotations.y && z == rotations.z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    @Override
    public int hashCode() {
        return (int) (x + y + z);
    }
}
