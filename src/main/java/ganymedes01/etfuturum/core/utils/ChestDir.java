package ganymedes01.etfuturum.core.utils;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;
import org.joml.Vector3ic;

public enum ChestDir {
    NONE(new BlockPos(0, 0, 0)),
    POS_Z(new BlockPos(0, 0, 1)),
    NEG_Z(new BlockPos(0, 0, -1)),
    POS_X(new BlockPos(1, 0, 0)),
    NEG_X(new BlockPos(-1, 0, 0)),
    ;

    public static final ChestDir[] VALUES = values();
    public static final ChestDir[] VALID_CONNECTIONS = { POS_Z, NEG_Z, POS_X, NEG_X };

    private final BlockPos offset;

    ChestDir(BlockPos offset) {
        this.offset = offset;
    }

    /// @param meta `0b..._E???`
    /// @return whether E is set
    public static boolean hasConnectionMeta(int meta) {
        return (meta & 0b1000) != 0;
    }

    /// @param meta `0b..._?CCC_E???`, where `C` are the connection bits
    /// @return [ChestDir] from `CCC` if `E` is set, otherwise [NONE]
    public static ChestDir fromConnectionMeta(int meta) {
        if (!hasConnectionMeta(meta)) {
            return NONE;
        }
        int ordinal = (meta & 0b111_0000) >> 4;
        if (ordinal >= VALUES.length) {
            return NONE;
        }
        return VALUES[ordinal];
    }

    /// Writes the [ChestDir] ordinal into `CCC`.<br>
    /// Sets the modern connections enabled bit `E`.<br>
    /// Leaves the rest of the bits unchanged.
    ///
    /// @param meta `0b..._?CCC_E???`
    public int toConnectionMeta(int meta) {
        return (meta & ~0b111_1000) | (ordinal() << 4) | 0b1000;
    }

    /// @param meta `0b..._?SSS`, where `SSS` is an ordinal of [ForgeDirection]
    public static ChestDir fromSideMeta(int meta) {
        return switch (meta & 0b111) {
            case 2 -> POS_Z;
            case 3 -> NEG_Z;
            case 4 -> POS_X;
            case 5 -> NEG_X;
            default -> NONE;
        };
    }

    /// Writes the ordinal of the [ForgeDirection] equivalent of this [ChestDir] into `SSS`.<br>
    /// [NONE] is mapped to [ForgeDirection#UNKNOWN].<br>
    /// Leaves the rest of the bits unchanged.
    ///
    /// @param meta `0b..._?SSS`
    public int toSideMeta(int meta) {
        int dirOrdinal = switch (this) {
            case POS_Z -> 2;
            case NEG_Z -> 3;
            case POS_X -> 4;
            case NEG_X -> 5;
            default -> 0;
        };
        return (meta & ~0b111) | dirOrdinal;
    }

    /// Turns the connection dir 180°. [NONE] is not turned.
    public ChestDir flip() {
        return switch (this) {
            case NONE -> NONE;
            case POS_Z -> NEG_Z;
            case NEG_Z -> POS_Z;
            case POS_X -> NEG_X;
            case NEG_X -> POS_X;
        };
    }

    /// Turns the connection dir 90°. [NONE] is not turned.
    public ChestDir turn() {
        return switch (this) {
            case NONE -> NONE;
            case POS_Z -> NEG_X;
            case NEG_X -> NEG_Z;
            case NEG_Z -> POS_X;
            case POS_X -> POS_Z;
        };
    }

    /// @return whether `other` is orthogonal to `this`. [NONE] is only orthogonal to [NONE]
    public boolean orthogonal(ChestDir other) {
        ChestDir turn = other.turn();
        return this == turn || this == turn.flip();
    }

    /// @return a unit vector in the direction of `this`, or the zero vector if `this` is [NONE]
    public Vector3ic toOffset() {
        return offset;
    }

    /// @return whether `this` is positive on its axis. [NONE] is not positive
    public boolean isPos() {
        return switch (this) {
            case NONE, NEG_Z, NEG_X -> false;
            case POS_Z, POS_X -> true;
        };
    }
}
