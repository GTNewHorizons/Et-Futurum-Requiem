package ganymedes01.etfuturum.world;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class WorldCoord implements Comparable<WorldCoord> {

    public int x;
    public int y;
    public int z;

    public WorldCoord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public WorldCoord(TileEntity tile) {
        x = tile.xCoord;
        y = tile.yCoord;
        z = tile.zCoord;
    }

    public WorldCoord add(ForgeDirection dir) {
        return new WorldCoord(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(x)
            .append(y)
            .append(z)
            .hashCode();
    }

    @Override
    public int compareTo(WorldCoord wc) {
        int legthThis = x * x + y * y + z * z;
        int legthOther = wc.x * wc.x + wc.y * wc.y + wc.z * wc.z;

        return Integer.compare(legthThis, legthOther);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WorldCoord wc)) return false;

        return x == wc.x && y == wc.y && z == wc.z;
    }

    @Override
    public String toString() {
        return "Coord: " + x + ", " + y + ", " + z;
    }
}
