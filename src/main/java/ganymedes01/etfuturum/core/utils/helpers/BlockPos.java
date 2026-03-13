package ganymedes01.etfuturum.core.utils.helpers;

import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.AbstractIterator;

public class BlockPos extends Vec3i {

    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);

    private static final int NUM_X_BITS = 26;
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
    private static final int Y_SHIFT = NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

    public BlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    public BlockPos(double x, double y, double z) {
        super(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
    }

    public BlockPos(Entity source) {
        this(source.posX, source.posY, source.posZ);
    }

    public BlockPos(Vec3 source) {
        this(source.xCoord, source.yCoord, source.zCoord);
    }

    public BlockPos(Vec3i source) {
        this(source.getX(), source.getY(), source.getZ());
    }

    public BlockPos(ChunkCoordinates coords) {
        this(coords.posX, coords.posY, coords.posZ);
    }

    public BlockPos add(double x, double y, double z) {
        return new BlockPos(getX() + x, getY() + y, getZ() + z);
    }

    public BlockPos add(int x, int y, int z) {
        return new BlockPos(getX() + x, getY() + y, getZ() + z);
    }

    public BlockPos add(Vec3i vec) {
        return new BlockPos(getX() + vec.getX(), getY() + vec.getY(), getZ() + vec.getZ());
    }

    public BlockPos multiply(int factor) {
        return new BlockPos(getX() * factor, getY() * factor, getZ() * factor);
    }

    public BlockPos up() {
        return this.up(1);
    }

    public BlockPos subtract(Vec3i vec) {
        return new BlockPos(getX() - vec.getX(), getY() - vec.getY(), getZ() - vec.getZ());
    }

    public BlockPos up(int n) {
        return this.offset(EnumFacing.UP, n);
    }

    public BlockPos down() {
        return this.down(1);
    }

    public BlockPos down(int n) {
        return this.offset(EnumFacing.DOWN, n);
    }

    public BlockPos north() {
        return this.north(1);
    }

    public BlockPos north(int n) {
        return this.offset(EnumFacing.NORTH, n);
    }

    public BlockPos south() {
        return this.south(1);
    }

    public BlockPos south(int n) {
        return this.offset(EnumFacing.SOUTH, n);
    }

    public BlockPos west() {
        return this.west(1);
    }

    public BlockPos west(int n) {
        return this.offset(EnumFacing.WEST, n);
    }

    public BlockPos east() {
        return this.east(1);
    }

    public BlockPos east(int n) {
        return this.offset(EnumFacing.EAST, n);
    }

    public BlockPos offset(EnumFacing facing) {
        return this.offset(facing, 1);
    }

    public BlockPos offset(EnumFacing facing, int n) {
        return new BlockPos(
            getX() + facing.getFrontOffsetX() * n,
            getY() + facing.getFrontOffsetY() * n,
            getZ() + facing.getFrontOffsetZ() * n);
    }

    public BlockPos offset(ForgeDirection facing) {
        return this.offset(facing, 1);
    }

    public BlockPos offset(ForgeDirection facing, int n) {
        return new BlockPos(getX() + facing.offsetX * n, getY() + facing.offsetY * n, getZ() + facing.offsetZ * n);
    }

    public BlockPos crossProductBP(Vec3i vec) {
        return new BlockPos(
            getY() * vec.getZ() - getZ() * vec.getY(),
            getZ() * vec.getX() - getX() * vec.getZ(),
            getX() * vec.getY() - getY() * vec.getX());
    }

    public long toLong() {
        return (getX() & X_MASK) << X_SHIFT | (getY() & Y_MASK) << Y_SHIFT | (getZ() & Z_MASK) << 0;
    }

    public static BlockPos fromLong(long serialized) {
        int j = (int) (serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
        int k = (int) (serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
        int l = (int) (serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
        return new BlockPos(j, k, l);
    }

    @Override
    public Vec3i crossProduct(Vec3i vec) {
        return crossProductBP(vec);
    }

    // Roadhog360 start
    public static AxisAlignedBB getBB(BlockPos pos1, BlockPos pos2) {
        return AxisAlignedBB
            .getBoundingBox(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
    }

    public static Iterable<BlockPos> iterate(BlockPos start, BlockPos end) {
        return iterate(
            Math.min(start.getX(), end.getX()),
            Math.min(start.getY(), end.getY()),
            Math.min(start.getZ(), end.getZ()),
            Math.max(start.getX(), end.getX()),
            Math.max(start.getY(), end.getY()),
            Math.max(start.getZ(), end.getZ()));
    }

    public static BlockPos readFromNBT(NBTTagCompound p_129240_) {
        return new BlockPos(p_129240_.getInteger("X"), p_129240_.getInteger("Y"), p_129240_.getInteger("Z"));
    }

    public static NBTTagCompound writeToNBT(BlockPos p_129225_) {
        NBTTagCompound compoundtag = new NBTTagCompound();
        compoundtag.setInteger("X", p_129225_.getX());
        compoundtag.setInteger("Y", p_129225_.getY());
        compoundtag.setInteger("Z", p_129225_.getZ());
        return compoundtag;
    }

    public TileEntity getTileEntity(World world) {
        return world.getTileEntity(getX(), getY(), getZ());
    }

    public Block getBlock(World world) {
        return world.getBlock(getX(), getY(), getZ());
    }

    public int getBlockMetadata(World world) {
        return world.getBlockMetadata(getX(), getY(), getZ());
    }

    public static Iterable<BlockPos> iterate(final int startX, final int startY, final int startZ, final int endX,
        final int endY, final int endZ) {

        return new Iterable<BlockPos>() {

            public Iterator<BlockPos> iterator() {
                return new AbstractIterator<BlockPos>() {

                    private boolean first = true;
                    private int lastPosX;
                    private int lastPosY;
                    private int lastPosZ;

                    @Override
                    protected BlockPos computeNext() {
                        if (this.first) {
                            this.first = false;
                            this.lastPosX = startX;
                            this.lastPosY = startY;
                            this.lastPosZ = startZ;
                            return new BlockPos(startX, startY, startZ);
                        } else if (this.lastPosX == endX && this.lastPosY == endY && this.lastPosZ == endZ) {
                            return this.endOfData();
                        } else {
                            if (this.lastPosX < endX) {
                                ++this.lastPosX;
                            } else if (this.lastPosY < endY) {
                                this.lastPosX = startX;
                                ++this.lastPosY;
                            } else if (this.lastPosZ < endZ) {
                                this.lastPosX = startX;
                                this.lastPosY = startY;
                                ++this.lastPosZ;
                            }

                            return new BlockPos(this.lastPosX, this.lastPosY, this.lastPosZ);
                        }
                    }
                };
            }
        };
    }

    public BlockPos toImmutable() {
        return this;
    }

    public Vec3 newVec3() {
        return Vec3.createVectorHelper(getX(), getY(), getZ());
    }

    public static class MutableBlockPos extends BlockPos {

        protected int x;
        protected int y;
        protected int z;

        public MutableBlockPos() {
            this(0, 0, 0);
        }

        public MutableBlockPos(BlockPos pos) {
            this(pos.getX(), pos.getY(), pos.getZ());
        }

        public MutableBlockPos(int x_, int y_, int z_) {
            super(0, 0, 0);
            this.x = x_;
            this.y = y_;
            this.z = z_;
        }

        @Override
        public BlockPos add(double x, double y, double z) {
            return super.add(x, y, z).toImmutable();
        }

        @Override
        public BlockPos add(int x, int y, int z) {
            return super.add(x, y, z).toImmutable();
        }

        @Override
        public BlockPos offset(EnumFacing facing, int n) {
            return super.offset(facing, n).toImmutable();
        }

        @Override
        public BlockPos offset(EnumFacing p_190942_1_) {
            return super.offset(p_190942_1_).toImmutable();
        }

        @Override
        public int getX() {
            return this.x;
        }

        @Override
        public int getY() {
            return this.y;
        }

        @Override
        public int getZ() {
            return this.z;
        }

        public BlockPos.MutableBlockPos setPos(int xIn, int yIn, int zIn) {
            this.x = xIn;
            this.y = yIn;
            this.z = zIn;
            return this;
        }

        public BlockPos.MutableBlockPos setPos(Entity entityIn) {
            return this.setPos(entityIn.posX, entityIn.posY, entityIn.posZ);
        }

        public BlockPos.MutableBlockPos setPos(double xIn, double yIn, double zIn) {
            return this
                .setPos(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
        }

        public BlockPos.MutableBlockPos setWithOffset(Vec3i p_122160_, ForgeDirection p_122161_) {
            return setPos(
                p_122160_.getX() + p_122161_.offsetX,
                p_122160_.getY() + p_122161_.offsetY,
                p_122160_.getZ() + p_122161_.offsetZ);
        }

        public BlockPos.MutableBlockPos setWithOffset(Vec3i p_122155_, int p_122156_, int p_122157_, int p_122158_) {
            return setPos(p_122155_.getX() + p_122156_, p_122155_.getY() + p_122157_, p_122155_.getZ() + p_122158_);
        }

        public BlockPos.MutableBlockPos setWithOffset(Vec3i p_175307_, Vec3i p_175308_) {
            return setPos(
                p_175307_.getX() + p_175308_.getX(),
                p_175307_.getY() + p_175308_.getY(),
                p_175307_.getZ() + p_175308_.getZ());
        }

        public BlockPos.MutableBlockPos setPos(Vec3i vec) {
            return this.setPos(vec.getX(), vec.getY(), vec.getZ());
        }

        public BlockPos.MutableBlockPos move(EnumFacing facing) {
            return this.move(facing, 1);
        }

        public BlockPos.MutableBlockPos move(EnumFacing facing, int p_189534_2_) {
            return this.setPos(
                this.x + facing.getFrontOffsetX() * p_189534_2_,
                this.y + facing.getFrontOffsetY() * p_189534_2_,
                this.z + facing.getFrontOffsetZ() * p_189534_2_);
        }

        public void setY(int yIn) {
            this.y = yIn;
        }

        @Override
        public BlockPos toImmutable() {
            return new BlockPos(this);
        }
    }
}
