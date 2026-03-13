package ganymedes01.etfuturum.core.utils;

import net.minecraft.util.EnumFacing;

public enum Mirror {

    NONE("no_mirror"),
    LEFT_RIGHT("mirror_left_right"),
    FRONT_BACK("mirror_front_back");

    private final String name;
    private static final String[] mirrorNames = new String[values().length];

    Mirror(String nameIn) {
        this.name = nameIn;
    }

    /**
     * Mirrors the given rotation like specified by this mirror. rotations start at 0 and go up to rotationCount-1. 0 is
     * front, rotationCount/2 is back.
     */
    public int mirrorRotation(int rotationIn, int rotationCount) {
        int i = rotationCount / 2;
        int j = rotationIn > i ? rotationIn - rotationCount : rotationIn;

        switch (this) {
            case LEFT_RIGHT:
                return (rotationCount - j) % rotationCount;

            case FRONT_BACK:
                return (i - j + rotationCount) % rotationCount;

            default:
                return rotationIn;
        }
    }

    /**
     * Determines the rotation that is equivalent to this mirror if the rotating object faces in the given direction
     */
    public Rotation toRotation(EnumFacing facing) {
        return (this != LEFT_RIGHT || facing.getFrontOffsetZ() == 0)
            && (this != FRONT_BACK || facing.getFrontOffsetX() == 0) ? Rotation.NONE : Rotation.CLOCKWISE_180;
    }

    /**
     * Mirror the given facing according to this mirror
     */
    public EnumFacing mirror(EnumFacing facing) {
        switch (this) {
            case LEFT_RIGHT:
                if (facing == EnumFacing.WEST) {
                    return EnumFacing.EAST;
                } else {
                    if (facing == EnumFacing.EAST) {
                        return EnumFacing.WEST;
                    }

                    return facing;
                }

            case FRONT_BACK:
                if (facing == EnumFacing.NORTH) {
                    return EnumFacing.SOUTH;
                } else {
                    if (facing == EnumFacing.SOUTH) {
                        return EnumFacing.NORTH;
                    }

                    return facing;
                }

            default:
                return facing;
        }
    }

    static {
        int i = 0;

        for (Mirror mirror : values()) {
            mirrorNames[i++] = mirror.name;
        }
    }
}
