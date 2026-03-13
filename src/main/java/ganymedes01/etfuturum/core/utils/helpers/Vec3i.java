package ganymedes01.etfuturum.core.utils.helpers;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import com.google.common.base.Objects;

public class Vec3i implements Comparable<Vec3i> {

    private final int x, y, z;

    public Vec3i(int xIn, int yIn, int zIn) {
        x = xIn;
        y = yIn;
        z = zIn;
    }

    public Vec3i(double xIn, double yIn, double zIn) {
        this(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
    }

    public Vec3i(BlockPos blockpos) {
        this(blockpos.getX(), blockpos.getY(), blockpos.getZ());
    }

    public Vec3i(Vec3 vec) {
        this(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) return true;
        if (!(p_equals_1_ instanceof Vec3i vec3i)) return false;
        return getX() == vec3i.getX() && getY() == vec3i.getY() && getZ() == vec3i.getZ();
    }

    @Override
    public int hashCode() {
        return (getY() + getZ() * 31) * 31 + getX();
    }

    @Override
    public int compareTo(Vec3i vec) {
        return getY() == vec.getY() ? getZ() == vec.getZ() ? getX() - vec.getX() : getZ() - vec.getZ()
            : getY() - vec.getY();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Vec3i crossProduct(Vec3i vec) {
        return new Vec3i(
            getY() * vec.getZ() - getZ() * vec.getY(),
            getZ() * vec.getX() - getX() * vec.getZ(),
            getX() * vec.getY() - getY() * vec.getX());
    }

    public boolean isWithinDistance(Vec3i vec, double distance) {
        return this.getSquaredDistance(vec.getX(), vec.getY(), vec.getZ(), false) < distance * distance;
    }

    public double getSquaredDistance(Vec3i vec) {
        return this.getSquaredDistance(vec.getX(), vec.getY(), vec.getZ(), true);
    }

    public double getSquaredDistance(Vec3 vec) {
        return this.getSquaredDistance(vec.xCoord, vec.yCoord, vec.zCoord, true);
    }

    public double getSquaredDistance(Vec3i vec, boolean treatAsBlockPos) {
        return this.getSquaredDistance(vec.x, vec.y, vec.z, treatAsBlockPos);
    }

    public double getSquaredDistance(double x, double y, double z, boolean treatAsBlockPos) {
        double d = treatAsBlockPos ? 0.5D : 0.0D;
        double e = (double) this.getX() + d - x;
        double f = (double) this.getY() + d - y;
        double g = (double) this.getZ() + d - z;
        return e * e + f * f + g * g;
    }

    public int manhattanDistance(Vec3i p_218139_1_) {
        float f = (float) Math.abs(p_218139_1_.getX() - this.getX());
        float f1 = (float) Math.abs(p_218139_1_.getY() - this.getY());
        float f2 = (float) Math.abs(p_218139_1_.getZ() - this.getZ());
        return (int) (f + f1 + f2);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("x", getX())
            .add("y", getY())
            .add("z", getZ())
            .toString();
    }
}
