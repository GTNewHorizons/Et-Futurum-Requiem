package ganymedes01.etfuturum.api.mappings;

import net.minecraft.world.World;

import ganymedes01.etfuturum.api.MultiBlockSoundRegistry;

/**
 * For floats, set to -1 for no volume/pitch override, and for strings set to null for no soundType override.
 * If all 3 are no override, the custom sound event is not used.
 */
public abstract class MultiBlockSoundContainer {

    /**
     * NOTE: If the block whose sound you are overriding has the same break and place sound, type will be BREAK instead
     * of PLACE!.
     * There is NO WAY to differentiate placing a block and breaking it when they use the same noise.
     */
    public abstract String getSound(World world, int x, int y, int z, String oldSoundIn,
        MultiBlockSoundRegistry.BlockSoundType type);

    public float getVolume(World world, int x, int y, int z, float volume,
        MultiBlockSoundRegistry.BlockSoundType type) {
        return -1;
    }

    public float getPitch(World world, int x, int y, int z, float pitch, MultiBlockSoundRegistry.BlockSoundType type) {
        return -1;
    }
}
